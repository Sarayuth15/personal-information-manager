import { Inbox } from "lucide-react";

/**
 * Phase 1 placeholder. Replaced in Phase 7 by the dashboard built from
 * docs/ui-reference/dashboard.jpg.
 */
export default function HomePage() {
  return (
    <main className="flex flex-1 flex-col items-center justify-center gap-3 p-6 text-center">
      <Inbox aria-hidden="true" className="size-8 text-muted-foreground" />
      <h1 className="text-2xl font-semibold">Personal Information Manager</h1>
      <p className="text-sm text-muted-foreground">
        Frontend initialized. The dashboard UI arrives in Phase 7.
      </p>
    </main>
  );
}
