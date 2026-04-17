import { renderHook } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { useRequireAuth } from "./useRequireAuth";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";

vi.mock("@/context/AuthContext", () => ({
  useAuth: vi.fn(),
}));

vi.mock("next/navigation", () => ({
  useRouter: vi.fn(),
}));

describe("useRequireAuth", () => {
  const replace = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(useRouter).mockReturnValue({ replace } as any);
  });

  it("should redirect if not authenticated and initialized", () => {
    vi.mocked(useAuth).mockReturnValue({ token: null, initialized: true } as any);
    
    renderHook(() => useRequireAuth());

    expect(replace).toHaveBeenCalledWith("/login");
  });

  it("should not redirect if authenticated", () => {
    vi.mocked(useAuth).mockReturnValue({ token: "token", initialized: true } as any);
    
    renderHook(() => useRequireAuth());

    expect(replace).not.toHaveBeenCalled();
  });

  it("should not redirect if not initialized", () => {
    vi.mocked(useAuth).mockReturnValue({ token: null, initialized: false } as any);
    
    renderHook(() => useRequireAuth());

    expect(replace).not.toHaveBeenCalled();
  });
});
