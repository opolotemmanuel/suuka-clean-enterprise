import { FormEvent, ReactNode, useEffect, useMemo, useState } from 'react';
import {
  clearBackendSession,
  forgotPassword,
  getBackendSession,
  loadMe,
  login,
  registerCleanerApplication,
  registerClient,
  resetPassword,
  verifyAccount,
  verifyMfa,
  type AuthStatus,
  type BackendUser,
} from '../api/backend';

export const roleDashboardPath: Record<string, string> = {
  CLIENT: '/client/dashboard',
  CLEANER: '/cleaner/dashboard',
  SUPERVISOR: '/supervisor/dashboard',
  CUSTOMER_SUCCESS_MANAGER: '/customer-success/dashboard',
  OPERATIONS_MANAGER: '/operations/dashboard',
  HR_MANAGER: '/hr/dashboard',
  FINANCE_MANAGER: '/finance/dashboard',
  INVENTORY_MANAGER: '/inventory-manager/dashboard',
  SYSTEM_ADMIN: '/system-admin/dashboard',
  EXECUTIVE_ADMIN: '/executive/dashboard',
};

const roleLogins = [
  ['Client', 'client@suukaclean.local'],
  ['Cleaner', 'cleaner@suukaclean.local'],
  ['Supervisor', 'supervisor@suukaclean.local'],
  ['Operations', 'operations@suukaclean.local'],
  ['Customer Success', 'customer.success@suukaclean.local'],
  ['HR', 'hr@suukaclean.local'],
  ['Finance', 'finance@suukaclean.local'],
  ['Inventory', 'inventory@suukaclean.local'],
  ['System Admin', 'system.admin@suukaclean.local'],
  ['Executive', 'executive@suukaclean.local'],
];

export function dashboardPathFor(user?: BackendUser | null) {
  return roleDashboardPath[user?.role ?? 'CLIENT'] ?? '/client/dashboard';
}

function navigate(path: string) {
  window.history.pushState({}, '', path);
  window.dispatchEvent(new PopStateEvent('popstate'));
}

function Shell({ children, narrow = false }: { children: ReactNode; narrow?: boolean }) {
  return (
    <main className={narrow ? 'auth-shell centered' : 'auth-shell'}>
      <section className="auth-brand-panel">
        <div className="auth-logo">
          <i className="fa-solid fa-sparkles" aria-hidden="true" />
          <span>Suuka Clean</span>
        </div>
        <h1>Secure cleaning operations platform</h1>
        <p>Manage cleaning services, teams, customers, payments, and operations in one secure platform.</p>
        <div className="auth-illustration" aria-hidden="true">
          <div><i className="fa-solid fa-shield-halved" /></div>
          <div><i className="fa-solid fa-calendar-check" /></div>
          <div><i className="fa-solid fa-users-gear" /></div>
        </div>
        <p className="auth-trust"><i className="fa-solid fa-lock" aria-hidden="true" /> Protected by role permissions, audit logs, and session controls.</p>
      </section>
      {children}
    </main>
  );
}

function Message({ error, success }: { error?: string | null; success?: string | null }) {
  if (!error && !success) return null;
  return <p className={error ? 'auth-message error' : 'auth-message'}>{error ?? success}</p>;
}

export function LoginPage({ onAuthenticated }: { onAuthenticated: (user: BackendUser) => void }) {
  const [email, setEmail] = useState('admin@suukaclean.local');
  const [password, setPassword] = useState('Password123!');
  const [remember, setRemember] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    try {
      const auth = await login({ email, password });
      const me = await loadMe().catch(() => null);
      if (me?.data.mfaRequired) {
        sessionStorage.setItem('suuka_pending_mfa_email', auth.user.email);
        clearBackendSession();
        navigate('/verify-mfa');
      } else {
        onAuthenticated(auth.user);
        navigate(dashboardPathFor(auth.user));
      }
      if (!remember) sessionStorage.setItem('suuka_session_only', 'true');
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Login failed';
      setError(message);
      if (message.toLowerCase().includes('locked')) navigate('/account-locked');
    }
  }

  return (
    <Shell>
      <form className="auth-card login-card" onSubmit={submit}>
        <p className="auth-eyebrow">Welcome back</p>
        <h2>Login</h2>
        <label>Email or phone number<input value={email} onChange={(event) => setEmail(event.target.value)} /></label>
        <label>Password<input type="password" value={password} onChange={(event) => setPassword(event.target.value)} /></label>
        <label className="auth-check"><input type="checkbox" checked={remember} onChange={(event) => setRemember(event.target.checked)} /> Remember me</label>
        <Message error={error} />
        <button className="primary-button" type="submit"><i className="fa-solid fa-right-to-bracket" /> Login</button>
        <div className="role-login-list" aria-label="Role test logins">
          {roleLogins.map(([label, account]) => (
            <button
              key={account}
              type="button"
              onClick={() => {
                setEmail(account);
                setPassword('Password123!');
              }}
            >
              {label}
            </button>
          ))}
        </div>
        <div className="auth-links">
          <button type="button" onClick={() => navigate('/forgot-password')}>Forgot Password</button>
          <button type="button" onClick={() => navigate('/register')}>Create Account</button>
        </div>
      </form>
    </Shell>
  );
}

