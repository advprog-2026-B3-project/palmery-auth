"use client";

import Link from "next/link";
import { useState } from "react";
import { createDebugEvent, fetchDebugEvents, fetchIntegrationStatus } from "@/lib/auth-api";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

type ConnectionState = {
  status: string;
  latencyMs?: number;
};

function valueToText(value: unknown): string {
  if (value === null || value === undefined) {
    return "-";
  }
  return String(value);
}

export default function DebugPage() {
  const [status, setStatus] = useState<Record<string, unknown> | null>(null);
  const [events, setEvents] = useState<Record<string, unknown>[]>([]);
  const [frontendToBackend, setFrontendToBackend] = useState<ConnectionState>({ status: "unknown" });
  const [backendToDatabase, setBackendToDatabase] = useState<ConnectionState>({ status: "unknown" });
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function loadStatus() {
    setError(null);
    setMessage("Checking connection status...");

    const startedAt = performance.now();
    try {
      const integrationStatus = await fetchIntegrationStatus();
      const latencyMs = Math.round(performance.now() - startedAt);

      setStatus(integrationStatus);
      setFrontendToBackend({ status: "connected", latencyMs });

      const dbObject = integrationStatus.database as Record<string, unknown> | undefined;
      const dbStatus = typeof dbObject?.status === "string" ? dbObject.status : "unknown";
      const dbLatency = typeof dbObject?.latency_ms === "number" ? dbObject.latency_ms : undefined;
      setBackendToDatabase({ status: dbStatus, latencyMs: dbLatency });

      setMessage("Connection stats loaded.");
    } catch (err) {
      setStatus(null);
      setFrontendToBackend({ status: "disconnected" });
      setBackendToDatabase({ status: "unknown" });
      setMessage(null);
      setError(err instanceof Error ? err.message : "Unexpected error");
    }
  }

  async function addEvent() {
    setError(null);
    setMessage("Writing debug event to database...");

    try {
      await createDebugEvent();
      setMessage("Event written. Load debug events to verify read/write path.");
    } catch (err) {
      setMessage(null);
      setError(err instanceof Error ? err.message : "Unexpected error");
    }
  }

  async function loadEvents() {
    setError(null);
    setMessage("Reading debug events from database...");

    try {
      const latestEvents = await fetchDebugEvents();
      setEvents(latestEvents);
      setMessage("Debug events loaded.");
    } catch (err) {
      setMessage(null);
      setError(err instanceof Error ? err.message : "Unexpected error");
    }
  }

  return (
    <main className="page page-stack">
      <header className="page-header">
        <h1 className="page-title">Integration Debug</h1>
        <p className="page-subtitle">
          Checks frontend to backend and backend to database connectivity with simple diagnostics.
        </p>
      </header>

      <Card>
        <CardHeader>
          <CardTitle>Actions</CardTitle>
          <CardDescription>Run live checks and event write/read verification.</CardDescription>
        </CardHeader>
        <CardContent className="page-stack">
          <div className="action-row">
            <Button type="button" onClick={loadStatus}>
              Check Connection Status
            </Button>
            <Button type="button" variant="secondary" onClick={addEvent}>
              Write Debug Event
            </Button>
            <Button type="button" variant="outline" onClick={loadEvents}>
              Read Debug Events
            </Button>
          </div>

          {message ? (
            <p>
              <Badge variant="success">status</Badge> {message}
            </p>
          ) : null}

          {error ? (
            <p>
              <Badge variant="danger">error</Badge> {error}
            </p>
          ) : null}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Connection Stats</CardTitle>
          <CardDescription>Monotone quick view for both integration legs.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="stats-grid">
            <div className="stat-card">
              <p className="stat-label">Frontend to Backend</p>
              <p className="stat-value">{valueToText(frontendToBackend.status)}</p>
              <p className="inline-note">Latency: {valueToText(frontendToBackend.latencyMs)} ms</p>
            </div>
            <div className="stat-card">
              <p className="stat-label">Backend to Database</p>
              <p className="stat-value">{valueToText(backendToDatabase.status)}</p>
              <p className="inline-note">Latency: {valueToText(backendToDatabase.latencyMs)} ms</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {status ? (
        <Card>
          <CardHeader>
            <CardTitle>Raw Backend Payload</CardTitle>
          </CardHeader>
          <CardContent>
            <pre className="pre-block">{JSON.stringify(status, null, 2)}</pre>
          </CardContent>
        </Card>
      ) : null}

      {events.length > 0 ? (
        <Card>
          <CardHeader>
            <CardTitle>Latest Debug Events</CardTitle>
          </CardHeader>
          <CardContent>
            <pre className="pre-block">{JSON.stringify(events, null, 2)}</pre>
          </CardContent>
        </Card>
      ) : null}

      <p className="inline-note">
        <Link className="page-link" href="/">
          Back to home
        </Link>
      </p>
    </main>
  );
}
