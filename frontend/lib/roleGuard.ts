"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export function useRequireRole(
  requiredRoles: string | string[],
  redirectTo = "/forbidden"
) {
  const { token, user, initialized, logout } = useAuth();
  const router = useRouter();
  const required = Array.isArray(requiredRoles) ? requiredRoles : [requiredRoles];

  useEffect(() => {
    if (!initialized) {
      return;
    }

    if (!token || !user || user.isExpired) {
      logout();
      router.replace("/login?error=session_required");
      return;
    }

    const normalizedRole = user.role?.toUpperCase();
    const allowed = required.map((role) => role.toUpperCase());

    if (!normalizedRole || !allowed.includes(normalizedRole)) {
      router.replace(redirectTo);
    }
  }, [initialized, token, user, logout, router, redirectTo, requiredRoles]);

  return { token, user, initialized };
}

export function hasRole(userRole: string | undefined, requiredRoles: string | string[]) {
  if (!userRole) {
    return false;
  }
  const normalized = userRole.toUpperCase();
  const allowed = Array.isArray(requiredRoles) ? requiredRoles : [requiredRoles];
  return allowed.map((role) => role.toUpperCase()).includes(normalized);
}
