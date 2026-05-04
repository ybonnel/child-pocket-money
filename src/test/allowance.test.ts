import { describe, it, expect, beforeEach } from 'vitest';
import 'fake-indexeddb/auto';
import { PocketMoneyDatabase } from '../data/db';
import { createChildRepository } from '../data/repositories/childRepository';
import { createTransactionRepository } from '../data/repositories/transactionRepository';
import { processDueAllowances } from '../domain/usecases/allowance';
import { Money } from '../core/money';
import type { Child } from '../domain/models';

function makeTestDb() {
  const db = new PocketMoneyDatabase('allowance-test-' + Math.random().toString(36).slice(2));
  const childRepo = createChildRepository(db);
  const txRepo = createTransactionRepository(db);
  return { db, childRepo, txRepo };
}

// A Monday
const MONDAY_2024_01_08 = new Date('2024-01-08T10:00:00');
// The next Monday
const MONDAY_2024_01_15 = new Date('2024-01-15T10:00:00');

describe('processDueAllowances', () => {
  let childRepo: ReturnType<typeof createChildRepository>;
  let txRepo: ReturnType<typeof createTransactionRepository>;

  beforeEach(() => {
    const ctx = makeTestDb();
    childRepo = ctx.childRepo;
    txRepo = ctx.txRepo;
    localStorage.removeItem('cpm.lastAllowanceCheck');
  });

  async function addChild(
    overrides: Partial<Omit<Child, 'id' | 'createdAt'>> = {},
  ): Promise<number> {
    return childRepo.add({
      name: 'Test',
      colorArgb: 0xff_e91e63,
      weeklyAllowance: Money.fromCents(1000n),
      allowanceDayOfWeek: 1, // Monday
      allowanceActive: true,
      archived: false,
      ...overrides,
    });
  }

  it('inserts allowance on the correct day', async () => {
    const id = await addChild();
    await processDueAllowances('AP', MONDAY_2024_01_08, childRepo, txRepo);

    const txs = await txRepo.listByChild(id);
    expect(txs.filter((t) => t.type === 'ALLOWANCE')).toHaveLength(1);
  });

  it('is idempotent — running twice inserts only once', async () => {
    const id = await addChild();
    await processDueAllowances('AP', MONDAY_2024_01_08, childRepo, txRepo);
    await processDueAllowances('AP', MONDAY_2024_01_08, childRepo, txRepo);

    const txs = await txRepo.listByChild(id);
    expect(txs.filter((t) => t.type === 'ALLOWANCE')).toHaveLength(1);
  });

  it('continues from last allowance date', async () => {
    const id = await addChild();
    await processDueAllowances('AP', MONDAY_2024_01_08, childRepo, txRepo);
    await processDueAllowances('AP', MONDAY_2024_01_15, childRepo, txRepo);

    const txs = await txRepo.listByChild(id);
    expect(txs.filter((t) => t.type === 'ALLOWANCE')).toHaveLength(2);
  });

  it('skips children with allowanceActive=false', async () => {
    const id = await addChild({ allowanceActive: false });
    await processDueAllowances('AP', MONDAY_2024_01_08, childRepo, txRepo);

    const txs = await txRepo.listByChild(id);
    expect(txs.filter((t) => t.type === 'ALLOWANCE')).toHaveLength(0);
  });

  it('skips children with zero allowance', async () => {
    const id = await addChild({ weeklyAllowance: Money.Zero });
    await processDueAllowances('AP', MONDAY_2024_01_08, childRepo, txRepo);

    const txs = await txRepo.listByChild(id);
    expect(txs.filter((t) => t.type === 'ALLOWANCE')).toHaveLength(0);
  });

  it('does not insert on wrong day of week', async () => {
    // allowance day = Wednesday (3)
    // Jan 8 is Monday. Window: Jan2–Jan8. There's no Wednesday in Jan2-Jan8
    // (Jan 3 = Wed? Let's use Thursday=4 to be safe)
    // Jan 8 = Mon. Window start = Jan 2 (Tue). Days: Tue,Wed,Thu,Fri,Sat,Sun,Mon
    // So Wednesday (3) = Jan 3 IS in window. Use Friday (5) = Jan 5 is in window too.
    // Use Sunday (7): Jan 7 = Sun — also in window!
    // All days from Jan 2 to Jan 8 cover all 7 weekdays. Any day will match.
    // Fix: use a window where only Monday is present.
    // Use Tuesday Jan 9 as 'today': window = Jan3-Jan9
    // allowanceDayOfWeek = Monday (1), Monday in window = Jan 8
    // allowanceDayOfWeek = Wednesday (3), Wednesday in window = Jan 3
    // So use a day where the window has no target weekday:
    // today = Wednesday Jan 3 2024: window = Dec 28 – Jan 3
    // Dec 28 = Thu, Dec 29 = Fri, Dec 30 = Sat, Dec 31 = Sun, Jan 1 = Mon, Jan 2 = Tue, Jan 3 = Wed
    // No Saturday (6) in Dec 28..Jan 3? Dec 30 = Sat. 
    // No Sunday (7)? Dec 31 = Sun.
    // Hmm — 7-day window always covers all 7 weekdays.
    // The test premise is wrong — use a 1-day window instead.
    // Let's test: child with allowance on TUESDAY, run on Monday Jan 8.
    // With continuation from last allowance:
    // - Insert a fake 'last allowance' on Sunday Jan 7, so cursor starts Jan 8 (Monday only)
    // - Monday Jan 8 is not Tuesday → no insert
    const id = await addChild({ allowanceDayOfWeek: 2 }); // Tuesday
    // Manually insert a fake prior allowance on Sunday Jan 7
    await txRepo.insert({
      childId: id,
      amount: Money.fromCents(1000n),
      label: 'fake',
      type: 'ALLOWANCE',
      occurredAt: new Date('2024-01-07'), // Sunday
      createdAt: new Date(),
    });
    // Now cursor starts Jan 8 (Monday) and goes to Jan 8 — only Monday in window
    await processDueAllowances('AP', MONDAY_2024_01_08, childRepo, txRepo);

    const txs = await txRepo.listByChild(id);
    // Only the fake Sunday insert, no new Tuesday allowance
    expect(txs.filter((t) => t.type === 'ALLOWANCE')).toHaveLength(1);
    expect(txs[0].occurredAt.getDate()).toBe(7); // Sunday Jan 7 only
  });

  it('first-run capped at 6 days back — only catches the current week', async () => {
    // allowance day = Monday, today = Monday Jan 15
    // look-back starts at Jan 9 (Jan 15 - 6 days)
    // Jan 8 is outside the window; Jan 15 is inside
    const id = await addChild({ allowanceDayOfWeek: 1 });
    await processDueAllowances('AP', MONDAY_2024_01_15, childRepo, txRepo);

    const txs = await txRepo.listByChild(id);
    const allowances = txs.filter((t) => t.type === 'ALLOWANCE');
    expect(allowances).toHaveLength(1);
    // occurredAt should be Jan 15 at midnight local time
    // Use getFullYear/month/date to avoid TZ offset issues in test
    expect(allowances[0].occurredAt.getDate()).toBe(15);
  });
});

