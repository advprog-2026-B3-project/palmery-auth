import { getToken, clearToken } from "@/lib/auth-storage";
import { decodeJwt } from "@/lib/auth-service";

function isTokenExpired(token: string): boolean {
  const payload = decodeJwt(token);
  if (!payload || typeof payload.exp !== "number") {
    return true;
  }
  return payload.exp * 1000 <= Date.now();
}

export async function fetchWithAuth(input: RequestInfo | URL, init: RequestInit = {}) {
  const token = getToken();
  const headers = new Headers(init.headers || {});

  if (token) {
    if (isTokenExpired(token)) {
      clearToken();
      if (typeof window !== "undefined") {
        window.location.href = "/login?error=session_expired";
      }
      throw new Error("Token expired");
    }
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(input, { ...init, headers });
  if (response.status === 401) {
    clearToken();
    if (typeof window !== "undefined") {
      window.location.href = "/login?error=session_unauthorized";
    }
  }
  return response;
}

