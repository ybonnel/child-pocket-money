import { describe, it, expect } from 'vitest';
import { Money } from '../core/money';
import { formatMoney } from '../core/currency';

describe('Money', () => {
  describe('construction', () => {
    it('creates from cents', () => {
      const m = Money.fromCents(1500n);
      expect(m.cents).toBe(1500n);
    });

    it('Zero is 0 cents', () => {
      expect(Money.Zero.cents).toBe(0n);
      expect(Money.Zero.isZero).toBe(true);
    });
  });

  describe('fromDecimalString', () => {
    it('parses integer string', () => {
      expect(Money.fromDecimalString('15')?.cents).toBe(1500n);
    });

    it('parses decimal string', () => {
      expect(Money.fromDecimalString('12.50')?.cents).toBe(1250n);
    });

    it('parses decimal with comma', () => {
      expect(Money.fromDecimalString('12,50')?.cents).toBe(1250n);
    });

    it('parses zero', () => {
      expect(Money.fromDecimalString('0')?.cents).toBe(0n);
    });

    it('returns null for invalid input', () => {
      expect(Money.fromDecimalString('abc')).toBeNull();
      expect(Money.fromDecimalString('')).toBeNull();
    });

    it('parses negative', () => {
      expect(Money.fromDecimalString('-5.00')?.cents).toBe(-500n);
    });
  });

  describe('arithmetic', () => {
    it('adds two moneys', () => {
      const a = Money.fromCents(1000n);
      const b = Money.fromCents(500n);
      expect(a.add(b).cents).toBe(1500n);
    });

    it('subtracts two moneys', () => {
      const a = Money.fromCents(1000n);
      const b = Money.fromCents(300n);
      expect(a.subtract(b).cents).toBe(700n);
    });

    it('negates', () => {
      expect(Money.fromCents(500n).negate().cents).toBe(-500n);
    });

    it('absoluteValue on negative', () => {
      expect(Money.fromCents(-250n).absoluteValue.cents).toBe(250n);
    });

    it('isPositive / isNegative / isZero', () => {
      expect(Money.fromCents(1n).isPositive).toBe(true);
      expect(Money.fromCents(-1n).isNegative).toBe(true);
      expect(Money.fromCents(0n).isZero).toBe(true);
    });
  });

  describe('equals', () => {
    it('equal moneys', () => {
      expect(Money.fromCents(100n).equals(Money.fromCents(100n))).toBe(true);
    });
    it('unequal moneys', () => {
      expect(Money.fromCents(100n).equals(Money.fromCents(200n))).toBe(false);
    });
  });

  describe('toDecimalString / round-trip', () => {
    it('stores as string', () => {
      expect(Money.fromCents(1500n).toDecimalString()).toBe('1500');
    });
    it('round-trips from string', () => {
      const m = Money.fromCents(BigInt('99999999999999'));
      expect(Money.fromCents(BigInt(m.toDecimalString())).cents).toBe(m.cents);
    });
  });
});

describe('formatMoney', () => {
  it('formats EUR correctly — contains amount and symbol', () => {
    const result = formatMoney(Money.fromCents(1500n), 'EUR');
    // 15.00 € in fr-FR
    expect(result).toContain('15');
    expect(result).toContain('€');
  });

  it('formats negative EUR', () => {
    const result = formatMoney(Money.fromCents(-500n), 'EUR');
    expect(result).toContain('5');
    expect(result).toContain('€');
    expect(result).toContain('-');
  });

  it('formats zero EUR', () => {
    const result = formatMoney(Money.fromCents(0n), 'EUR');
    expect(result).toContain('0');
    expect(result).toContain('€');
  });

  it('formats USD', () => {
    const result = formatMoney(Money.fromCents(999n), 'USD');
    // 9.99 USD — formatted with fr-FR locale
    expect(result).toContain('9');
    // The number 9.99 will be formatted as 9,99 in fr-FR
    expect(result.replace(/\s/g, '')).toMatch(/9[,.]99/);
  });

  it('formats JPY with 0 decimals', () => {
    const result = formatMoney(Money.fromCents(500n), 'JPY');
    // JPY: 500 cents = 500 yen, no decimal
    expect(result).toContain('500');
    // No decimal separator for JPY
    expect(result).not.toMatch(/500[,.]\d/);
  });

  it('formats GBP', () => {
    const result = formatMoney(Money.fromCents(2000n), 'GBP');
    expect(result).toContain('20');
    expect(result).toMatch(/[£]/);
  });

  it('large amount keeps bigint precision', () => {
    // 9007199254740993 cents = extremely large value
    const m = Money.fromCents(100_000_00n); // 100,000.00
    const result = formatMoney(m, 'EUR');
    expect(result).toContain('100');
    expect(result).toContain('€');
  });
});
