const API_BASE = process.env.NEXT_PUBLIC_AUTH_API_BASE_URL ?? "http://localhost:8080";

export type RegisterPayload = {
  name: string;
  email: string;
  password: string;
  role: string;
};

export type TokenResponse = {
  access_token: string;
  token_type: string;
  expires_in: number;
  scope?: string;
};

export async function registerUser(payload: RegisterPayload): Promise<string> {
  const response = await fetch(`${API_BASE}/api/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.message ?? "Register failed");
  }

  return data.message ?? "registered";
}

export async function loginForToken(email: string, password: string): Promise<TokenResponse> {
  const response = await fetch(`${API_BASE}/api/auth/token`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ grant_type: "password", email, password }),
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.message ?? "Login failed");
  }

  return data as TokenResponse;
}

export async function introspectToken(token: string): Promise<Record<string, unknown>> {
  const response = await fetch(`${API_BASE}/api/auth/introspect`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ token }),
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.message ?? "Introspection failed");
  }

  return data as Record<string, unknown>;
}

export async function fetchIntegrationStatus(): Promise<Record<string, unknown>> {
  const response = await fetch(`${API_BASE}/api/debug/integration`, {
    method: "GET",
    headers: { "Content-Type": "application/json" },
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error("Cannot load integration status");
  }

  return data as Record<string, unknown>;
}

export async function createDebugEvent(source = "frontend-debug"): Promise<Record<string, unknown>> {
  const response = await fetch(`${API_BASE}/api/debug/events`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ source }),
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error("Cannot create debug event");
  }

  return data as Record<string, unknown>;
}

export async function fetchDebugEvents(): Promise<Record<string, unknown>[]> {
  const response = await fetch(`${API_BASE}/api/debug/events`, {
    method: "GET",
    headers: { "Content-Type": "application/json" },
  });

  const data = await response.json().catch(() => []);
  if (!response.ok) {
    throw new Error("Cannot fetch debug events");
  }

  return Array.isArray(data) ? (data as Record<string, unknown>[]) : [];
}
