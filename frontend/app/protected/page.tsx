"use client";

import { useState } from "react";
import Link from "next/link";
import { fetchProtectedPing, logout, getStoredToken } from "@/lib/auth-api";

export default function ProtectedTestPage() {
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

  function onLogout() {
    logout();
    setResult(null);
    setError(null);
  }

  return (
    <main className="page page-stack">
      <header className="page-header">
        <h1 className="page-title">Protected Request Test</h1>
        <p className="page-subtitle">
          Menguji pemanggilan endpoint backend yang membutuhkan Authorization: Bearer token.
        </p>
      </header>

      <div className="action-row">
        <button className="ui-button ui-button-default ui-button-size-default" onClick={callProtected}>
          Call /api/protected/ping
        </button>
        <button className="ui-button ui-button-secondary ui-button-size-default" onClick={onLogout}>
          Logout (clear token)
        </button>
      </div>

      <p className="inline-note">Token tersimpan: {getStoredToken() ? "YA" : "TIDAK"}</p>

      {result ? (
        <pre className="pre-block">{result}</pre>
      ) : null}

      {error ? (
        <p className="inline-note">Error: {error}</p>
      ) : null}

      <p className="inline-note">
        <Link className="page-link" href="/">
          Back to home
        </Link>
      </p>
    </main>
  );
}