export function RegisterPage() {
  const [tab, setTab] = useState<'client' | 'cleaner'>('client');
  const [form, setForm] = useState<Record<string, string>>({});
  const [terms, setTerms] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  function update(key: string, value: string) {
    setForm((current) => ({ ...current, [key]: value }));
  }

  async function submit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setMessage(null);
    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    try {
      if (tab === 'client') {
        await registerClient({
          fullName: form.fullName ?? '',
          email: form.email ?? '',
          phoneNumber: form.phoneNumber ?? '',
          password: form.password ?? '',
          address: form.address ?? '',
          zone: form.zone ?? '',
          termsAccepted: terms,
        });
        navigate('/verify-account');
      } else {
        await registerCleanerApplication({
          fullName: form.fullName ?? '',
          email: form.email ?? '',
          phoneNumber: form.phoneNumber ?? '',
          nationalId: form.nationalId ?? '',
          location: form.location ?? '',
          experienceLevel: form.experienceLevel ?? '',
          availability: form.availability ?? '',
          password: form.password ?? '',
          idDocumentName: form.idDocumentName ?? '',
          profilePhotoName: form.profilePhotoName ?? '',
        });
        navigate('/application-status');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed');
    }
  }

  const clientFields = ['fullName', 'email', 'phoneNumber', 'address', 'zone', 'password', 'confirmPassword'];
  const cleanerFields = ['fullName', 'email', 'phoneNumber', 'nationalId', 'location', 'experienceLevel', 'availability', 'password', 'confirmPassword', 'idDocumentName', 'profilePhotoName'];
  const fields = tab === 'client' ? clientFields : cleanerFields;

  return (
    <Shell narrow>
      <form className="auth-card register-card" onSubmit={submit}>
        <h2>Create account</h2>
        <div className="auth-tabs">
          <button type="button" className={tab === 'client' ? 'active' : ''} onClick={() => setTab('client')}>Client Account</button>
          <button type="button" className={tab === 'cleaner' ? 'active' : ''} onClick={() => setTab('cleaner')}>Cleaner Application</button>
        </div>
        <div className="auth-grid">
          {fields.map((field) => (
            <label key={field}>{field.replace(/([A-Z])/g, ' $1')}
              <input type={field.toLowerCase().includes('password') ? 'password' : 'text'} value={form[field] ?? ''} onChange={(event) => update(field, event.target.value)} />
            </label>
          ))}
        </div>
        {tab === 'client' && <label className="auth-check"><input type="checkbox" checked={terms} onChange={(event) => setTerms(event.target.checked)} /> I agree to the terms</label>}
        <Message error={error} success={message} />
        <button className="primary-button" type="submit">{tab === 'client' ? 'Create Client Account' : 'Submit Application'}</button>
        <button className="auth-text-button" type="button" onClick={() => navigate('/login')}>Back to login</button>
      </form>
    </Shell>
  );
}

export function VerifyAccountPage() {
  const [email, setEmail] = useState(getBackendSession()?.user.email ?? '');
  const [code, setCode] = useState('123456');
  const [error, setError] = useState<string | null>(null);

  async function submit(event: FormEvent) {
    event.preventDefault();
    try {
      const status = await verifyAccount({ email, code });
      navigate(status.cleanerApplicationStatus ? '/application-status' : dashboardPathFor(status.user));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Verification failed');
    }
  }

  return <CodeCard title="Verify Account" email={email} setEmail={setEmail} code={code} setCode={setCode} error={error} onSubmit={submit} />;
}

export function VerifyMfaPage({ onAuthenticated }: { onAuthenticated: (user: BackendUser) => void }) {
  const [email, setEmail] = useState(sessionStorage.getItem('suuka_pending_mfa_email') ?? getBackendSession()?.user.email ?? 'admin@suukaclean.local');
  const [code, setCode] = useState('123456');
  const [error, setError] = useState<string | null>(null);

  async function submit(event: FormEvent) {
    event.preventDefault();
    try {
      const auth = await verifyMfa({ email, code });
      sessionStorage.removeItem('suuka_pending_mfa_email');
      onAuthenticated(auth.user);
      navigate(dashboardPathFor(auth.user));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'MFA failed');
    }
  }

  return <CodeCard title="Verify MFA" email={email} setEmail={setEmail} code={code} setCode={setCode} error={error} onSubmit={submit} />;
}

