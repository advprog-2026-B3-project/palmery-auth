"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { registerUser } from "@/lib/auth-api";

type RegisterState = {
  name: string;
  email: string;
  password: string;
  role: string;
};

const initialState: RegisterState = {
  name: "",
  email: "",
  password: "",
  role: "user",
};

export default function RegisterPage() {
  const [form, setForm] = useState<RegisterState>(initialState);
  const [status, setStatus] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setStatus("Processing...");
    setError(null);

    try {
      const message = await registerUser(form);
      setStatus(message);
      setForm(initialState);
    } catch (err) {
      setStatus(null);
      setError(err instanceof Error ? err.message : "Unexpected error");
    }
  }

  return (
    <main>
      <h1>Create Account</h1>
      <p className="muted">Registers user in the auth service.</p>
      <div className="card">
        <form onSubmit={onSubmit}>
          <input
            placeholder="Name"
            value={form.name}
            onChange={(event) => setForm({ ...form, name: event.target.value })}
            required
          />
          <input
            type="email"
            placeholder="Email"
            value={form.email}
            onChange={(event) => setForm({ ...form, email: event.target.value })}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={form.password}
            onChange={(event) => setForm({ ...form, password: event.target.value })}
            required
          />
          <select
            value={form.role}
            onChange={(event) => setForm({ ...form, role: event.target.value })}
          >
            <option value="user">user</option>
            <option value="admin">admin</option>
            <option value="guest">guest</option>
          </select>
          <button type="submit">Register</button>
        </form>
        {status && <p className="status-ok">{status}</p>}
        {error && <p className="status-err">{error}</p>}
      </div>
      <p>
        <Link href="/">Back to home</Link>
      </p>
    </main>
  );
}
