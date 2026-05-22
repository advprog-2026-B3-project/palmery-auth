import { renderHook } from "@testing-library/react";
import { describe, it, expect, vi } from "vitest";
import { useAuth } from "./useAuth";
import { useAuth as useAuthContext } from "@/context/AuthContext";

vi.mock("@/context/AuthContext", () => ({
  useAuth: vi.fn(),
}));

describe("useAuth hook wrapper", () => {
  it("should call useAuthContext", () => {
    const mockValue = { token: "abc" };
    vi.mocked(useAuthContext).mockReturnValue(mockValue as any);
    
    const { result } = renderHook(() => useAuth());
    expect(result.current).toBe(mockValue);
  });
});
