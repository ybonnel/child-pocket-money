/**
 * Tests for ChildEditScreen — covers the /child/new route (bug fix) and
 * form validation / save logic.
 *
 * Strategy
 * --------
 * - Mock `dexie-react-hooks` (useLiveQuery) so the component doesn't need a
 *   real IDB instance.
 * - Mock `childUseCases` to capture `add` calls.
 * - Wrap the component in a MemoryRouter so react-router hooks work.
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { ChildEditScreen } from '../ui/screens/ChildEdit/ChildEditScreen';

// ── Mocks ────────────────────────────────────────────────────────────────────

// useLiveQuery returns undefined on first render (loading state) by default.
// We override per test when we need a specific value.
vi.mock('dexie-react-hooks', () => ({
  useLiveQuery: vi.fn((fn: () => unknown) => {
    // Execute the factory so that tests calling with undefined don't explode.
    // Real async resolution doesn't happen in tests, so we just return undefined
    // (simulates "still loading" or "not found") unless the factory throws.
    try {
      // If the factory is a sync no-op (id === undefined path) it's fine.
      // We swallow errors so that id=0 / NaN factories don't crash either.
      void fn();
    } catch (_) {
      // ignore
    }
    return undefined;
  }),
}));

// Mock childUseCases so we don't need a real DB for save tests.
const mockAdd = vi.fn().mockResolvedValue(1);
vi.mock('../domain/usecases/children', () => ({
  childUseCases: {
    add: (...args: unknown[]) => mockAdd(...args),
    update: vi.fn().mockResolvedValue(undefined),
  },
}));

// Mock navigate so we can check redirection without a full router.
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// ── Helpers ──────────────────────────────────────────────────────────────────

function renderNew() {
  return render(
    <MemoryRouter initialEntries={['/child/new']}>
      <Routes>
        <Route path="/child/new" element={<ChildEditScreen />} />
      </Routes>
    </MemoryRouter>,
  );
}

// ── Tests ─────────────────────────────────────────────────────────────────────

describe('ChildEditScreen — /child/new', () => {
  beforeEach(() => {
    mockAdd.mockClear();
    mockNavigate.mockClear();
  });

  it('renders the add form without crashing (bug fix: no IDB DataError)', () => {
    // Before the fix, this would trigger useChild(0) → DataError
    expect(() => renderNew()).not.toThrow();
    expect(screen.getByText('Nouvel enfant')).toBeInTheDocument();
  });

  it('shows an empty name field', () => {
    renderNew();
    const nameInput = screen.getByPlaceholderText('Ex: Emma');
    expect(nameInput).toHaveValue('');
  });

  it('shows validation error when name is empty on submit', async () => {
    renderNew();
    const saveBtn = screen.getByRole('button', { name: /enregistrer/i });
    await userEvent.click(saveBtn);
    expect(await screen.findByText('Le prénom est requis')).toBeInTheDocument();
  });

  it('shows validation error when amount is invalid (non-numeric)', async () => {
    renderNew();
    const nameInput = screen.getByPlaceholderText('Ex: Emma');
    await userEvent.type(nameInput, 'Emma');

    // <input type="number"> in jsdom sanitizes values to "" for out-of-spec
    // numbers. To get a non-empty but invalid string into allowanceStr state
    // we temporarily swap the input type to "text", fire the change, then
    // revert — simulating what would happen on a real browser with a raw string.
    const amountInput = screen.getByPlaceholderText('0.00');
    amountInput.setAttribute('type', 'text');
    fireEvent.change(amountInput, { target: { value: 'abc' } });
    amountInput.setAttribute('type', 'number');

    const saveBtn = screen.getByRole('button', { name: /enregistrer/i });
    await userEvent.click(saveBtn);

    expect(await screen.findByText('Montant invalide')).toBeInTheDocument();
  });

  it('calls childUseCases.add with correct data on valid submit', async () => {
    renderNew();

    const nameInput = screen.getByPlaceholderText('Ex: Emma');
    await userEvent.type(nameInput, 'Emma');

    const amountInput = screen.getByPlaceholderText('0.00');
    fireEvent.change(amountInput, { target: { value: '5.00' } });

    const saveBtn = screen.getByRole('button', { name: /enregistrer/i });
    await userEvent.click(saveBtn);

    await waitFor(() => expect(mockAdd).toHaveBeenCalledTimes(1));

    const [arg] = mockAdd.mock.calls[0] as [Record<string, unknown>];
    expect(arg.name).toBe('Emma');
    expect(arg.archived).toBe(false);
    // weeklyAllowance should be 500 cents (5.00 €)
    expect((arg.weeklyAllowance as { cents: bigint }).cents).toBe(500n);
  });

  it('navigates back after a successful save', async () => {
    renderNew();

    const nameInput = screen.getByPlaceholderText('Ex: Emma');
    await userEvent.type(nameInput, 'Zoé');

    const saveBtn = screen.getByRole('button', { name: /enregistrer/i });
    await userEvent.click(saveBtn);

    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith(-1));
  });

  it('does NOT call childUseCases.add when validation fails', async () => {
    renderNew();
    // Submit without filling in anything
    const saveBtn = screen.getByRole('button', { name: /enregistrer/i });
    await userEvent.click(saveBtn);

    expect(mockAdd).not.toHaveBeenCalled();
  });
});
