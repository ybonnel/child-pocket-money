import { useLiveQuery } from 'dexie-react-hooks';
import { childRepository } from '../../data/repositories/childRepository';
import type { Child } from '../../domain/models';

export function useChildren(): Child[] {
  return useLiveQuery(() => childRepository.listAll(), [], []);
}

export function useChild(id: number | undefined): Child | undefined {
  return useLiveQuery(
    () => (id === undefined ? Promise.resolve(undefined) : childRepository.getById(id)),
    [id],
  );
}
