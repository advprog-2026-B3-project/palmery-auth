"use client";

import "./login.css";
import Link from "next/link";
import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { loginForToken } from "@/lib/auth-api";
import { useAuth } from "@/context/AuthContext";
import { getAuthApiBase } from "@/lib/auth-service";
import {
  buildCallbackWithToken,
  getReturnUrlFromSearch,
  getDefaultReturnUrl,
} from "@/lib/return-url";

export default function LoginPage() {
  const googleAuthUrl = `${getAuthApiBase()}/auth/google`;
  const router = useRouter();
  const auth = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [status, setStatus] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [returnUrl, setReturnUrl] = useState<string | null>(null);
  const registerHref = returnUrl
    ? `/register?returnUrl=${encodeURIComponent(returnUrl)}`
    : "/register";

  useEffect(() => {
    if (typeof window === "undefined") {
      return;
    }

    const params = new URLSearchParams(window.location.search);
    const oauthError = params.get("error");
    if (oauthError) {
      setError(oauthError.replace(/_/g, " "));
    }
    setReturnUrl(
      getReturnUrlFromSearch(window.location.search) ?? getDefaultReturnUrl(),
    );
  }, []);

  async function onSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setStatus("Authenticating...");
    setError(null);

    try {
      const tokenResponse = await loginForToken(email, password);
      if (returnUrl) {
        auth.logout();
        setStatus("Login berhasil. Mengalihkan ke aplikasi utama...");
        window.location.href = buildCallbackWithToken(
          returnUrl,
          tokenResponse.access_token,
        );
        return;
      }
      auth.setToken(tokenResponse.access_token);
      setStatus("Login berhasil. Redirecting to dashboard...");
      router.replace("/dashboard");
    } catch (err) {
      setStatus(null);
      setError(err instanceof Error ? err.message : "Unexpected error");
    }
  }

  return (
    <div className="login-container">
      
      {/* LEFT SIDE */}
      <div className="login-left">
        <img
          src="/palmery.svg"
          alt="Palmery illustration"
          className="login-image"
        />
        <h1 className="brand">Palmery</h1>
        <p className="brand-subtitle">Access your account and manage orders, inventory, and users with a clean dashboard experience.</p>
      </div>

      {/* RIGHT SIDE */}
      <div className="login-right">

        <div className="login-card">
          <h2 className="title">Sign In</h2>
          <p className="subtitle">Enter your email and password to continue to your MySawit account.</p>

          {error ? <div className="auth-banner auth-banner-error">{error}</div> : null}
          {status ? <div className="auth-banner auth-banner-success">{status}</div> : null}

          <form onSubmit={onSubmit}>

            <label>Email</label>
            <input
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />

            <label>Password</label>
            <input
              type="password"
              placeholder="********"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            <button type="submit">Log In</button>
          </form>

          <div className="oauth-actions">
            <p>or continue with</p>
            <a
              className="google-button"
              href={
                returnUrl
                  ? `${googleAuthUrl}?returnUrl=${encodeURIComponent(returnUrl)}`
                  : googleAuthUrl
              }
            >
              <span className="google-button-icon">G</span>
              Login with Google
            </a>
          </div>

          <div className="login-footer">
            <p className="links">
              Don’t have an account? <Link href={registerHref}>Create Account</Link>
            </p>
            <p className="links">
              Forgot Password? <Link href="#">Reset</Link>
            </p>
          </div>
        </div>

      </div>

    </div>
  );
}
