# Argent de poche — Child Pocket Money Manager

A **Progressive Web App (PWA)** to manage pocket money for your children. Runs entirely in the browser — all data is 100% local, nothing is sent over the network.

🌐 **Live app:** https://ybonnel.github.io/child-pocket-money/

## Features

- 👧👦 **Multi-profile**: one profile per child with a name and custom colour
- 💰 **Weekly allowance**: set an amount and a day of week; credited automatically on app open
- ➕➖ **Manual transactions**: add or withdraw money with a reason
- 📋 **Full history**: all transactions with date and type, swipe to delete
- 💳 **Real-time balance**: computed from transactions, always consistent
- 🔒 **100% local**: no network connection, all data stays on the device (IndexedDB)
- 🌍 **Multi-currency**: EUR, USD, GBP, CHF, CAD, JPY
- 🌙 **Light/dark/system theme**
- 📲 **Installable**: add to home screen on Android Chrome or iOS Safari

## Architecture

| Concern | Choice |
|---|---|
| Language | TypeScript (strict) |
| Framework | React 18 |
| Build | Vite 5 |
| PWA | vite-plugin-pwa (Workbox) |
| Routing | React Router v6 (HashRouter) |
| Database | IndexedDB via Dexie.js |
| State | Zustand + Dexie live queries |
| Date math | date-fns |
| Currency | Intl.NumberFormat |
| Tests | Vitest + fake-indexeddb |

## Project structure

```
src/
├── core/           # Money (bigint cents), currency formatter, time helpers
├── data/           # Dexie DB, repositories, localStorage preferences
├── domain/
│   ├── models.ts   # Child, Transaction, TransactionType
│   └── usecases/   # children, transactions, balance, allowance
├── store/          # Zustand preferences store
├── i18n/           # French strings (fr.ts)
├── ui/
│   ├── theme/      # CSS tokens + ThemeProvider
│   ├── components/ # ChildAvatar, MoneyText, EmptyState
│   ├── screens/    # ChildList, ChildDetail, ChildEdit, TransactionEdit, Settings
│   └── hooks/      # useChildren, useTransactions, useBalance, useAllowanceCheck
└── test/           # Unit tests (money, repositories, allowance, balance)
```

## Screens

- **ChildList** (home): list of children with avatar, balance, allowance info; FAB to add
- **ChildDetail**: balance card, add/withdraw buttons, transaction history with swipe-to-delete
- **ChildEdit**: create or edit a child (name, colour, weekly allowance, day of week)
- **TransactionEdit**: add a credit or debit with an optional reason
- **Settings**: currency, theme, app install button

## Allowance logic

The app processes due allowances on every start and when the tab becomes visible again (throttled to once per hour). The algorithm is idempotent — a unique compound index `[childId+type+occurredAtEpochMs]` in IndexedDB prevents double-credits even if processing runs twice concurrently.

## Development

```bash
# Install dependencies
npm install

# Development server
npm run dev

# Run tests
npm test

# Production build
npm run build

# Preview production build
npm run preview
```

## Deploy

Pushes to `main` automatically deploy to GitHub Pages via the `deploy.yml` workflow.  
Repo setting required: **Settings → Pages → Source: GitHub Actions**.

> ⚠️ Data is stored locally on each device. There is no sync between devices — by design.

## Licence

MIT
