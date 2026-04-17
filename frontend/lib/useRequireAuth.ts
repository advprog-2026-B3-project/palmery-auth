"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export function useRequireAuth(redirectTo = "/login") {
  const { token, initialized } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (initialized && !token) {
      router.replace(redirectTo);
    }
  }, [initialized, token, redirectTo, router]);

  return { token, initialized };
}
