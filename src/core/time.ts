import { startOfDay, addDays, getISODay } from 'date-fns';

/** ISO weekday: 1=Monday … 7=Sunday */
export type IsoWeekday = 1 | 2 | 3 | 4 | 5 | 6 | 7;

export const ISO_WEEKDAYS: { value: IsoWeekday; label: string }[] = [
  { value: 1, label: 'Lundi' },
  { value: 2, label: 'Mardi' },
  { value: 3, label: 'Mercredi' },
  { value: 4, label: 'Jeudi' },
  { value: 5, label: 'Vendredi' },
  { value: 6, label: 'Samedi' },
  { value: 7, label: 'Dimanche' },
];

/** Return the start of the given day in local time (midnight) */
export function startOfDayLocal(date: Date): Date {
  return startOfDay(date);
}

/** Get ISO weekday (1=Monday … 7=Sunday) for a date */
export function isoWeekday(date: Date): IsoWeekday {
  return getISODay(date) as IsoWeekday;
}

export { addDays };
