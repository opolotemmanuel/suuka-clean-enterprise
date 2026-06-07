const TOKEN_KEY = 'suuka_backend_access_token';
const REFRESH_TOKEN_KEY = 'suuka_backend_refresh_token';
const USER_KEY = 'suuka_backend_user';

type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};

export type BackendUser = {
  id: string;
  fullName: string;
  email: string;
  role: string;
  permissions: string[];
  branch?: string;
  zone?: string;
};

export type AuthStatus = {
  user: BackendUser;
  mfaRequired: boolean;
  accountVerified: boolean;
  lockedUntil?: string | null;
  cleanerApplicationStatus?: 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'MORE_INFORMATION_REQUIRED' | null;
  reviewNotes?: string | null;
  submittedDocuments: string[];
};

type AuthPayload = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: BackendUser;
};

export type BackendSession = {
  token: string;
  user: BackendUser;
};

export type DashboardMetric = {
  key: string;
  label: string;
  value: string;
  icon: string;
  trend?: string | null;
};

export type DashboardAction = {
  label: string;
  actionType: string;
  targetModule: string;
  targetRoute: string;
  requiredPermission: string;
};

export type DashboardSummary = {
  role: string;
  metrics: DashboardMetric[];
  quickActions: DashboardAction[];
  activity: string[];
  emptyStates: string[];
};

export type BackendNotification = {
  id: string;
  type: string;
  title: string;
  message: string;
  relatedModule: string;
  relatedEntityId?: string | null;
  targetRole: string;
  read: boolean;
  availableActions: string[];
  createdAt?: string;
};

export type AIRecommendationRecord = {
  id: string;
  recommendationTitle: string;
  recommendationBody: string;
  insightCategory?: string | null;
  riskLevel?: string | null;
  generatedForRole?: string | null;
  aiStatus: string;
  relatedEntityType?: string | null;
  relatedEntityId?: string | null;
  createdAt?: string;
};

export function getBackendSession(): BackendSession | null {
  const token = localStorage.getItem(TOKEN_KEY);
  const userJson = localStorage.getItem(USER_KEY);
  if (!token || !userJson) return null;

  try {
    return { token, user: JSON.parse(userJson) as BackendUser };
  } catch {
    clearBackendSession();
    return null;
  }
}

export function clearBackendSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

export function storeBackendAuth(auth: AuthPayload) {
  localStorage.setItem(TOKEN_KEY, auth.accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, auth.refreshToken);
  localStorage.setItem(USER_KEY, JSON.stringify(auth.user));
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
}

export async function connectDemoBackendSession(): Promise<BackendSession> {
  const email = 'demo.client@suukaclean.local';
  const password = 'Password123!';
  const fullName = 'Demo Client';

  let auth = await authenticate('/api/auth/login', { email, password });
  if (!auth) {
    auth = await authenticate('/api/auth/register', {
      fullName,
      email,
      password,
      branch: 'HQ',
      zone: 'Central',
    });
  }

  if (!auth) {
    throw new Error('Backend login/register failed');
  }

  storeBackendAuth(auth);
  return { token: auth.accessToken, user: auth.user };
}

export async function backendFetch<T>(path: string, init: RequestInit = {}): Promise<ApiResponse<T>> {
  return backendFetchWithRetry<T>(path, init, true);
}

async function backendFetchWithRetry<T>(path: string, init: RequestInit, allowReconnect: boolean): Promise<ApiResponse<T>> {
  const session = getBackendSession();
  const headers = new Headers(init.headers);
  headers.set('Content-Type', headers.get('Content-Type') ?? 'application/json');
  if (session?.token) {
    headers.set('Authorization', `Bearer ${session.token}`);
  }

  const response = await fetch(path, { ...init, headers });
  const body = (await response.json().catch(() => null)) as ApiResponse<T> | null;
  if ((response.status === 401 || response.status === 403) && allowReconnect && !path.startsWith('/api/auth/')) {
    clearBackendSession();
    await connectDemoBackendSession();
    return backendFetchWithRetry<T>(path, init, false);
  }
  if (!body) {
    throw new Error(`Backend request failed with ${response.status}`);
  }
  if (!response.ok || !body.success) {
    throw new Error(body.message || `Backend request failed with ${response.status}`);
  }
  return body;
}

