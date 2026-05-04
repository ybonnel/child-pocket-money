import { transactionRepository } from '../../data/repositories/transactionRepository';

export const balanceUseCases = {
  compute: (childId: number) => transactionRepository.computeBalance(childId),
  observe: (childId: number) => transactionRepository.observeBalance(childId),
};
