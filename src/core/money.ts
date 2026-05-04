/**
 * Money — value type wrapping bigint cents.
 * Mirrors the Kotlin Money(cents: Long) value class.
 */
export class Money {
  private constructor(public readonly cents: bigint) {}

  static readonly Zero = new Money(0n);

  static fromCents(cents: bigint): Money {
    return new Money(cents);
  }

  /** Parse a decimal string like "12.50" or "1500" (treated as 15.00 if > 2 decimal places) */
  static fromDecimalString(str: string): Money | null {
    const cleaned = str.replace(',', '.').trim();
    if (!/^-?\d+(\.\d{0,2})?$/.test(cleaned)) return null;
    const parts = cleaned.split('.');
    const whole = BigInt(parts[0] ?? '0');
    const sign = whole < 0n || cleaned.startsWith('-') ? -1n : 1n;
    const absWhole = whole < 0n ? -whole : whole;
    const fracStr = (parts[1] ?? '').padEnd(2, '0');
    const frac = BigInt(fracStr);
    return new Money(sign * (absWhole * 100n + frac));
  }

  /** Cents as decimal string for storage */
  toDecimalString(): string {
    return this.cents.toString();
  }

  add(other: Money): Money {
    return new Money(this.cents + other.cents);
  }

  subtract(other: Money): Money {
    return new Money(this.cents - other.cents);
  }

  negate(): Money {
    return new Money(-this.cents);
  }

  get isPositive(): boolean {
    return this.cents > 0n;
  }

  get isNegative(): boolean {
    return this.cents < 0n;
  }

  get isZero(): boolean {
    return this.cents === 0n;
  }

  get absoluteValue(): Money {
    return new Money(this.cents < 0n ? -this.cents : this.cents);
  }

  equals(other: Money): boolean {
    return this.cents === other.cents;
  }
}
