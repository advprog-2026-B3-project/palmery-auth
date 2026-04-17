"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/useAuth";
import "./navbar.css";

const roleNavMap: Record<string, { href: string; label: string }[]> = {
  ADMIN: [{ href: "/admin", label: "Admin Panel" }],
  MANDOR: [{ href: "/mandor", label: "Mandor Panel" }],
  BURUH: [{ href: "/buruh", label: "Buruh Panel" }],
  SUPIR: [{ href: "/supir", label: "Supir Panel" }],
};

export default function Navbar() {
  const { user, role, initialized, isAuthenticated, logout } = useAuth();
  const router = useRouter();

  if (!initialized || !isAuthenticated) {
    return null;
  }

  function onLogout() {
    logout();
    router.push("/login");
  }

  const navItems = role ? roleNavMap[role] ?? [] : [];

  return (
    <nav className="navbar">
      <div className="navbar-start">
        <Link className="navbar-brand" href="/dashboard">
          MySawit
        </Link>
        <div className="navbar-links">
          <Link href="/dashboard">Dashboard</Link>
          {navItems.map((item) => (
            <Link key={item.href} href={item.href}>
              {item.label}
            </Link>
          ))}
        </div>
      </div>

      <div className="navbar-end">
        <div className="navbar-user">
          <div>
            <p className="navbar-name">{user?.name ?? user?.email ?? "User"}</p>
            <span className={`navbar-badge navbar-badge-${role?.toLowerCase() ?? "default"}`}>
              {role ?? "Unknown"}
            </span>
          </div>
          <button className="navbar-logout" onClick={onLogout}>
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}
