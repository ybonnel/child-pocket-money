import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useChild } from '../../hooks/useChildren';
import { useTransactions } from '../../hooks/useTransactions';
import { useBalance } from '../../hooks/useBalance';
import { ChildAvatar } from '../../components/ChildAvatar';
import { MoneyText } from '../../components/MoneyText';
import { EmptyState } from '../../components/EmptyState';
import { transactionUseCases } from '../../../domain/usecases/transactions';
import { childUseCases } from '../../../domain/usecases/children';
import { fr } from '../../../i18n/fr';
import type { Transaction } from '../../../domain/models';
import { format } from 'date-fns';
import { fr as dateFnsFr } from 'date-fns/locale';
import './ChildDetail.css';

function TransactionItem({
  tx,
  onDelete,
}: {
  tx: Transaction;
  onDelete: (id: number) => void;
}) {
  const [swiped, setSwiped] = useState(false);

  const typeColor =
    tx.type === 'CREDIT' || tx.type === 'ALLOWANCE'
      ? 'var(--color-credit)'
      : tx.type === 'DEBIT'
        ? 'var(--color-debit)'
        : 'var(--color-on-surface-muted)';

  const typeLabel =
    tx.type === 'ALLOWANCE'
      ? fr.transaction_type_allowance
      : tx.type === 'CREDIT'
        ? fr.transaction_type_credit
        : tx.type === 'DEBIT'
          ? fr.transaction_type_debit
          : fr.transaction_type_adjustment;

  return (
    <li className={`tx-item ${swiped ? 'tx-item--swiped' : ''}`}>
      <div className="tx-item__delete-bg">
        <button
          className="tx-item__delete-btn"
          onClick={() => onDelete(tx.id)}
          aria-label={fr.btn_delete}
        >
          🗑️
        </button>
      </div>
      <div
        className="tx-item__content"
        onTouchStart={(e) => {
          const startX = e.touches[0].clientX;
          const handleEnd = (ev: TouchEvent) => {
            const dx = ev.changedTouches[0].clientX - startX;
            if (dx < -60) setSwiped(true);
            else setSwiped(false);
            document.removeEventListener('touchend', handleEnd);
          };
          document.addEventListener('touchend', handleEnd);
        }}
      >
        <div className="tx-item__type-dot" style={{ backgroundColor: typeColor }} />
        <div className="tx-item__body">
          <span className="tx-item__label">{tx.label}</span>
          <span className="tx-item__meta">
            {typeLabel} · {format(tx.occurredAt, 'd MMM yyyy', { locale: dateFnsFr })}
          </span>
        </div>
        <MoneyText
          className="tx-item__amount"
          money={tx.amount}
          // Inline style applied via the className
        />
      </div>
    </li>
  );
}

export function ChildDetailScreen() {
  const { id } = useParams<{ id: string }>();
  const childId = Number(id);
  const navigate = useNavigate();
  const child = useChild(childId);
  const balance = useBalance(childId);
  const transactions = useTransactions(childId);
  const [deletingTxId, setDeletingTxId] = useState<number | null>(null);
  const [showDeleteChild, setShowDeleteChild] = useState(false);

  if (!child) {
    return (
      <div className="screen">
        <header className="app-bar">
          <button className="app-bar__back icon-btn" onClick={() => navigate('/')}>
            ←
          </button>
          <h1 className="app-bar__title">…</h1>
        </header>
      </div>
    );
  }

  const handleDeleteTx = async (txId: number) => {
    setDeletingTxId(txId);
  };

  const confirmDeleteTx = async () => {
    if (deletingTxId !== null) {
      await transactionUseCases.delete(deletingTxId);
      setDeletingTxId(null);
    }
  };

  const handleDeleteChild = async () => {
    await childUseCases.delete(childId);
    navigate('/');
  };

  return (
    <div className="screen child-detail-screen">
      <header className="app-bar">
        <button className="app-bar__back icon-btn" onClick={() => navigate('/')}>
          ←
        </button>
        <h1 className="app-bar__title">{child.name}</h1>
        <button
          className="icon-btn"
          onClick={() => navigate(`/child/${childId}/edit`)}
          aria-label={fr.btn_edit_child}
        >
          ✏️
        </button>
        <button
          className="icon-btn"
          onClick={() => setShowDeleteChild(true)}
          aria-label={fr.btn_delete}
        >
          🗑️
        </button>
      </header>

      <main className="screen__content">
        {/* Balance card */}
        <div className="balance-card">
          <ChildAvatar name={child.name} colorArgb={child.colorArgb} size={64} />
          <div className="balance-card__info">
            <span className="balance-card__label">{fr.label_balance}</span>
            <MoneyText
              className={`balance-card__amount ${balance.isNegative ? 'balance-card__amount--negative' : ''}`}
              money={balance}
            />
            {child.allowanceActive && !child.weeklyAllowance.isZero && (
              <span className="balance-card__allowance">
                <MoneyText money={child.weeklyAllowance} />
                /sem ·{' '}
                {fr[`weekday_${child.allowanceDayOfWeek}` as keyof typeof fr] as string}
              </span>
            )}
          </div>
        </div>

        {/* Action buttons */}
        <div className="action-btns">
          <button
            className="action-btn action-btn--credit"
            onClick={() => navigate(`/child/${childId}/credit`)}
          >
            + {fr.btn_add_credit}
          </button>
          <button
            className="action-btn action-btn--debit"
            onClick={() => navigate(`/child/${childId}/debit`)}
          >
            − {fr.btn_add_debit}
          </button>
        </div>

        {/* Transactions */}
        {transactions.length === 0 ? (
          <EmptyState
            icon="📋"
            title={fr.transactions_empty_title}
            subtitle={fr.transactions_empty_subtitle}
          />
        ) : (
          <ul className="tx-list" role="list">
            {transactions.map((tx) => (
              <TransactionItem key={tx.id} tx={tx} onDelete={handleDeleteTx} />
            ))}
          </ul>
        )}
      </main>

      {/* Delete transaction dialog */}
      {deletingTxId !== null && (
        <div className="dialog-overlay">
          <div className="dialog">
            <p>{fr.confirm_delete_transaction}</p>
            <div className="dialog__actions">
              <button className="dialog__btn" onClick={() => setDeletingTxId(null)}>
                {fr.btn_cancel}
              </button>
              <button className="dialog__btn dialog__btn--danger" onClick={confirmDeleteTx}>
                {fr.btn_delete}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete child dialog */}
      {showDeleteChild && (
        <div className="dialog-overlay">
          <div className="dialog">
            <p>{fr.confirm_delete_child}</p>
            <div className="dialog__actions">
              <button className="dialog__btn" onClick={() => setShowDeleteChild(false)}>
                {fr.btn_cancel}
              </button>
              <button className="dialog__btn dialog__btn--danger" onClick={handleDeleteChild}>
                {fr.btn_delete}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
