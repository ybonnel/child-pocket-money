import { liveQuery } from 'dexie';
import { db as defaultDb, type ChildRow, PocketMoneyDatabase } from '../db';
import type { Child } from '../../domain/models';
import { Money } from '../../core/money';
import type { IsoWeekday } from '../../core/time';

// ─── Mappers ────────────────────────────────────────────────────────────────

function rowToChild(row: ChildRow & { id: number }): Child {
  return {
    id: row.id,
    name: row.name,
    colorArgb: row.colorArgb,
    weeklyAllowance: Money.fromCents(BigInt(row.weeklyAllowanceCents)),
    allowanceDayOfWeek: row.allowanceDayOfWeek as IsoWeekday,
    allowanceActive: row.allowanceActive === 1,
    archived: row.archived === 1,
    createdAt: new Date(row.createdAtEpochMs),
  };
}

function childToRow(child: Omit<Child, 'id' | 'createdAt'> & { id?: number }): ChildRow {
  return {
    ...(child.id ? { id: child.id } : {}),
    name: child.name,
    colorArgb: child.colorArgb,
    weeklyAllowanceCents: child.weeklyAllowance.toDecimalString(),
    allowanceDayOfWeek: child.allowanceDayOfWeek,
    allowanceActive: child.allowanceActive ? 1 : 0,
    archived: child.archived ? 1 : 0,
    createdAtEpochMs: Date.now(),
  };
}

// ─── Factory ─────────────────────────────────────────────────────────────────

export function createChildRepository(db: PocketMoneyDatabase = defaultDb) {
  return {
    async add(child: Omit<Child, 'id' | 'createdAt'>): Promise<number> {
      return db.children.add(childToRow(child));
    },

    async update(child: Child): Promise<void> {
      const row = childToRow(child);
      await db.children.update(child.id, {
        name: row.name,
        colorArgb: row.colorArgb,
        weeklyAllowanceCents: row.weeklyAllowanceCents,
        allowanceDayOfWeek: row.allowanceDayOfWeek,
        allowanceActive: row.allowanceActive,
        archived: row.archived,
      });
    },

    async delete(id: number): Promise<void> {
      await db.transaction('rw', db.children, db.transactions, async () => {
        await db.transactions.where('childId').equals(id).delete();
        await db.children.delete(id);
      });
    },

    async getById(id: number): Promise<Child | undefined> {
      if (!id || !Number.isFinite(id)) return undefined;
      const row = await db.children.get(id);
      if (!row || row.id === undefined) return undefined;
      return rowToChild(row as ChildRow & { id: number });
    },

    async listAll(): Promise<Child[]> {
      const rows = await db.children.where('archived').equals(0).toArray();
      return rows
        .filter((r) => r.id !== undefined)
        .map((r) => rowToChild(r as ChildRow & { id: number }));
    },

    async listActiveWithAllowance(): Promise<Child[]> {
      const all = await this.listAll();
      return all.filter((c) => c.allowanceActive && !c.weeklyAllowance.isZero);
    },

    observeAll() {
      return liveQuery(() =>
        db.children
          .where('archived')
          .equals(0)
          .toArray()
          .then((rows) =>
            rows
              .filter((r) => r.id !== undefined)
              .map((r) => rowToChild(r as ChildRow & { id: number })),
          ),
      );
    },

    observeById(id: number) {
      return liveQuery(async () => {
        const row = await db.children.get(id);
        if (!row || row.id === undefined) return undefined;
        return rowToChild(row as ChildRow & { id: number });
      });
    },
  };
}

// Default singleton
export const childRepository = createChildRepository();
