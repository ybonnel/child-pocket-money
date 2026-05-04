import { createChildRepository } from '../../data/repositories/childRepository';
import { createTransactionRepository } from '../../data/repositories/transactionRepository';
import { childRepository as defaultChildRepo } from '../../data/repositories/childRepository';
import { transactionRepository as defaultTxRepo } from '../../data/repositories/transactionRepository';
import { isUniqueConstraintError } from '../../data/db';
import { preferences } from '../../data/preferences';
import { startOfDayLocal, isoWeekday, addDays } from '../../core/time';

const ONE_HOUR_MS = 60 * 60 * 1000;

type ChildRepo = ReturnType<typeof createChildRepository>;
type TxRepo = ReturnType<typeof createTransactionRepository>;

/**
 * Process due allowances — idempotent.
 * Mirror of the Kotlin ProcessDueAllowancesUseCase algorithm.
 */
export async function processDueAllowances(
  allowanceLabel: string,
  now: Date = new Date(),
  childRepo: ChildRepo = defaultChildRepo,
  txRepo: TxRepo = defaultTxRepo,
): Promise<void> {
  const today = startOfDayLocal(now);
  const children = await childRepo.listActiveWithAllowance();

  for (const child of children) {
    const lastMs = await txRepo.lastAllowanceEpochMs(child.id);
    const lastDate = lastMs ? startOfDayLocal(new Date(lastMs)) : null;

    // Start from day after last allowance, or from 6 days ago if none.
    let cursor = lastDate ? addDays(lastDate, 1) : addDays(today, -6);

    while (cursor <= today) {
      if (isoWeekday(cursor) === child.allowanceDayOfWeek) {
        try {
          await txRepo.insert({
            childId: child.id,
            amount: child.weeklyAllowance,
            label: allowanceLabel,
            type: 'ALLOWANCE',
            occurredAt: cursor,
            createdAt: now,
          });
        } catch (e) {
          if (!isUniqueConstraintError(e)) throw e;
          // Duplicate → already inserted by another tab/activation. Safe to ignore.
        }
      }
      cursor = addDays(cursor, 1);
    }
  }

  preferences.setLastAllowanceCheck(now.getTime());
}

/** Returns true if allowance check should be skipped (throttled to once per hour). */
export function shouldThrottleAllowanceCheck(): boolean {
  const last = preferences.getLastAllowanceCheck();
  if (last === null) return false;
  return Date.now() - last < ONE_HOUR_MS;
}
