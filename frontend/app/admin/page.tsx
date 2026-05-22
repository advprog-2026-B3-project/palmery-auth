"use client";

import Link from "next/link";
import { useRequireRole } from "@/lib/roleGuard";
import { useAuth } from "@/lib/useAuth";

export default function AdminPage() {
  const { user, initialized } = useRequireRole("ADMIN", "/forbidden");

  if (!initialized) {
    return <p>Checking permissions...</p>;
  }

  return (
    <main className="page page-stack">
      <header className="page-header">
        <h1 className="page-title">Admin Area</h1>
        <p className="page-subtitle">Only ADMIN users can view this page.</p>
      </header>

      <p className="inline-note">Welcome, {user?.name ?? user?.email ?? "Admin"}.</p>
      <p className="inline-note">Your role: {user?.role}</p>

      <p className="inline-note">
        <Link className="page-link" href="/dashboard">
          Back to dashboard
        </Link>
      </p>
    </main>
  );
}
