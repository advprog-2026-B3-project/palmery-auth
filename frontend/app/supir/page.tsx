"use client";

import Link from "next/link";
import { useRequireRole } from "@/lib/roleGuard";

export default function SupirPage() {
  const { user, initialized } = useRequireRole("SUPIR", "/forbidden");

  if (!initialized) {
    return <p>Checking permissions...</p>;
  }

  return (
    <main className="page page-stack">
      <header className="page-header">
        <h1 className="page-title">Supir Area</h1>
        <p className="page-subtitle">Only SUPIR users may access this route.</p>
      </header>

      <p className="inline-note">Welcome, {user?.name ?? user?.email ?? "Supir"}.</p>
      <p className="inline-note">Your role: {user?.role}</p>

      <p className="inline-note">
        <Link className="page-link" href="/dashboard">
          Back to dashboard
        </Link>
      </p>
    </main>
  );
}