export async function loadDashboardSummary() {
  return backendFetch<DashboardSummary>('/api/dashboard/summary');
}

export async function loadNotifications() {
  return backendFetch<BackendNotification[]>('/api/notifications');
}

export async function markNotificationRead(id: string) {
  return backendFetch<BackendNotification>(`/api/notifications/${id}/read`, { method: 'PATCH' });
}

export async function loadBackendData<T = unknown>(path: string) {
  return backendFetch<T>(path);
}

export async function loadAIRecommendations() {
  return backendFetch<AIRecommendationRecord[]>('/api/admin/ai/recommendations');
}

export type BookingDto = {
  id: string;
  clientId: string;
  cleanerId?: string | null;
  serviceType: string;
  propertyAddress: string;
  scheduledAt: string;
  durationHours: number;
  status: string;
  completionNotes?: string | null;
};

export async function createBooking(payload: {
  serviceType: string;
  propertyAddress: string;
  latitude: number;
  longitude: number;
  scheduledAt: string;
  durationHours: number;
  paymentMethod: string;
}) {
  return backendFetch<BookingDto>('/api/client/bookings', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function loadOwnBookings() {
  return backendFetch<BookingDto[]>('/api/client/bookings');
}

export async function cancelOwnBooking(id: string) {
  return backendFetch<BookingDto>(`/api/client/bookings/${id}/cancel`, { method: 'POST' });
}

async function authenticate(path: string, payload: Record<string, string>): Promise<AuthPayload | null> {
  const response = await fetch(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  if (!response.ok) return null;
  const body = (await response.json()) as ApiResponse<AuthPayload>;
  return body.success ? body.data : null;
}

export async function login(payload: { email: string; password: string }) {
  const auth = await authenticate('/api/auth/login', payload);
  if (!auth) throw new Error('Invalid email or password');
  storeBackendAuth(auth);
  return auth;
}

export async function registerClient(payload: Record<string, string | boolean>) {
  const response = await fetch('/api/auth/register/client', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  const body = (await response.json()) as ApiResponse<AuthPayload>;
  if (!response.ok || !body.success) throw new Error(body.message || 'Registration failed');
  storeBackendAuth(body.data);
  return body.data;
}

export async function registerCleanerApplication(payload: Record<string, string>) {
  const response = await fetch('/api/auth/register/cleaner-application', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  const body = (await response.json()) as ApiResponse<AuthStatus>;
  if (!response.ok || !body.success) throw new Error(body.message || 'Application failed');
  return body.data;
}

export async function verifyAccount(payload: { email: string; code: string }) {
  const response = await fetch('/api/auth/verify-account', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  const body = (await response.json()) as ApiResponse<AuthStatus>;
  if (!response.ok || !body.success) throw new Error(body.message || 'Verification failed');
  return body.data;
}

export async function verifyMfa(payload: { email: string; code: string }) {
  const auth = await authenticate('/api/auth/verify-mfa', payload);
  if (!auth) throw new Error('MFA verification failed');
  storeBackendAuth(auth);
  return auth;
}

export async function forgotPassword(payload: { email: string }) {
  const response = await fetch('/api/auth/forgot-password', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  const body = (await response.json()) as ApiResponse<void>;
  if (!response.ok || !body.success) throw new Error(body.message || 'Password reset request failed');
  return body.message;
}

export async function resetPassword(payload: { token: string; newPassword: string }) {
  const response = await fetch('/api/auth/reset-password', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  const body = (await response.json()) as ApiResponse<void>;
  if (!response.ok || !body.success) throw new Error(body.message || 'Password reset failed');
  return body.message;
}

export async function loadMe() {
  return backendFetch<AuthStatus>('/api/auth/me');
}
