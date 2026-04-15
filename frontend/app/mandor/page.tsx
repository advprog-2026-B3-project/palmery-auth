"use client";

import Link from "next/link";
import { useRequireRole } from "@/lib/roleGuard";

export default function MandorPage() {
  const { user, initialized } = useRequireRole("MANDOR", "/forbidden");

  if (!initialized) {
    return <p>Checking permissions...</p>;
  }

  return (
    <main className="page page-stack">
      <header className="page-header">
        <h1 className="page-title">Mandor Area</h1>
        <p className="page-subtitle">Only MANDOR users may access this route.</p>
      </header>

      <p className="inline-note">Welcome, {user?.name ?? user?.email ?? "Mandor"}.</p>
      <p className="inline-note">Your role: {user?.role}</p>

      <p className="inline-note">
        <Link className="page-link" href="/dashboard">
          Back to dashboard
        </Link>
      </p>
    </main>
  );
}
