"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { introspectToken, loginForToken, TokenResponse } from "@/lib/auth-api";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";

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
      setStatus("Authenticated. Access token generated.");
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

    setStatus("Running token introspection...");
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
    <main className="page page-stack">
      <header className="page-header">
        <h1 className="page-title">Login + Token</h1>
        <p className="page-subtitle">Exchange user credentials for OAuth-style access token.</p>
      </header>

      <Card>
        <CardHeader>
          <CardTitle>Credential Login</CardTitle>
          <CardDescription>Uses grant type password via auth backend token endpoint.</CardDescription>
        </CardHeader>
        <CardContent>
          <form className="form-grid" onSubmit={onSubmit}>
            <div className="field">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="debug@example.com"
                value={form.email}
                onChange={(event) => setForm({ ...form, email: event.target.value })}
                required
              />
            </div>

            <div className="field">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                placeholder="********"
                value={form.password}
                onChange={(event) => setForm({ ...form, password: event.target.value })}
                required
              />
            </div>

            <div className="action-row">
              <Button type="submit">Login</Button>
              <Button type="button" variant="secondary" onClick={() => setForm(initialState)}>
                Reset
              </Button>
              <Button type="button" variant="outline" onClick={runIntrospection} disabled={!token?.access_token}>
                Introspect Token
              </Button>
            </div>
          </form>

          {status ? (
            <p>
              <Badge variant="success">status</Badge> {status}
            </p>
          ) : null}

          {error ? (
            <p>
              <Badge variant="danger">error</Badge> {error}
            </p>
          ) : null}

          {token ? (
            <div className="page-stack">
              <div className="stats-grid">
                <div className="stat-card">
                  <p className="stat-label">Token Type</p>
                  <p className="stat-value">{token.token_type}</p>
                </div>
                <div className="stat-card">
                  <p className="stat-label">Expires In</p>
                  <p className="stat-value">{token.expires_in} sec</p>
                </div>
              </div>
              <pre className="pre-block">{token.access_token}</pre>
            </div>
          ) : null}

          {introspection ? <pre className="pre-block">{JSON.stringify(introspection, null, 2)}</pre> : null}
        </CardContent>
      </Card>

      <p className="inline-note">
        <Link className="page-link" href="/">
          Back to home
        </Link>
      </p>
    </main>
  );
}
