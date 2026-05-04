import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { transactionUseCases } from '../../../domain/usecases/transactions';
import { Money } from '../../../core/money';
import { fr } from '../../../i18n/fr';
import './TransactionEdit.css';

export function TransactionEditScreen() {
  const { id, type } = useParams<{ id: string; type: 'credit' | 'debit' }>();
  const childId = Number(id);
  const isCredit = type === 'credit';
  const navigate = useNavigate();

  const [amountStr, setAmountStr] = useState('');
  const [reason, setReason] = useState('');
  const [amountError, setAmountError] = useState('');
  const [saving, setSaving] = useState(false);

  const handleSave = async () => {
    setAmountError('');

    const parsed = Money.fromDecimalString(amountStr);
    if (!parsed || parsed.isZero || parsed.isNegative) {
      setAmountError(fr.error_amount_required);
      return;
    }

    setSaving(true);
    try {
      if (isCredit) {
        await transactionUseCases.addCredit(childId, parsed, reason || fr.transaction_type_credit);
      } else {
        await transactionUseCases.addDebit(childId, parsed, reason || fr.transaction_type_debit);
      }
      navigate(-1);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="screen transaction-edit-screen">
      <header className="app-bar">
        <button className="app-bar__back icon-btn" onClick={() => navigate(-1)}>
          ←
        </button>
        <h1 className="app-bar__title">
          {isCredit ? fr.screen_add_credit_title : fr.screen_add_debit_title}
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
          <div className="form-field">
            <label className="form-label" htmlFor="tx-amount">
              {fr.label_amount}
            </label>
            <input
              id="tx-amount"
              className={`form-input form-input--large ${amountError ? 'form-input--error' : ''}`}
              type="number"
              min="0.01"
              step="0.01"
              value={amountStr}
              onChange={(e) => setAmountStr(e.target.value)}
              placeholder="0.00"
              autoFocus
            />
            {amountError && <span className="form-error">{amountError}</span>}
          </div>

          <div className="form-field">
            <label className="form-label" htmlFor="tx-reason">
              {fr.label_reason}
            </label>
            <input
              id="tx-reason"
              className="form-input"
              type="text"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder={fr.placeholder_reason}
              maxLength={100}
            />
          </div>

          <button
            type="submit"
            className={`btn-primary ${isCredit ? 'btn-primary--credit' : 'btn-primary--debit'}`}
            disabled={saving}
          >
            {fr.btn_save}
          </button>
        </form>
      </main>
    </div>
  );
}
