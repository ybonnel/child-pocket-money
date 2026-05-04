import { useEffect } from 'react';
import {
  processDueAllowances,
  shouldThrottleAllowanceCheck,
} from '../../domain/usecases/allowance';
import { fr } from '../../i18n/fr';

/**
 * Hook that triggers allowance processing on app start and on visibility change.
 * Throttled to once per hour via localStorage.
 */
export function useAllowanceCheck() {
  useEffect(() => {
    async function check() {
      if (shouldThrottleAllowanceCheck()) return;
      try {
        await processDueAllowances(fr.transaction_type_allowance);
      } catch (e) {
        console.error('Allowance check failed', e);
      }
    }

    check();

    const handleVisibility = () => {
      if (document.visibilityState === 'visible') {
        check();
      }
    };

    document.addEventListener('visibilitychange', handleVisibility);
    return () => document.removeEventListener('visibilitychange', handleVisibility);
  }, []);
}
