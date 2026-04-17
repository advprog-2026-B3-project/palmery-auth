import Link from "next/link";

export default function ForbiddenPage() {
  return (
    <main className="page page-stack forbidden-page">
      <header className="page-header">
        <h1 className="page-title">403 Forbidden</h1>
        <p className="page-subtitle">
          You do not have access to this page.
        </p>
      </header>

      <section className="page-card forbidden-card">
        <p>
          Your account currently does not have the required role or permission to view this content. Please return to your dashboard or ask your administrator for access.
        </p>

        <Link className="ui-button ui-button-default ui-button-size-default" href="/dashboard">
          Go back to dashboard
        </Link>
      </section>
    </main>
  );
}
