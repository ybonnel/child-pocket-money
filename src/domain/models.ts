import { Money } from '../core/money';
import type { IsoWeekday } from '../core/time';

export type TransactionType = 'ALLOWANCE' | 'CREDIT' | 'DEBIT' | 'ADJUSTMENT';

export interface Child {
  id: number; // 0 for unsaved
  name: string;
  colorArgb: number;
  weeklyAllowance: Money;
  allowanceDayOfWeek: IsoWeekday;
  allowanceActive: boolean;
  archived: boolean;
  createdAt: Date | null;
}

export interface Transaction {
  id: number;
  childId: number;
  amount: Money; // signed: positive = credit, negative = debit
  label: string;
  type: TransactionType;
  occurredAt: Date;
  createdAt: Date;
}
