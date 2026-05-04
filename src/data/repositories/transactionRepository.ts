import { liveQuery } from 'dexie';
import Dexie from 'dexie';
import { db as defaultDb, type TransactionRow, PocketMoneyDatabase } from '../db';
import type { Transaction, TransactionType } from '../../domain/models';
import { Money } from '../../core/money';

// ─── Mappers ────────────────────────────────────────────────────────────────

function rowToTransaction(row: TransactionRow & { id: number }): Transaction {
  return {
    id: row.id,
    childId: row.childId,
    amount: Money.fromCents(BigInt(row.amountCents)),
    label: row.label,
    type: row.type as TransactionType,
    occurredAt: new Date(row.occurredAtEpochMs),
    createdAt: new Date(row.createdAtEpochMs),
  };
}

function txToRow(
  tx: Omit<Transaction, 'id' | 'createdAt'> & { createdAt?: Date },
): TransactionRow {
  return {
    childId: tx.childId,
    amountCents: tx.amount.toDecimalString(),
    label: tx.label,
    type: tx.type,
    occurredAtEpochMs: tx.occurredAt.getTime(),
    createdAtEpochMs: (tx.createdAt ?? new Date()).getTime(),
  };
}

// ─── Factory ─────────────────────────────────────────────────────────────────

export function createTransactionRepository(db: PocketMoneyDatabase = defaultDb) {
  return {
    async insert(tx: Omit<Transaction, 'id'>): Promise<number> {
      return db.transactions.add(txToRow(tx));
    },

    async delete(id: number): Promise<void> {
      await db.transactions.delete(id);
    },

    async listByChild(childId: number): Promise<Transaction[]> {
      const rows = await db.transactions
        .where('childId')
        .equals(childId)
        .reverse()
        .sortBy('occurredAtEpochMs');
      return rows
        .filter((r) => r.id !== undefined)
        .map((r) => rowToTransaction(r as TransactionRow & { id: number }));
    },

    async lastAllowanceEpochMs(childId: number): Promise<number | null> {
      const rows = await db.transactions
        .where('[childId+type+occurredAtEpochMs]')
        .between(
          [childId, 'ALLOWANCE', Dexie.minKey],
          [childId, 'ALLOWANCE', Dexie.maxKey],
        )
        .toArray();
      if (rows.length === 0) return null;
      return Math.max(...rows.map((r) => r.occurredAtEpochMs));
    },

    async computeBalance(childId: number): Promise<Money> {
      const rows = await db.transactions.where('childId').equals(childId).toArray();
      const total = rows.reduce((sum, r) => sum + BigInt(r.amountCents), 0n);
      return Money.fromCents(total);
    },

    observeByChild(childId: number) {
      return liveQuery(() =>
        db.transactions
          .where('childId')
          .equals(childId)
          .reverse()
          .sortBy('occurredAtEpochMs')
          .then((rows) =>
            rows
              .filter((r) => r.id !== undefined)
              .map((r) => rowToTransaction(r as TransactionRow & { id: number })),
          ),
      );
    },

    observeBalance(childId: number) {
      return liveQuery(async () => {
        const rows = await db.transactions.where('childId').equals(childId).toArray();
        const total = rows.reduce((sum, r) => sum + BigInt(r.amountCents), 0n);
        return Money.fromCents(total);
      });
    },
  };
}

// Default singleton
export const transactionRepository = createTransactionRepository();
