"use client";

import "../login/login.css";
import Link from "next/link";
import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { registerUser } from "@/lib/auth-api";

type RegisterState = {
  name: string;
  email: string;
  password: string;
  role: string;
  supervisorCertNumber?: string;
};

const initialState: RegisterState = {
  name: "",
  email: "",
  password: "",
  role: "WORKER",
  supervisorCertNumber: "",
};

export default function RegisterPage() {
  const router = useRouter();
  const [form, setForm] = useState<RegisterState>(initialState);
  const [error, setError] = useState<string | null>(null);
  const [redirectCountdown, setRedirectCountdown] = useState<number | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (redirectCountdown === null) {
      return;
    }

    if (redirectCountdown <= 0) {
      router.replace("/login?registered=1");
      return;
    }

    const timer = window.setTimeout(() => {
      setRedirectCountdown((current) => (current === null ? null : current - 1));
    }, 1000);

    return () => window.clearTimeout(timer);
  }, [redirectCountdown, router]);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      if (form.role === "SUPERVISOR" && !form.supervisorCertNumber?.trim()) {
        setError("Nomor Sertifikasi Mandor wajib diisi.");
        return;
      }

      const payload = {
        name: form.name,
        email: form.email,
        password: form.password,
        role: form.role,
      };

      await registerUser(payload);
      setRedirectCountdown(3);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unexpected error");
    } finally {
      setIsSubmitting(false);
    }
  }

  const isRedirecting = redirectCountdown !== null;

  return (
    <div className="login-container">
      <div className="login-left">
        <img
          src="/palmery.svg"
          alt="Palmery illustration"
          className="login-image"
        />
        <h1 className="brand">Palmery</h1>
      </div>

      <div className="login-right">
        <div className="login-card">
          <h2 className="title">Buat Akun</h2>

          {isRedirecting ? (
            <div className="auth-banner auth-banner-success">
              Akun berhasil didaftarkan. Mengalihkan ke halaman login dalam{" "}
              <strong>{redirectCountdown}</strong> detik...
            </div>
          ) : null}

          {error ? <div className="auth-banner auth-banner-error">{error}</div> : null}

          <form onSubmit={onSubmit}>
            <label htmlFor="name">Nama</label>
            <input
              id="name"
              type="text"
              placeholder="Nama lengkap"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              required
              disabled={isRedirecting}
            />

            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              placeholder="you@example.com"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              required
              disabled={isRedirecting}
            />

            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              placeholder="********"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
              disabled={isRedirecting}
            />

            <label htmlFor="role">Peran</label>
            <select
              id="role"
              value={form.role}
              onChange={(e) => setForm({ ...form, role: e.target.value })}
              disabled={isRedirecting}
            >
              <option value="WORKER">Buruh</option>
              <option value="SUPERVISOR">Mandor</option>
              <option value="DRIVER">Supir Truk</option>
            </select>

            {form.role === "SUPERVISOR" ? (
              <>
                <label htmlFor="supervisorCertNumber">Nomor Sertifikasi Mandor</label>
                <input
                  id="supervisorCertNumber"
                  type="text"
                  placeholder="Contoh: M-2024-XXXXX"
                  value={form.supervisorCertNumber}
                  onChange={(e) => setForm({ ...form, supervisorCertNumber: e.target.value })}
                  required
                  disabled={isRedirecting}
                />
              </>
            ) : null}

            <button type="submit" disabled={isRedirecting || isSubmitting}>
              {isSubmitting ? "Mendaftarkan..." : "Register"}
            </button>
          </form>

          <p className="links">
            Sudah punya akun? <Link href="/login">Login</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
