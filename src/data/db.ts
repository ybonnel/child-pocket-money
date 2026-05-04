import Dexie, { type Table } from 'dexie';
import type { IsoWeekday } from '../core/time';
import type { TransactionType } from '../domain/models';

export interface ChildRow {
  id?: number;
  name: string;
  colorArgb: number;
  weeklyAllowanceCents: string; // bigint as decimal string
  allowanceDayOfWeek: IsoWeekday;
  allowanceActive: 0 | 1;
  archived: 0 | 1;
  createdAtEpochMs: number;
}

export interface TransactionRow {
  id?: number;
  childId: number;
  amountCents: string; // signed bigint as decimal string
  label: string;
  type: TransactionType;
  occurredAtEpochMs: number;
  createdAtEpochMs: number;
}

export class PocketMoneyDatabase extends Dexie {
  children!: Table<ChildRow, number>;
  transactions!: Table<TransactionRow, number>;

  constructor(name = 'child-pocket-money') {
    super(name);
    this.version(1).stores({
      children: '++id, archived, createdAtEpochMs',
      transactions:
        '++id, childId, occurredAtEpochMs, &[childId+type+occurredAtEpochMs]',
    });
  }
}

// Singleton for production use
export const db = new PocketMoneyDatabase();

export function isUniqueConstraintError(e: unknown): boolean {
  if (e instanceof Dexie.ModifyError) return false;
  // Dexie wraps IDB ConstraintError
  if (e && typeof e === 'object' && 'name' in e) {
    return (e as { name: string }).name === 'ConstraintError';
  }
  return false;
}
