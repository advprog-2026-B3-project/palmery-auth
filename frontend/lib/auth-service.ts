import { getToken as readToken, setToken as persistToken, clearToken as removeToken } from "@/lib/auth-storage";

export type AuthTokenPayload = {
  email?: string;
  name?: string;
  role?: string;
  exp?: number;
  [key: string]: unknown;
};

export type AuthUser = {
  email?: string;
  name?: string;
  role?: string;
  exp?: number;
  isExpired: boolean;
};

const API_BASE = process.env.NEXT_PUBLIC_AUTH_API_BASE_URL ?? "http://localhost:8080";

function base64UrlDecode(value: string): string {
  const padded = value.padEnd(value.length + (4 - (value.length % 4)) % 4, "=");
  const base64 = padded.replace(/-/g, "+").replace(/_/g, "/");

  if (typeof window !== "undefined" && typeof window.atob === "function") {
    return window.atob(base64);
  }

  return Buffer.from(base64, "base64").toString("utf-8");
}

export function decodeJwt<T extends AuthTokenPayload = AuthTokenPayload>(token: string): T | null {
  if (!token || typeof token !== "string") {
    return null;
  }

  const parts = token.split(".");
  if (parts.length !== 3) {
    return null;
  }

  try {
    const payload = base64UrlDecode(parts[1]);
    return JSON.parse(payload) as T;
  } catch {
    return null;
  }
}

export function normalizeRole(rawRole?: string): string | null {
  if (!rawRole) {
    return null;
  }

  const normalized = rawRole.trim().toUpperCase();
  if (["WORKER", "BURUH", "PEKERJA"].includes(normalized)) return "BURUH";
  if (["SUPIR", "DRIVER"].includes(normalized)) return "SUPIR";
  if (["SUPERVISOR", "MANDOR"].includes(normalized)) return "MANDOR";
  if (["ADMIN", "ADMIN_UTAMA"].includes(normalized)) return "ADMIN";
  return normalized;
}

export function isTokenExpired(token: string): boolean {
  const payload = decodeJwt(token);
  if (!payload || typeof payload.exp !== "number") {
    return true;
  }

  return payload.exp * 1000 <= Date.now();
}

export function getAuthUser(token: string): AuthUser | null {
  const payload = decodeJwt(token);
  if (!payload) {
    return null;
  }

  const role = normalizeRole(typeof payload.role === "string" ? payload.role : undefined);
  return {
    email: typeof payload.email === "string" ? payload.email : undefined,
    name: typeof payload.name === "string" ? payload.name : undefined,
    role: role ?? undefined,
    exp: typeof payload.exp === "number" ? payload.exp : undefined,
    isExpired: typeof payload.exp === "number" ? payload.exp * 1000 <= Date.now() : true,
  };
}

export function getAuthToken(): string | null {
  return readToken();
}

export function saveAuthToken(token: string): void {
  persistToken(token);
}

export function clearAuthToken(): void {
  removeToken();
}

export function isAuthenticated(): boolean {
  const token = getAuthToken();
  return Boolean(token && !isTokenExpired(token));
}

export function redirectToLogin(error?: string): void {
  if (typeof window !== "undefined") {
    const query = error ? `?error=${error}` : "";
    window.location.href = `/login${query}`;
  }
}

export async function authFetch(input: RequestInfo | URL, init: RequestInit = {}) {
  const token = getAuthToken();
  const headers = new Headers(init.headers || {});

  if (token) {
    if (isTokenExpired(token)) {
      clearAuthToken();
      redirectToLogin("session_expired");
      throw new Error("Token expired");
    }
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(input, { ...init, headers });

  if (response.status === 401) {
    clearAuthToken();
    redirectToLogin("session_unauthorized");
  }

  return response;
}

export function getAuthApiBase(): string {
  return API_BASE;
}
