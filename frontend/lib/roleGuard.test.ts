import { renderHook } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { hasRole, useRequireRole } from "./roleGuard";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";

vi.mock("@/context/AuthContext", () => ({
  useAuth: vi.fn(),
}));

vi.mock("next/navigation", () => ({
  useRouter: vi.fn(),
}));

describe("roleGuard", () => {
  describe("hasRole", () => {
    it("should return true if role matches", () => {
      expect(hasRole("ADMIN", "ADMIN")).toBe(true);
      expect(hasRole("ADMIN", ["ADMIN", "USER"])).toBe(true);
    });

    it("should return false if role does not match", () => {
      expect(hasRole("USER", "ADMIN")).toBe(false);
      expect(hasRole(undefined, "ADMIN")).toBe(false);
    });

    it("should be case-insensitive", () => {
      expect(hasRole("admin", "ADMIN")).toBe(true);
      expect(hasRole("ADMIN", "admin")).toBe(true);
    });
  });

  describe("useRequireRole", () => {
    const replace = vi.fn();
    const logout = vi.fn();

    beforeEach(() => {
      vi.clearAllMocks();
      vi.mocked(useRouter).mockReturnValue({ replace } as any);
    });

    it("should do nothing if not initialized", () => {
      vi.mocked(useAuth).mockReturnValue({ initialized: false } as any);
      renderHook(() => useRequireRole("ADMIN"));
      expect(replace).not.toHaveBeenCalled();
    });

    it("should redirect to login and logout if no token", () => {
      vi.mocked(useAuth).mockReturnValue({ 
        initialized: true, 
        token: null, 
        user: null, 
        logout 
      } as any);
      
      renderHook(() => useRequireRole("ADMIN"));
      
      expect(logout).toHaveBeenCalled();
      expect(replace).toHaveBeenCalledWith("/login?error=session_required");
    });

    it("should redirect to login and logout if token expired", () => {
      vi.mocked(useAuth).mockReturnValue({ 
        initialized: true, 
        token: "expired", 
        user: { isExpired: true }, 
        logout 
      } as any);
      
      renderHook(() => useRequireRole("ADMIN"));
      
      expect(logout).toHaveBeenCalled();
      expect(replace).toHaveBeenCalledWith("/login?error=session_required");
    });

    it("should redirect to forbidden if role mismatch", () => {
      vi.mocked(useAuth).mockReturnValue({ 
        initialized: true, 
        token: "valid", 
        user: { role: "USER", isExpired: false }, 
        logout 
      } as any);
      
      renderHook(() => useRequireRole("ADMIN"));
      
      expect(replace).toHaveBeenCalledWith("/forbidden");
    });

    it("should not redirect if role matches", () => {
      vi.mocked(useAuth).mockReturnValue({ 
        initialized: true, 
        token: "valid", 
        user: { role: "ADMIN", isExpired: false }, 
        logout 
      } as any);
      
      renderHook(() => useRequireRole("ADMIN"));
      
      expect(replace).not.toHaveBeenCalled();
    });

    it("should work with array of roles", () => {
      vi.mocked(useAuth).mockReturnValue({ 
        initialized: true, 
        token: "valid", 
        user: { role: "USER", isExpired: false }, 
        logout 
      } as any);
      
      renderHook(() => useRequireRole(["ADMIN", "SUPERVISOR"]));
      expect(replace).toHaveBeenCalledWith("/forbidden");

      vi.mocked(useAuth).mockReturnValue({ 
        initialized: true, 
        token: "valid", 
        user: { role: "SUPERVISOR", isExpired: false }, 
        logout 
      } as any);
      renderHook(() => useRequireRole(["ADMIN", "SUPERVISOR"]));
      expect(replace).toHaveBeenCalledTimes(1); // Only from first call
    });
  });
});
