"use client";

import "../login/login.css";
import Link from "next/link";
import { FormEvent, useState } from "react";
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
  const [form, setForm] = useState<RegisterState>(initialState);
  const [status, setStatus] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setStatus("Mendaftarkan akun...");
    setError(null);

    try {
      if (form.role === "SUPERVISOR" && !form.supervisorCertNumber?.trim()) {
        setStatus(null);
        setError("Nomor Sertifikasi Mandor wajib diisi.");
        return;
      }

      const payload = {
        name: form.name,
        email: form.email,
        password: form.password,
        role: form.role,
      };

      const message = await registerUser(payload);
      setStatus(message);
      setForm(initialState);
    } catch (err) {
      setStatus(null);
      setError(err instanceof Error ? err.message : "Unexpected error");
    }
  }

  return (
    <div className="login-container">
      <div className="login-left">
        <img
          src="/login-illustration.png"
          alt="Palmery illustration"
          className="login-image"
        />
        <h1 className="brand">Palmery</h1>
      </div>

      <div className="login-right">
        <div className="login-card">
          <h2 className="title">Buat Akun</h2>

          <form onSubmit={onSubmit}>
            <label htmlFor="name">Nama</label>
            <input
              id="name"
              type="text"
              placeholder="Nama lengkap"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              required
            />

            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              placeholder="you@example.com"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              required
            />

            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              placeholder="********"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />

            <label htmlFor="role">Peran</label>
            <select
              id="role"
              value={form.role}
              onChange={(e) => setForm({ ...form, role: e.target.value })}
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
                />
              </>
            ) : null}

            <button type="submit">Register</button>
          </form>

          <p className="links">
            Sudah punya akun? <Link href="/login">Login</Link>
          </p>

          {status && <p className="status">{status}</p>}
          {error && <p className="error">{error}</p>}
        </div>
      </div>
    </div>
  );
}