function CodeCard(props: { title: string; email: string; setEmail: (value: string) => void; code: string; setCode: (value: string) => void; error: string | null; onSubmit: (event: FormEvent) => void }) {
  return (
    <Shell narrow>
      <form className="auth-card compact-card" onSubmit={props.onSubmit}>
        <h2>{props.title}</h2>
        <label>Email<input value={props.email} onChange={(event) => props.setEmail(event.target.value)} /></label>
        <label>OTP code, 6 digits<input inputMode="numeric" maxLength={6} value={props.code} onChange={(event) => props.setCode(event.target.value)} /></label>
        <Message error={props.error} />
        <button className="primary-button" type="submit"><i className="fa-solid fa-shield-halved" /> Verify</button>
        <button className="auth-text-button" type="button">Resend Code</button>
      </form>
    </Shell>
  );
}

export function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState<string | null>(null);
  async function submit(event: FormEvent) {
    event.preventDefault();
    setMessage(await forgotPassword({ email }));
  }
  return (
    <Shell narrow>
      <form className="auth-card compact-card" onSubmit={submit}>
        <h2>Forgot Password</h2>
        <label>Email or phone number<input value={email} onChange={(event) => setEmail(event.target.value)} /></label>
        <Message success={message} />
        <button className="primary-button" type="submit">Send Reset Link / Code</button>
      </form>
    </Shell>
  );
}

export function ResetPasswordPage() {
  const [token, setToken] = useState(new URLSearchParams(window.location.search).get('token') ?? '');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string | null>(null);

  async function submit(event: FormEvent) {
    event.preventDefault();
    if (newPassword !== confirmPassword) return setError('Passwords do not match');
    if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/.test(newPassword)) return setError('Password does not meet the security rules');
    await resetPassword({ token, newPassword });
    navigate('/login');
  }

  return (
    <Shell narrow>
      <form className="auth-card reset-card" onSubmit={submit}>
        <h2>Reset Password</h2>
        <label>Reset token<input value={token} onChange={(event) => setToken(event.target.value)} /></label>
        <label>New password<input type="password" value={newPassword} onChange={(event) => setNewPassword(event.target.value)} /></label>
        <label>Confirm password<input type="password" value={confirmPassword} onChange={(event) => setConfirmPassword(event.target.value)} /></label>
        <Message error={error} />
        <button className="primary-button" type="submit">Reset Password</button>
      </form>
    </Shell>
  );
}

export function SimpleAuthPage({ type }: { type: 'locked' | 'expired' | 'unauthorized' | 'onboarding' }) {
  const copy = {
    locked: ['Account temporarily locked', 'Please try again later or reset your password.'],
    expired: ['Your session has expired for security reasons.', 'Login again to continue.'],
    unauthorized: ['You do not have permission to access this page.', 'Go to your correct dashboard or contact support.'],
    onboarding: ['First-time setup', 'Complete your profile, preferences, payment method, documents, and permissions setup.'],
  }[type];
  return (
    <Shell narrow>
      <section className="auth-card compact-card">
        <h2>{copy[0]}</h2>
        <p>{copy[1]}</p>
        <button className="primary-button" type="button" onClick={() => navigate(type === 'unauthorized' ? dashboardPathFor(getBackendSession()?.user) : '/login')}>
          {type === 'unauthorized' ? 'Go to My Dashboard' : 'Login Again'}
        </button>
        {type === 'locked' && <button className="secondary-button" type="button" onClick={() => navigate('/forgot-password')}>Reset password</button>}
      </section>
    </Shell>
  );
}

export function ApplicationStatusPage() {
  const [status, setStatus] = useState<AuthStatus | null>(null);
  useEffect(() => {
    loadMe().then((response) => setStatus(response.data)).catch(() => setStatus(null));
  }, []);
  const state = status?.cleanerApplicationStatus ?? 'PENDING_REVIEW';
  return (
    <Shell narrow>
      <section className="auth-card compact-card">
        <h2>Application Status</h2>
        <div className="application-status">{state}</div>
        <p>{status?.reviewNotes ?? 'Your application is stored and waiting for review.'}</p>
        <div className="submitted-docs">
          {(status?.submittedDocuments ?? []).map((document) => <span key={document}>{document}</span>)}
        </div>
        {state === 'APPROVED' && <button className="primary-button" type="button" onClick={() => navigate('/cleaner/dashboard')}>Continue to Cleaner Dashboard</button>}
        {state === 'MORE_INFORMATION_REQUIRED' && <button className="secondary-button" type="button">Upload Missing Documents</button>}
      </section>
    </Shell>
  );
}

export function usePathname() {
  const [path, setPath] = useState(window.location.pathname);
  useEffect(() => {
    const listener = () => setPath(window.location.pathname);
    window.addEventListener('popstate', listener);
    return () => window.removeEventListener('popstate', listener);
  }, []);
  return useMemo(() => path, [path]);
}
