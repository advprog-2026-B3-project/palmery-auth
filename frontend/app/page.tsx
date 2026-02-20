import Link from "next/link";

export default function Home() {
  return (
    <main>
      <h1>Palmery Auth</h1>
      <p className="muted">
        Next.js 16 frontend with Spring Security backed auth APIs.
      </p>
      <div className="card">
        <h2>Available Flows</h2>
        <nav>
          <Link href="/register">Register</Link>
          <Link href="/login">Login + Token</Link>
          <Link href="/debug">Debug Integration</Link>
        </nav>
      </div>
    </main>
  );
}
