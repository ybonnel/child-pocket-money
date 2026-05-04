import { useLiveQuery } from 'dexie-react-hooks';
import { transactionRepository } from '../../data/repositories/transactionRepository';
import type { Transaction } from '../../domain/models';

export function useTransactions(childId: number): Transaction[] {
  return useLiveQuery(() => transactionRepository.listByChild(childId), [childId], []);
}
