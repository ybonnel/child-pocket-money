import { childRepository } from '../../data/repositories/childRepository';
import type { Child } from '../models';

export const childUseCases = {
  add: (child: Omit<Child, 'id' | 'createdAt'>) => childRepository.add(child),
  update: (child: Child) => childRepository.update(child),
  delete: (id: number) => childRepository.delete(id),
  getById: (id: number) => childRepository.getById(id),
  observeAll: () => childRepository.observeAll(),
  observeById: (id: number) => childRepository.observeById(id),
};
