import Link from "next/link";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

const debugFlows = [
  { href: "/register", title: "Register", meta: "Create and persist auth account" },
  { href: "/login", title: "Login + Token", meta: "Issue JWT and introspect claims" },
  { href: "/debug", title: "Integration Debug", meta: "Frontend -> backend -> database checks" },
];

export default function Home() {
  return (
    <main className="page page-stack">
      <header className="page-header">
        <h1 className="page-title">Palmery Auth Debug Console</h1>
        <p className="page-subtitle">
          Simple shadcn-style UI for verifying auth and service integration paths.
        </p>
      </header>

      <Card>
        <CardHeader>
          <CardTitle>Flows</CardTitle>
          <CardDescription>Pick a page to test auth features and integration behavior.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="link-grid">
            {debugFlows.map((flow) => (
              <Link key={flow.href} className="nav-tile" href={flow.href}>
                <p className="nav-tile-title">{flow.title}</p>
                <p className="nav-tile-meta">{flow.meta}</p>
              </Link>
            ))}
          </div>
        </CardContent>
      </Card>
    </main>
  );
}