describe('balance aggregation', () => {
  let childRepo: ReturnType<typeof createChildRepository>;
  let txRepo: ReturnType<typeof createTransactionRepository>;

  beforeEach(async () => {
    const ctx = makeTestDb();
    childRepo = ctx.childRepo;
    txRepo = ctx.txRepo;
    localStorage.removeItem('cpm.lastAllowanceCheck');
  });

  it('sums credits and debits correctly', async () => {
    const id = await childRepo.add({
      name: 'Test',
      colorArgb: 0,
      weeklyAllowance: Money.fromCents(1000n),
      allowanceDayOfWeek: 1,
      allowanceActive: true,
      archived: false,
    });

    await txRepo.insert({
      childId: id,
      amount: Money.fromCents(1000n),
      label: 'AP',
      type: 'ALLOWANCE',
      occurredAt: new Date(),
      createdAt: new Date(),
    });
    await txRepo.insert({
      childId: id,
      amount: Money.fromCents(500n),
      label: 'Cadeau',
      type: 'CREDIT',
      occurredAt: new Date(),
      createdAt: new Date(),
    });
    await txRepo.insert({
      childId: id,
      amount: Money.fromCents(-200n),
      label: 'Bonbon',
      type: 'DEBIT',
      occurredAt: new Date(),
      createdAt: new Date(),
    });

    const balance = await txRepo.computeBalance(id);
    expect(balance.cents).toBe(1300n); // 1000 + 500 - 200
  });

  it('balance is zero with no transactions', async () => {
    const id = await childRepo.add({
      name: 'Test',
      colorArgb: 0,
      weeklyAllowance: Money.Zero,
      allowanceDayOfWeek: 1,
      allowanceActive: true,
      archived: false,
    });
    const balance = await txRepo.computeBalance(id);
    expect(balance.cents).toBe(0n);
  });

  it('balance can be negative', async () => {
    const id = await childRepo.add({
      name: 'Test',
      colorArgb: 0,
      weeklyAllowance: Money.Zero,
      allowanceDayOfWeek: 1,
      allowanceActive: true,
      archived: false,
    });
    await txRepo.insert({
      childId: id,
      amount: Money.fromCents(-500n),
      label: 'Débit',
      type: 'DEBIT',
      occurredAt: new Date(),
      createdAt: new Date(),
    });

    const balance = await txRepo.computeBalance(id);
    expect(balance.cents).toBe(-500n);
    expect(balance.isNegative).toBe(true);
  });
});
