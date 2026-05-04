import { transactionRepository } from '../../data/repositories/transactionRepository';
import type { Transaction } from '../models';
import { Money } from '../../core/money';

export const transactionUseCases = {
  addCredit: (childId: number, amount: Money, label: string): Promise<number> =>
    transactionRepository.insert({
      childId,
      amount,
      label,
      type: 'CREDIT',
      occurredAt: new Date(),
      createdAt: new Date(),
    }),

  addDebit: (childId: number, amount: Money, label: string): Promise<number> =>
    transactionRepository.insert({
      childId,
      amount: amount.negate(),
      label,
      type: 'DEBIT',
      occurredAt: new Date(),
      createdAt: new Date(),
    }),

  delete: (id: number): Promise<void> => transactionRepository.delete(id),

  listByChild: (childId: number): Promise<Transaction[]> =>
    transactionRepository.listByChild(childId),

  observeByChild: (childId: number) => transactionRepository.observeByChild(childId),
};
