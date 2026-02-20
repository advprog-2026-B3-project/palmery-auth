"use client";

import Link from "next/link";
import { useState } from "react";
import {
  createDebugEvent,
  fetchDebugEvents,
  fetchIntegrationStatus,
} from "@/lib/auth-api";

type ConnectionState = {
  status: string;
  latencyMs?: number;
};

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
      setMessage("Event written. Load debug events to verify database read/write.");
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
    <main>
      <h1>Integration Debug</h1>
      <p>Validate frontend -&gt; backend and backend -&gt; database connection.</p>

      <button type="button" onClick={loadStatus}>Check Connection Status</button>
      <button type="button" onClick={addEvent}>Write Debug Event</button>
      <button type="button" onClick={loadEvents}>Read Debug Events</button>

      {message ? <p>{message}</p> : null}
      {error ? <p>{error}</p> : null}

      <h2>Frontend to Backend</h2>
      <pre>{JSON.stringify(frontendToBackend, null, 2)}</pre>

      <h2>Backend to Database</h2>
      <pre>{JSON.stringify(backendToDatabase, null, 2)}</pre>

      {status ? (
        <>
          <h2>Raw Backend Payload</h2>
          <pre>{JSON.stringify(status, null, 2)}</pre>
        </>
      ) : null}

      {events.length > 0 ? (
        <>
          <h2>Latest Debug Events</h2>
          <pre>{JSON.stringify(events, null, 2)}</pre>
        </>
      ) : null}

      <p>
        <Link href="/">Back to home</Link>
      </p>
    </main>
  );
}
