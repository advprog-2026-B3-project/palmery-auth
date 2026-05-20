const DEFAULT_ALLOWED_ORIGINS = [
  "http://localhost:3001",
  "http://127.0.0.1:3001",
];

export function getReturnUrlFromSearch(search: string): string | null {
  if (typeof window === "undefined") {
    return null;
  }
  const params = new URLSearchParams(search);
  const raw = params.get("returnUrl");
  if (!raw) {
    return null;
  }
  return sanitizeReturnUrl(raw);
}

export function sanitizeReturnUrl(raw: string): string | null {
  try {
    const url = new URL(raw);
    const allowed = (
      process.env.NEXT_PUBLIC_ALLOWED_RETURN_ORIGINS?.split(",") ??
      DEFAULT_ALLOWED_ORIGINS
    ).map((origin) => origin.trim());

    if (!allowed.includes(url.origin)) {
      return null;
    }
    if (!url.pathname.startsWith("/auth/callback")) {
      return null;
    }
    return url.toString();
  } catch {
    return null;
  }
}

export function buildCallbackWithToken(returnUrl: string, token: string): string {
  const url = new URL(returnUrl);
  url.searchParams.set("token", token);
  return url.toString();
}
