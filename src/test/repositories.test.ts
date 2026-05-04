import { describe, it, expect, beforeEach } from 'vitest';
import 'fake-indexeddb/auto';
import { PocketMoneyDatabase } from '../data/db';
import { createChildRepository } from '../data/repositories/childRepository';
import { createTransactionRepository } from '../data/repositories/transactionRepository';
import { Money } from '../core/money';
import type { Child } from '../domain/models';

// Helper: fresh DB + repos per test
function makeTestDb() {
  const db = new PocketMoneyDatabase('test-' + Math.random().toString(36).slice(2));
  db.backendDB(); // eagerly open
  const childRepo = createChildRepository(db);
  const txRepo = createTransactionRepository(db);
  return { db, childRepo, txRepo };
}

describe('childRepository', () => {
  let childRepo: ReturnType<typeof createChildRepository>;
  let txRepo: ReturnType<typeof createTransactionRepository>;

  beforeEach(() => {
    const ctx = makeTestDb();
    childRepo = ctx.childRepo;
    txRepo = ctx.txRepo;
  });

  const newChild = (): Omit<Child, 'id' | 'createdAt'> => ({
    name: 'Emma',
    colorArgb: 0xff_e91e63,
    weeklyAllowance: Money.fromCents(500n),
    allowanceDayOfWeek: 1,
    allowanceActive: true,
    archived: false,
  });

  it('adds and retrieves a child', async () => {
    const id = await childRepo.add(newChild());
    expect(id).toBeTypeOf('number');

    const child = await childRepo.getById(id);
    expect(child).toBeDefined();
    expect(child!.name).toBe('Emma');
    expect(child!.weeklyAllowance.cents).toBe(500n);
    expect(child!.allowanceDayOfWeek).toBe(1);
    expect(child!.allowanceActive).toBe(true);
  });

  it('updates a child', async () => {
    const id = await childRepo.add(newChild());
    const child = await childRepo.getById(id);
    await childRepo.update({ ...child!, name: 'Zoé' });
    const updated = await childRepo.getById(id);
    expect(updated!.name).toBe('Zoé');
  });

  it('lists active children', async () => {
    await childRepo.add(newChild());
    await childRepo.add({ ...newChild(), name: 'Louis' });
    const all = await childRepo.listAll();
    expect(all).toHaveLength(2);
  });

  it('delete cascades transactions', async () => {
    const id = await childRepo.add(newChild());
    await txRepo.insert({
      childId: id,
      amount: Money.fromCents(100n),
      label: 'test',
      type: 'CREDIT',
      occurredAt: new Date(),
      createdAt: new Date(),
    });

    await childRepo.delete(id);

    const child = await childRepo.getById(id);
    expect(child).toBeUndefined();

    const txs = await txRepo.listByChild(id);
    expect(txs).toHaveLength(0);
  });

  it('listActiveWithAllowance excludes inactive / zero-allowance children', async () => {
    await childRepo.add(newChild()); // active + allowance
    await childRepo.add({ ...newChild(), name: 'No-allow', weeklyAllowance: Money.Zero });
    await childRepo.add({ ...newChild(), name: 'Inactive', allowanceActive: false });

    const active = await childRepo.listActiveWithAllowance();
    expect(active).toHaveLength(1);
    expect(active[0].name).toBe('Emma');
  });
});

describe('transactionRepository', () => {
  let childRepo: ReturnType<typeof createChildRepository>;
  let txRepo: ReturnType<typeof createTransactionRepository>;
  let childId: number;

  beforeEach(async () => {
    const ctx = makeTestDb();
    childRepo = ctx.childRepo;
    txRepo = ctx.txRepo;
    childId = await childRepo.add({
      name: 'Tom',
      colorArgb: 0xff_2196f3,
      weeklyAllowance: Money.fromCents(1000n),
      allowanceDayOfWeek: 1,
      allowanceActive: true,
      archived: false,
    });
  });

  it('inserts and lists transactions', async () => {
    await txRepo.insert({
      childId,
      amount: Money.fromCents(500n),
      label: 'Argent de poche',
      type: 'ALLOWANCE',
      occurredAt: new Date('2024-01-08'),
      createdAt: new Date(),
    });

    const txs = await txRepo.listByChild(childId);
    expect(txs).toHaveLength(1);
    expect(txs[0].amount.cents).toBe(500n);
  });

  it('computes balance correctly', async () => {
    await txRepo.insert({
      childId,
      amount: Money.fromCents(1000n),
      label: 'Crédit',
      type: 'CREDIT',
      occurredAt: new Date(),
      createdAt: new Date(),
    });
    await txRepo.insert({
      childId,
      amount: Money.fromCents(-300n),
      label: 'Débit',
      type: 'DEBIT',
      occurredAt: new Date(),
      createdAt: new Date(),
    });

    const balance = await txRepo.computeBalance(childId);
    expect(balance.cents).toBe(700n);
  });

  it('returns 0 balance when no transactions', async () => {
    const balance = await txRepo.computeBalance(childId);
    expect(balance.cents).toBe(0n);
  });

  it('unique compound index prevents duplicate allowance on same day', async () => {
    const day = new Date('2024-01-08T00:00:00.000Z');
    const tx = {
      childId,
      amount: Money.fromCents(1000n),
      label: 'Argent de poche',
      type: 'ALLOWANCE' as const,
      occurredAt: day,
      createdAt: new Date(),
    };

    await txRepo.insert(tx);
    await expect(txRepo.insert(tx)).rejects.toThrow();
  });

  it('lastAllowanceEpochMs returns null when no allowances', async () => {
    const ms = await txRepo.lastAllowanceEpochMs(childId);
    expect(ms).toBeNull();
  });

  it('lastAllowanceEpochMs returns the most recent allowance', async () => {
    const day1 = new Date('2024-01-08');
    const day2 = new Date('2024-01-15');

    await txRepo.insert({
      childId,
      amount: Money.fromCents(1000n),
      label: 'AP',
      type: 'ALLOWANCE',
      occurredAt: day1,
      createdAt: new Date(),
    });
    await txRepo.insert({
      childId,
      amount: Money.fromCents(1000n),
      label: 'AP',
      type: 'ALLOWANCE',
      occurredAt: day2,
      createdAt: new Date(),
    });

    const ms = await txRepo.lastAllowanceEpochMs(childId);
    expect(ms).toBe(day2.getTime());
  });

  it('deletes a transaction', async () => {
    const id = await txRepo.insert({
      childId,
      amount: Money.fromCents(100n),
      label: 'test',
      type: 'CREDIT',
      occurredAt: new Date(),
      createdAt: new Date(),
    });

    await txRepo.delete(id);
    const txs = await txRepo.listByChild(childId);
    expect(txs).toHaveLength(0);
  });
});
