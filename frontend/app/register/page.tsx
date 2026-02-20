"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { registerUser } from "@/lib/auth-api";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";

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
    setStatus("Registering account...");
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
    <main className="page page-stack">
      <header className="page-header">
        <h1 className="page-title">Register User</h1>
        <p className="page-subtitle">Creates a new user in the auth service.</p>
      </header>

      <Card>
        <CardHeader>
          <CardTitle>Registration Form</CardTitle>
          <CardDescription>Use this to seed users for login and token flow tests.</CardDescription>
        </CardHeader>
        <CardContent>
          <form className="form-grid" onSubmit={onSubmit}>
            <div className="field">
              <Label htmlFor="name">Name</Label>
              <Input
                id="name"
                placeholder="Debug User"
                value={form.name}
                onChange={(event) => setForm({ ...form, name: event.target.value })}
                required
              />
            </div>

            <div className="field">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="debug@example.com"
                value={form.email}
                onChange={(event) => setForm({ ...form, email: event.target.value })}
                required
              />
            </div>

            <div className="field">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                placeholder="********"
                value={form.password}
                onChange={(event) => setForm({ ...form, password: event.target.value })}
                required
              />
            </div>

            <div className="field">
              <Label htmlFor="role">Role</Label>
              <Select
                id="role"
                value={form.role}
                onChange={(event) => setForm({ ...form, role: event.target.value })}
              >
                <option value="user">user</option>
                <option value="admin">admin</option>
                <option value="guest">guest</option>
              </Select>
            </div>

            <div className="action-row">
              <Button type="submit">Register</Button>
              <Button type="button" variant="secondary" onClick={() => setForm(initialState)}>
                Reset
              </Button>
            </div>
          </form>

          {status ? (
            <p>
              <Badge variant="success">status</Badge> {status}
            </p>
          ) : null}

          {error ? (
            <p>
              <Badge variant="danger">error</Badge> {error}
            </p>
          ) : null}
        </CardContent>
      </Card>

      <p className="inline-note">
        <Link className="page-link" href="/">
          Back to home
        </Link>
      </p>
    </main>
  );
}
