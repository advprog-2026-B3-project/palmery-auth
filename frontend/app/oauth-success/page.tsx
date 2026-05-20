"use client";

import { useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import { introspectToken } from "@/lib/auth-api";
import { getAuthUser, isTokenExpired } from "@/lib/auth-service";
import { useAuth } from "@/context/AuthContext";

export default function OAuthSuccessPage() {
  const router = useRouter();
  const { setToken, logout } = useAuth();
  const [status, setStatus] = useState("Processing Google OAuth login...");
  const [error, setError] = useState<string | null>(null);
  const handledRef = useRef(false);

  useEffect(() => {
    if (typeof window === "undefined" || handledRef.current) {
      return;
    }
    handledRef.current = true;

    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    if (!token) {
      setError("OAuth token missing from redirect.");
      router.replace("/login?error=oauth_missing_token");
      return;
    }

    if (isTokenExpired(token)) {
      logout();
      setError("OAuth token is expired.");
      router.replace("/login?error=oauth_expired");
      return;
    }

    const authUser = getAuthUser(token);
    if (!authUser) {
      logout();
      setError("Unable to read OAuth token payload.");
      router.replace("/login?error=oauth_invalid_token");
      return;
    }

    setStatus("Verifying token with backend...");

    introspectToken(token)
      .then((data) => {
        if (data.active === true) {
          setToken(token);
          router.replace("/dashboard");
          return;
        }

        logout();
        setError("OAuth token is invalid or inactive.");
        router.replace("/login?error=oauth_invalid_token");
      })
      .catch(() => {
        logout();
        setError("OAuth token verification failed.");
        router.replace("/login?error=oauth_invalid_token");
      });
  }, [logout, router, setToken]);

  return (
    <main className="page page-stack">
      <header className="page-header">
        <h1 className="page-title">Google OAuth Login</h1>
        <p className="page-subtitle">Finishing authentication and checking your JWT.</p>
      </header>

      <p>{status}</p>
      {error ? <p className="inline-note">Error: {error}</p> : null}
    </main>
  );
}
