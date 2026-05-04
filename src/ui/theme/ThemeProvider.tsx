import { useEffect, type ReactNode } from 'react';
import { usePreferencesStore } from '../../store/preferencesStore';

interface ThemeProviderProps {
  children: ReactNode;
}

export function ThemeProvider({ children }: ThemeProviderProps) {
  const theme = usePreferencesStore((s) => s.theme);

  useEffect(() => {
    const html = document.documentElement;

    if (theme === 'system') {
      html.removeAttribute('data-theme');
      const mq = window.matchMedia('(prefers-color-scheme: dark)');
      const apply = (dark: boolean) => {
        html.setAttribute('data-theme', dark ? 'dark' : 'light');
      };
      apply(mq.matches);
      const handler = (e: MediaQueryListEvent) => apply(e.matches);
      mq.addEventListener('change', handler);
      return () => mq.removeEventListener('change', handler);
    } else {
      html.setAttribute('data-theme', theme);
    }
  }, [theme]);

  return <>{children}</>;
}
