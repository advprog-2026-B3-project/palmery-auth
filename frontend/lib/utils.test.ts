import { describe, it, expect } from "vitest";
import { cn } from "./utils";

describe("utils", () => {
  describe("cn", () => {
    it("should join class names", () => {
      expect(cn("a", "b")).toBe("a b");
    });

    it("should filter out falsy values", () => {
      expect(cn("a", false, "b", null, undefined, "")).toBe("a b");
    });

    it("should return empty string if no valid inputs", () => {
      expect(cn(false, null, undefined)).toBe("");
    });
  });
});
