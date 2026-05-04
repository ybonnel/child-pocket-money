import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useChild } from '../../hooks/useChildren';
import { childUseCases } from '../../../domain/usecases/children';
import { Money } from '../../../core/money';
import { fr } from '../../../i18n/fr';
import type { IsoWeekday } from '../../../core/time';
import { ISO_WEEKDAYS } from '../../../core/time';
import './ChildEdit.css';

const PRESET_COLORS = [
  0xff_e53935, // red
  0xff_e91e63, // pink
  0xff_9c27b0, // purple
  0xff_3f51b5, // indigo
  0xff_2196f3, // blue
  0xff_009688, // teal
  0xff_4caf50, // green
  0xff_ff9800, // orange
  0xff_795548, // brown
  0xff_607d8b, // blue-grey
];

function argbToHex(argb: number): string {
  const r = (argb >> 16) & 0xff;
  const g = (argb >> 8) & 0xff;
  const b = argb & 0xff;
  return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`;
}

export function ChildEditScreen() {
  const { id } = useParams<{ id: string }>();
  const isNew = id === 'new';
  const childId = isNew ? undefined : Number(id);
  const navigate = useNavigate();
  const existingChild = useChild(childId ?? 0);

  const [name, setName] = useState(isNew ? '' : existingChild?.name ?? '');
  const [colorArgb, setColorArgb] = useState(
    isNew ? PRESET_COLORS[1] : existingChild?.colorArgb ?? PRESET_COLORS[1],
  );
  const [allowanceStr, setAllowanceStr] = useState(
    isNew
      ? ''
      : existingChild?.weeklyAllowance.isZero
        ? ''
        : String(Number(existingChild?.weeklyAllowance.cents ?? 0n) / 100),
  );
  const [allowanceDay, setAllowanceDay] = useState<IsoWeekday>(
    isNew ? 1 : existingChild?.allowanceDayOfWeek ?? 1,
  );
  const [allowanceActive, setAllowanceActive] = useState(
    isNew ? true : existingChild?.allowanceActive ?? true,
  );
  const [nameError, setNameError] = useState('');
  const [amountError, setAmountError] = useState('');

  // If editing, wait for child to load
  if (!isNew && existingChild === undefined && childId !== undefined) {
    return null;
  }

  const handleSave = async () => {
    let valid = true;
    setNameError('');
    setAmountError('');

    const trimmedName = name.trim();
    if (!trimmedName) {
      setNameError(fr.error_name_required);
      valid = false;
    }

    let allowanceMoney = Money.Zero;
    if (allowanceStr.trim()) {
      const parsed = Money.fromDecimalString(allowanceStr);
      if (!parsed || parsed.isNegative) {
        setAmountError(fr.error_invalid_amount);
        valid = false;
      } else {
        allowanceMoney = parsed;
      }
    }

    if (!valid) return;

    if (isNew) {
      await childUseCases.add({
        name: trimmedName,
        colorArgb,
        weeklyAllowance: allowanceMoney,
        allowanceDayOfWeek: allowanceDay,
        allowanceActive,
        archived: false,
      });
    } else if (existingChild) {
      await childUseCases.update({
        ...existingChild,
        name: trimmedName,
        colorArgb,
        weeklyAllowance: allowanceMoney,
        allowanceDayOfWeek: allowanceDay,
        allowanceActive,
      });
    }
    navigate(-1);
  };

  return (
    <div className="screen child-edit-screen">
      <header className="app-bar">
        <button className="app-bar__back icon-btn" onClick={() => navigate(-1)}>
          ←
        </button>
        <h1 className="app-bar__title">
          {isNew ? fr.screen_add_child_title : fr.screen_edit_child_title}
        </h1>
      </header>

      <main className="screen__content">
        <form
          className="edit-form"
          onSubmit={(e) => {
            e.preventDefault();
            handleSave();
          }}
        >
          {/* Name */}
          <div className="form-field">
            <label className="form-label" htmlFor="child-name">
              {fr.label_child_name}
            </label>
            <input
              id="child-name"
              className={`form-input ${nameError ? 'form-input--error' : ''}`}
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Ex: Emma"
              maxLength={50}
              autoFocus={isNew}
            />
            {nameError && <span className="form-error">{nameError}</span>}
          </div>

          {/* Color picker */}
          <div className="form-field">
            <label className="form-label">{fr.label_child_color}</label>
            <div className="color-grid">
              {PRESET_COLORS.map((c) => (
                <button
                  key={c}
                  type="button"
                  className={`color-swatch ${colorArgb === c ? 'color-swatch--selected' : ''}`}
                  style={{ backgroundColor: argbToHex(c) }}
                  onClick={() => setColorArgb(c)}
                  aria-label={`Couleur ${argbToHex(c)}`}
                />
              ))}
            </div>
          </div>

          {/* Weekly allowance */}
          <div className="form-field">
            <label className="form-label" htmlFor="allowance-amount">
              {fr.label_weekly_amount}
            </label>
            <input
              id="allowance-amount"
              className={`form-input ${amountError ? 'form-input--error' : ''}`}
              type="number"
              min="0"
              step="0.01"
              value={allowanceStr}
              onChange={(e) => setAllowanceStr(e.target.value)}
              placeholder="0.00"
            />
            {amountError && <span className="form-error">{amountError}</span>}
          </div>

          {/* Day of week */}
          <div className="form-field">
            <label className="form-label" htmlFor="allowance-day">
              {fr.label_allowance_day_of_week}
            </label>
            <select
              id="allowance-day"
              className="form-select"
              value={allowanceDay}
              onChange={(e) => setAllowanceDay(Number(e.target.value) as IsoWeekday)}
            >
              {ISO_WEEKDAYS.map((d) => (
                <option key={d.value} value={d.value}>
                  {d.label}
                </option>
              ))}
            </select>
          </div>

          {/* Allowance active toggle */}
          <div className="form-field form-field--row">
            <label className="form-label" htmlFor="allowance-active">
              {fr.label_allowance_active}
            </label>
            <input
              id="allowance-active"
              type="checkbox"
              className="form-toggle"
              checked={allowanceActive}
              onChange={(e) => setAllowanceActive(e.target.checked)}
            />
          </div>

          <button type="submit" className="btn-primary">
            {fr.btn_save}
          </button>
        </form>
      </main>
    </div>
  );
}
