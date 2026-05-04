/**
 * Edge-case tests for childRepository — covers the scenarios that triggered
 * the DataError when navigating to /child/new.
 */
import { describe, it, expect, beforeEach } from 'vitest';
import 'fake-indexeddb/auto';
import { PocketMoneyDatabase } from '../data/db';
import { createChildRepository } from '../data/repositories/childRepository';
import { Money } from '../core/money';
import type { Child } from '../domain/models';

function makeTestDb() {
  const db = new PocketMoneyDatabase('edge-' + Math.random().toString(36).slice(2));
  db.backendDB();
  return createChildRepository(db);
}

const baseChild = (): Omit<Child, 'id' | 'createdAt'> => ({
  name: 'Alice',
  colorArgb: 0xff_2196f3,
  weeklyAllowance: Money.fromCents(500n),
  allowanceDayOfWeek: 1,
  allowanceActive: true,
  archived: false,
});

// ── getById edge cases ───────────────────────────────────────────────────────

describe('childRepository.getById — edge cases', () => {
  let repo: ReturnType<typeof makeTestDb>;

  beforeEach(() => {
    repo = makeTestDb();
  });

  it('returns undefined for id 0 (the bug trigger)', async () => {
    // IDB auto-increment keys start at 1, so 0 is always invalid.
    // Before the fix, db.children.get(0) threw a DataError.
    const result = await repo.getById(0);
    expect(result).toBeUndefined();
  });

  it('returns undefined for a non-existent numeric id', async () => {
    const result = await repo.getById(99_999);
    expect(result).toBeUndefined();
  });

  it('returns undefined for NaN coerced to a number', async () => {
    // Number(NaN) === NaN — guard against accidental NaN keys
    const result = await repo.getById(NaN);
    expect(result).toBeUndefined();
  });
});

// ── listAll edge cases ───────────────────────────────────────────────────────

describe('childRepository.listAll — edge cases', () => {
  it('returns an empty array on a fresh (empty) database', async () => {
    const repo = makeTestDb();
    const result = await repo.listAll();
    expect(result).toEqual([]);
  });

  it('excludes archived children', async () => {
    const repo = makeTestDb();
    await repo.add({ ...baseChild(), name: 'Visible' });
    const id = await repo.add({ ...baseChild(), name: 'Hidden', archived: true });
    // manually archive via update
    const child = await repo.getById(id);
    await repo.update({ ...child!, archived: true });

    const list = await repo.listAll();
    expect(list).toHaveLength(1);
    expect(list[0].name).toBe('Visible');
  });
});

// ── update edge cases ────────────────────────────────────────────────────────

describe('childRepository.update — edge cases', () => {
  it('does not throw when updating a non-existent child', async () => {
    const repo = makeTestDb();
    const ghost: Child = {
      id: 99_999,
      name: 'Ghost',
      colorArgb: 0xff_000000,
      weeklyAllowance: Money.Zero,
      allowanceDayOfWeek: 1,
      allowanceActive: false,
      archived: false,
      createdAt: null,
    };
    // Dexie.update on a missing key is a no-op (returns 0), should not throw
    await expect(repo.update(ghost)).resolves.not.toThrow();
  });
});
