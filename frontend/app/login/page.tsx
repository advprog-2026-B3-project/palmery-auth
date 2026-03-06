"use client";

import "./login.css";
import Link from "next/link";
import { FormEvent, useState } from "react";
import { loginForToken } from "@/lib/auth-api";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [status, setStatus] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setStatus("Authenticating...");
    setError(null);

    try {
      await loginForToken(email, password);
      setStatus("Login berhasil. Token dibuat.");
    } catch (err) {
      setStatus(null);
      setError(err instanceof Error ? err.message : "Unexpected error");
    }
  }

  return (
    <div className="login-container">
      
      {/* LEFT SIDE */}
      <div className="login-left">
        <img
          src="/login-illustration.png"
          alt="Palmery illustration"
          className="login-image"
        />
        <h1 className="brand">Palmery</h1>
      </div>

      {/* RIGHT SIDE */}
      <div className="login-right">

        <div className="login-card">
          <h2 className="title">Welcome Back!</h2>

          <form onSubmit={onSubmit}>

            <label>Email</label>
            <input
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />

            <label>Password</label>
            <input
              type="password"
              placeholder="********"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            <button type="submit">Log In</button>
          </form>

          <p className="links">
            Don’t have an account?{" "}
            <Link href="/register">Sign Up</Link>
          </p>

          <p className="links">
            Forgot Password?{" "}
            <Link href="#">Click Here</Link>
          </p>

          {status && <p className="status">{status}</p>}
          {error && <p className="error">{error}</p>}
        </div>

      </div>

    </div>
  );
}