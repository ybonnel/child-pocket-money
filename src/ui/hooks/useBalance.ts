import { useLiveQuery } from 'dexie-react-hooks';
import { transactionRepository } from '../../data/repositories/transactionRepository';
import { Money } from '../../core/money';

export function useBalance(childId: number): Money {
  return useLiveQuery(
    () => transactionRepository.computeBalance(childId),
    [childId],
    Money.Zero,
  );
}
