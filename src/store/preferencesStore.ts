import { create } from 'zustand';
import { preferences } from '../data/preferences';
import type { CurrencyCode } from '../core/currency';

type Theme = 'system' | 'light' | 'dark';

export interface BeforeInstallPromptEvent extends Event {
  prompt(): Promise<void>;
  readonly userChoice: Promise<{ outcome: 'accepted' | 'dismissed' }>;
}

interface PreferencesState {
  currency: CurrencyCode;
  theme: Theme;
  installPrompt: BeforeInstallPromptEvent | null;

  setCurrency: (c: CurrencyCode) => void;
  setTheme: (t: Theme) => void;
  setInstallPrompt: (e: BeforeInstallPromptEvent | null) => void;
}

export const usePreferencesStore = create<PreferencesState>((set) => ({
  currency: preferences.getCurrency(),
  theme: preferences.getTheme(),
  installPrompt: null,

  setCurrency: (c) => {
    preferences.setCurrency(c);
    set({ currency: c });
  },
  setTheme: (t) => {
    preferences.setTheme(t);
    set({ theme: t });
  },
  setInstallPrompt: (e) => set({ installPrompt: e }),
}));
