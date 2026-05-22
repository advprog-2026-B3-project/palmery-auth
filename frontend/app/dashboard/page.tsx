"use client";

import Link from "next/link";
import { useState } from "react";
import { fetchProtectedPing } from "@/lib/auth-api";
import { useRequireAuth } from "@/lib/useRequireAuth";
import { useAuth } from "@/lib/useAuth";

export default function DashboardPage() {
  const { token, initialized } = useRequireAuth("/login");
  const { user, logout } = useAuth();
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function callProtected() {
    setResult(null);
    setError(null);
    try {
      const data = await fetchProtectedPing();
      setResult(JSON.stringify(data, null, 2));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unexpected error");
    }
  }

  function handleLogout() {
    logout();
  }

  if (!initialized) {
    return <p>Loading authentication state...</p>;
  }

  return (
    <main className="page page-stack dashboard-page">
      <header className="page-header">
        <h1 className="page-title">Welcome back, {user?.name ?? "User"}</h1>
        <p className="page-subtitle">
          You are signed in as <strong>{user?.role ?? "Unknown"}</strong>. Manage your MySawit workspace here.
        </p>
      </header>

      <section className="summary-grid">
        <div className="summary-card">
          <h2>Quick summary</h2>
          <p>Overview of your role, tasks, and recent activity in the MySawit system.</p>
        </div>
        <div className="summary-card">
          <h3>Account</h3>
          <p>{user?.email ?? "No email"}</p>
        </div>
        <div className="summary-card">
          <h3>Role</h3>
          <p>{user?.role ?? "No role"}</p>
        </div>
      </section>

      <section className="page-card">
        <div className="section-header">
          <h2>Actions</h2>
          <div className="action-row">
            <button className="ui-button ui-button-default ui-button-size-default" onClick={callProtected}>
              Fetch protected backend data
            </button>
            <button className="ui-button ui-button-secondary ui-button-size-default" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </div>

        <div className="info-grid">
          <div className="info-card">
            <strong>Email</strong>
            <p>{user?.email ?? "-"}</p>
          </div>
          <div className="info-card">
            <strong>Role</strong>
            <p>{user?.role ?? "-"}</p>
          </div>
          <div className="info-card">
            <strong>Token active</strong>
            <p>{token ? "Yes" : "No"}</p>
          </div>
        </div>
      </section>

      {result ? <pre className="pre-block">{result}</pre> : null}
      {error ? <p className="inline-note">Error: {error}</p> : null}

      <p className="inline-note">
        <Link className="page-link" href="/">
          Back to home
        </Link>
      </p>
    </main>
  );
}
