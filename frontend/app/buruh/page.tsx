"use client";

import Link from "next/link";
import { useRequireRole } from "@/lib/roleGuard";

export default function BuruhPage() {
  const { user, initialized } = useRequireRole("BURUH", "/forbidden");

  if (!initialized) {
    return <p>Checking permissions...</p>;
  }

  return (
    <main className="page page-stack">
      <header className="page-header">
        <h1 className="page-title">Buruh Area</h1>
        <p className="page-subtitle">Only BURUH users may access this route.</p>
      </header>

      <p className="inline-note">Welcome, {user?.name ?? user?.email ?? "Buruh"}.</p>
      <p className="inline-note">Your role: {user?.role}</p>

      <p className="inline-note">
        <Link className="page-link" href="/dashboard">
          Back to dashboard
        </Link>
      </p>
    </main>
  );
}
