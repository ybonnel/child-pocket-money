import React from 'react';
import ReactDOM from 'react-dom/client';
import { App } from './App';
import './ui/theme/tokens.css';
import { useRegisterSW } from 'virtual:pwa-register/react';

function Root() {
  const {
    needRefresh: [needRefresh],
    updateServiceWorker,
  } = useRegisterSW({
    onRegistered(r) {
      console.log('SW registered:', r);
    },
    onRegisterError(error) {
      console.log('SW registration error', error);
    },
  });

  return (
    <>
      <App />
      {needRefresh && (
        <div
          style={{
            position: 'fixed',
            bottom: 16,
            left: '50%',
            transform: 'translateX(-50%)',
            backgroundColor: 'var(--color-primary)',
            color: 'white',
            borderRadius: 8,
            padding: '12px 20px',
            boxShadow: 'var(--shadow-lg)',
            zIndex: 999,
            display: 'flex',
            gap: 12,
            alignItems: 'center',
            fontSize: '0.875rem',
          }}
        >
          <span>Mise à jour disponible</span>
          <button
            onClick={() => updateServiceWorker(true)}
            style={{
              background: 'rgba(255,255,255,0.2)',
              border: 'none',
              color: 'white',
              padding: '4px 10px',
              borderRadius: 4,
              cursor: 'pointer',
              fontWeight: 600,
            }}
          >
            Recharger
          </button>
        </div>
      )}
    </>
  );
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <Root />
  </React.StrictMode>,
);
