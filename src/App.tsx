import { HashRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from './ui/theme/ThemeProvider';
import { ChildListScreen } from './ui/screens/ChildList/ChildListScreen';
import { ChildDetailScreen } from './ui/screens/ChildDetail/ChildDetailScreen';
import { ChildEditScreen } from './ui/screens/ChildEdit/ChildEditScreen';
import { TransactionEditScreen } from './ui/screens/TransactionEdit/TransactionEditScreen';
import { SettingsScreen } from './ui/screens/Settings/SettingsScreen';
import { useAllowanceCheck } from './ui/hooks/useAllowanceCheck';
import { useEffect } from 'react';
import { usePreferencesStore } from './store/preferencesStore';

function AllowanceChecker() {
  useAllowanceCheck();
  return null;
}

function InstallPromptListener() {
  const setInstallPrompt = usePreferencesStore((s) => s.setInstallPrompt);

  useEffect(() => {
    const handler = (e: Event) => {
      e.preventDefault();
      setInstallPrompt(
        e as import('./store/preferencesStore').BeforeInstallPromptEvent,
      );
    };
    window.addEventListener('beforeinstallprompt', handler);
    return () => window.removeEventListener('beforeinstallprompt', handler);
  }, [setInstallPrompt]);

  return null;
}

export function App() {
  return (
    <ThemeProvider>
      <AllowanceChecker />
      <InstallPromptListener />
      <HashRouter>
        <Routes>
          <Route path="/" element={<ChildListScreen />} />
          <Route path="/child/new" element={<ChildEditScreen />} />
          <Route path="/child/:id" element={<ChildDetailScreen />} />
          <Route path="/child/:id/edit" element={<ChildEditScreen />} />
          <Route path="/child/:id/credit" element={<TransactionEditScreen />} />
          <Route path="/child/:id/debit" element={<TransactionEditScreen />} />
          <Route path="/settings" element={<SettingsScreen />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </HashRouter>
    </ThemeProvider>
  );
}
