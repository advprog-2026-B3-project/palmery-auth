"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { introspectToken, loginForToken, TokenResponse } from "@/lib/auth-api";

type LoginState = {
  email: string;
  password: string;
};

const initialState: LoginState = {
  email: "",
  password: "",
};

export default function LoginPage() {
  const [form, setForm] = useState<LoginState>(initialState);
  const [token, setToken] = useState<TokenResponse | null>(null);
  const [introspection, setIntrospection] = useState<Record<string, unknown> | null>(null);
  const [status, setStatus] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setStatus("Authenticating...");
    setError(null);
    setIntrospection(null);

    try {
      const tokenResponse = await loginForToken(form.email, form.password);
      setToken(tokenResponse);
      setStatus("Authenticated. Token issued.");
    } catch (err) {
      setToken(null);
      setStatus(null);
      setError(err instanceof Error ? err.message : "Unexpected error");
    }
  }

  async function runIntrospection() {
    if (!token?.access_token) {
      return;
    }

    setStatus("Introspecting token...");
    setError(null);

    try {
      const response = await introspectToken(token.access_token);
      setIntrospection(response);
      setStatus("Introspection completed.");
    } catch (err) {
      setIntrospection(null);
      setStatus(null);
      setError(err instanceof Error ? err.message : "Unexpected error");
    }
  }

  return (
    <main>
      <h1>Login</h1>
      <p className="muted">Exchanges credentials for a JWT token.</p>
      <div className="card">
        <form onSubmit={onSubmit}>
          <input
            type="email"
            placeholder="Email"
            value={form.email}
            onChange={(event) => setForm({ ...form, email: event.target.value })}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={form.password}
            onChange={(event) => setForm({ ...form, password: event.target.value })}
            required
          />
          <button type="submit">Login</button>
        </form>

        {token && (
          <>
            <p className="status-ok">Token type: {token.token_type}</p>
            <p className="muted">Expires in: {token.expires_in} seconds</p>
            <pre>{token.access_token}</pre>
            <button type="button" onClick={runIntrospection}>
              Introspect Token
            </button>
          </>
        )}

        {introspection && (
          <>
            <h2>Introspection</h2>
            <pre>{JSON.stringify(introspection, null, 2)}</pre>
          </>
        )}

        {status && <p className="status-ok">{status}</p>}
        {error && <p className="status-err">{error}</p>}
      </div>
      <p>
        <Link href="/">Back to home</Link>
      </p>
    </main>
  );
}
