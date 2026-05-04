import { argbToHex, isLightColor } from '../../core/color';
import './ChildAvatar.css';

interface ChildAvatarProps {
  name: string;
  colorArgb: number;
  size?: number;
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
