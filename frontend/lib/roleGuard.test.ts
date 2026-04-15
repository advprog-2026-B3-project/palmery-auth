import { describe, it, expect } from "vitest";
import { hasRole } from "./roleGuard";

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
});
