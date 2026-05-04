import { Money } from './money';

export type CurrencyCode = 'EUR' | 'USD' | 'GBP' | 'CHF' | 'CAD' | 'JPY';

export interface CurrencyInfo {
  code: CurrencyCode;
  label: string;
  /** Number of decimal digits for display (JPY = 0) */
  decimals: number;
}

export const SUPPORTED_CURRENCIES: CurrencyInfo[] = [
  { code: 'EUR', label: 'Euro (€)', decimals: 2 },
  { code: 'USD', label: 'Dollar US ($)', decimals: 2 },
  { code: 'GBP', label: 'Livre sterling (£)', decimals: 2 },
  { code: 'CHF', label: 'Franc suisse (CHF)', decimals: 2 },
  { code: 'CAD', label: 'Dollar canadien (CA$)', decimals: 2 },
  { code: 'JPY', label: 'Yen japonais (¥)', decimals: 0 },
];

const formattersCache = new Map<CurrencyCode, Intl.NumberFormat>();

function getFormatter(currency: CurrencyCode): Intl.NumberFormat {
  if (!formattersCache.has(currency)) {
    formattersCache.set(
      currency,
      new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency,
        minimumFractionDigits: currency === 'JPY' ? 0 : 2,
        maximumFractionDigits: currency === 'JPY' ? 0 : 2,
      }),
    );
  }
  return formattersCache.get(currency)!;
}

/**
 * Format a Money value for display.
 * cents are divided by 100 for all currencies except JPY (cents = whole units).
 */
export function formatMoney(money: Money, currency: CurrencyCode): string {
  const info = SUPPORTED_CURRENCIES.find((c) => c.code === currency)!;
  const divisor = info.decimals === 0 ? 1 : 100;
  const amount = Number(money.cents) / divisor;
  return getFormatter(currency).format(amount);
}

/** Parse a display amount string to Money */
export function parseMoneyInput(input: string, _currency: CurrencyCode): Money | null {
  return Money.fromDecimalString(input);
}
