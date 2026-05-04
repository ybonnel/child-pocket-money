import { useLiveQuery } from 'dexie-react-hooks';
import { childRepository } from '../../data/repositories/childRepository';
import type { Child } from '../../domain/models';

export function useChildren(): Child[] {
  return useLiveQuery(() => childRepository.listAll(), [], []);
}

export function useChild(id: number): Child | undefined {
  return useLiveQuery(() => childRepository.getById(id), [id]);
}
