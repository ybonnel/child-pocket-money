import { formatMoney } from '../../core/currency';
import { usePreferencesStore } from '../../store/preferencesStore';
import type { Money } from '../../core/money';

interface MoneyTextProps {
  money: Money;
  className?: string;
  /** Override currency (uses store value by default) */
  currency?: string;
}

export function MoneyText({ money, className, currency: currencyProp }: MoneyTextProps) {
  const storeCurrency = usePreferencesStore((s) => s.currency);
  const currency = (currencyProp ?? storeCurrency) as import('../../core/currency').CurrencyCode;
  const formatted = formatMoney(money, currency);
  return <span className={className}>{formatted}</span>;
}
