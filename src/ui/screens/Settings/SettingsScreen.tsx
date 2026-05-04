import { useNavigate } from 'react-router-dom';
import { usePreferencesStore } from '../../../store/preferencesStore';
import { SUPPORTED_CURRENCIES, type CurrencyCode } from '../../../core/currency';
import { fr } from '../../../i18n/fr';
import './Settings.css';

type Theme = 'system' | 'light' | 'dark';

const THEMES: { value: Theme; label: string }[] = [
  { value: 'system', label: fr.theme_system },
  { value: 'light', label: fr.theme_light },
  { value: 'dark', label: fr.theme_dark },
];

export function SettingsScreen() {
  const navigate = useNavigate();
  const { currency, setCurrency, theme, setTheme, installPrompt, setInstallPrompt } =
    usePreferencesStore();

  const handleInstall = async () => {
    if (!installPrompt) return;
    await installPrompt.prompt();
    const choice = await installPrompt.userChoice;
    if (choice.outcome === 'accepted') {
      setInstallPrompt(null);
    }
  };

  return (
    <div className="screen settings-screen">
      <header className="app-bar">
        <button className="app-bar__back icon-btn" onClick={() => navigate(-1)}>
          ←
        </button>
        <h1 className="app-bar__title">{fr.screen_settings_title}</h1>
      </header>

      <main className="screen__content">
        {/* Currency */}
        <section className="settings-section">
          <h2 className="settings-section__title">{fr.label_currency}</h2>
          <div className="settings-card">
            <select
              className="form-select settings-select"
              value={currency}
              onChange={(e) => setCurrency(e.target.value as CurrencyCode)}
              aria-label={fr.label_currency}
            >
              {SUPPORTED_CURRENCIES.map((c) => (
                <option key={c.code} value={c.code}>
                  {c.label}
                </option>
              ))}
            </select>
          </div>
        </section>

        {/* Theme */}
        <section className="settings-section">
          <h2 className="settings-section__title">{fr.label_theme}</h2>
          <div className="settings-card settings-card--radio">
            {THEMES.map((t) => (
              <label key={t.value} className="radio-option">
                <input
                  type="radio"
                  name="theme"
                  value={t.value}
                  checked={theme === t.value}
                  onChange={() => setTheme(t.value)}
                />
                <span>{t.label}</span>
              </label>
            ))}
          </div>
        </section>

        {/* Install */}
        {installPrompt && (
          <section className="settings-section">
            <button className="btn-primary" onClick={handleInstall}>
              📲 {fr.btn_install_app}
            </button>
          </section>
        )}

        {/* About */}
        <section className="settings-section">
          <h2 className="settings-section__title">{fr.label_about}</h2>
          <div className="settings-card">
            <p className="settings-about">{fr.about_version}</p>
            <p className="settings-about">{fr.about_description}</p>
          </div>
        </section>
      </main>
    </div>
  );
}
