import type { CurrencyCode } from '../core/currency';

type Theme = 'system' | 'light' | 'dark';

const KEYS = {
  currency: 'cpm.currency',
  theme: 'cpm.theme',
  lastAllowanceCheck: 'cpm.lastAllowanceCheck',
} as const;

export const preferences = {
  getCurrency(): CurrencyCode {
    return (localStorage.getItem(KEYS.currency) as CurrencyCode) ?? 'EUR';
  },
  setCurrency(c: CurrencyCode): void {
    localStorage.setItem(KEYS.currency, c);
  },

  getTheme(): Theme {
    return (localStorage.getItem(KEYS.theme) as Theme) ?? 'system';
  },
  setTheme(t: Theme): void {
    localStorage.setItem(KEYS.theme, t);
  },

  getLastAllowanceCheck(): number | null {
    const v = localStorage.getItem(KEYS.lastAllowanceCheck);
    return v ? Number(v) : null;
  },
  setLastAllowanceCheck(epochMs: number): void {
    localStorage.setItem(KEYS.lastAllowanceCheck, String(epochMs));
  },
};
