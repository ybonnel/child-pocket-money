import { useNavigate } from 'react-router-dom';
import { useChildren } from '../../hooks/useChildren';
import { useBalance } from '../../hooks/useBalance';
import { ChildAvatar } from '../../components/ChildAvatar';
import { MoneyText } from '../../components/MoneyText';
import { EmptyState } from '../../components/EmptyState';
import { fr } from '../../../i18n/fr';
import type { Child } from '../../../domain/models';
import './ChildList.css';

function ChildCard({ child }: { child: Child }) {
  const navigate = useNavigate();
  const balance = useBalance(child.id);

  return (
    <button
      className="child-card"
      onClick={() => navigate(`/child/${child.id}`)}
      aria-label={child.name}
    >
      <ChildAvatar name={child.name} colorArgb={child.colorArgb} size={52} />
      <div className="child-card__info">
        <span className="child-card__name">{child.name}</span>
        {child.allowanceActive && !child.weeklyAllowance.isZero && (
          <span className="child-card__allowance">
            {fr.label_weekly_allowance}:{' '}
            <MoneyText money={child.weeklyAllowance} />
            /sem
          </span>
        )}
      </div>
      <div
        className={`child-card__balance ${balance.isNegative ? 'child-card__balance--negative' : ''}`}
      >
        <MoneyText money={balance} />
      </div>
    </button>
  );
}

export function ChildListScreen() {
  const navigate = useNavigate();
  const children = useChildren();

  return (
    <div className="screen child-list-screen">
      <header className="app-bar">
        <h1 className="app-bar__title">{fr.screen_children_title}</h1>
        <button
          className="icon-btn"
          onClick={() => navigate('/settings')}
          aria-label="Paramètres"
        >
          ⚙️
        </button>
      </header>

      <main className="screen__content">
        {children.length === 0 ? (
          <EmptyState
            icon="👧"
            title={fr.children_empty_title}
            subtitle={fr.children_empty_subtitle}
          />
        ) : (
          <ul className="child-list" role="list">
            {children.map((child) => (
              <li key={child.id}>
                <ChildCard child={child} />
              </li>
            ))}
          </ul>
        )}
      </main>

      <button
        className="fab"
        onClick={() => navigate('/child/new')}
        aria-label={fr.btn_add_child}
      >
        +
      </button>
    </div>
  );
}
