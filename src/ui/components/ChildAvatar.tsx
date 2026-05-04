import './ChildAvatar.css';

interface ChildAvatarProps {
  name: string;
  colorArgb: number;
  size?: number;
}

/** Extract #RRGGBB from a 32-bit ARGB int */
function argbToHex(argb: number): string {
  const r = (argb >> 16) & 0xff;
  const g = (argb >> 8) & 0xff;
  const b = argb & 0xff;
  return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`;
}

/** Luminance check to choose white or dark text */
function isLightColor(hex: string): boolean {
  const r = parseInt(hex.slice(1, 3), 16);
  const g = parseInt(hex.slice(3, 5), 16);
  const b = parseInt(hex.slice(5, 7), 16);
  const luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
  return luminance > 0.5;
}

export function ChildAvatar({ name, colorArgb, size = 48 }: ChildAvatarProps) {
  const hex = argbToHex(colorArgb);
  const textColor = isLightColor(hex) ? '#1a1a2e' : '#ffffff';
  const initial = name.charAt(0).toUpperCase();

  return (
    <div
      className="child-avatar"
      style={{
        width: size,
        height: size,
        backgroundColor: hex,
        color: textColor,
        fontSize: size * 0.42,
        borderRadius: '50%',
      }}
      aria-label={name}
    >
      {initial}
    </div>
  );
}
