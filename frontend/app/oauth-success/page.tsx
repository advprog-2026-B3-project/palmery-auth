"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { introspectToken } from "@/lib/auth-api";
import { getAuthUser, isTokenExpired } from "@/lib/auth-service";
import { useAuth } from "@/context/AuthContext";

export default function OAuthSuccessPage() {
  const router = useRouter();
  const auth = useAuth();
  const [status, setStatus] = useState("Processing Google OAuth login...");
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (typeof window === "undefined") {
      return;
    }

    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    if (!token) {
      setError("OAuth token missing from redirect.");
      router.replace("/login?error=oauth_missing_token");
      return;
    }

    if (isTokenExpired(token)) {
      auth.logout();
      setError("OAuth token is expired.");
      router.replace("/login?error=oauth_expired");
      return;
    }

    const authUser = getAuthUser(token);
    if (!authUser) {
      auth.logout();
      setError("Unable to read OAuth token payload.");
      router.replace("/login?error=oauth_invalid_token");
      return;
    }

    auth.setToken(token);
    setStatus("Verifying token with backend...");

    introspectToken(token)
      .then((data) => {
        if (data.active) {
          router.replace("/dashboard");
        } else {
          auth.logout();
          setError("OAuth token is invalid or inactive.");
          router.replace("/login?error=oauth_invalid_token");
        }
      })
      .catch(() => {
        auth.logout();
        setError("OAuth token verification failed.");
        router.replace("/login?error=oauth_invalid_token");
      });
  }, [auth, router]);

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
