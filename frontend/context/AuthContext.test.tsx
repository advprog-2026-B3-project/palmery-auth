import { render, act, screen, cleanup, renderHook } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { AuthProvider, useAuth } from "./AuthContext";
import * as authService from "@/lib/auth-service";
import React from "react";

vi.mock("@/lib/auth-service", () => ({
  getAuthToken: vi.fn(),
  getAuthUser: vi.fn(),
  saveAuthToken: vi.fn(),
  clearAuthToken: vi.fn(),
  isTokenExpired: vi.fn(),
}));

const TestComponent = () => {
  const { token, isAuthenticated, logout, setToken } = useAuth();
  return (
    <div>
      <div data-testid="token">{token}</div>
      <div data-testid="auth">{isAuthenticated ? "YES" : "NO"}</div>
      <button onClick={() => setToken("new-token")}>Login</button>
      <button onClick={logout}>Logout</button>
    </div>
  );
};

describe("AuthContext", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    cleanup();
  });

  it("should initialize with token from service", async () => {
    vi.mocked(authService.getAuthToken).mockReturnValue("initial-token");
    vi.mocked(authService.getAuthUser).mockReturnValue({ email: "t@t.com", isExpired: false });

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(screen.getByTestId("token").textContent).toBe("initial-token");
    expect(screen.getByTestId("auth").textContent).toBe("YES");
  });

  it("should update token when setToken is called", async () => {
    vi.mocked(authService.getAuthToken).mockReturnValue(null);
    vi.mocked(authService.getAuthUser).mockReturnValue({ email: "t@t.com", isExpired: false });

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await act(async () => {
      screen.getByText("Login").click();
    });

    expect(authService.saveAuthToken).toHaveBeenCalledWith("new-token");
  });

  it("should clear token when setToken(null) is called", async () => {
    vi.mocked(authService.getAuthToken).mockReturnValue("initial-token");
    vi.mocked(authService.getAuthUser).mockReturnValue({ email: "t@t.com", isExpired: false });

    const { result } = renderHook(() => useAuth(), { wrapper: AuthProvider });

    await act(async () => {
      result.current.setToken(null);
    });

    expect(authService.clearAuthToken).toHaveBeenCalled();
  });

  it("should clear token when setToken receives an invalid/expired token", async () => {
    vi.mocked(authService.getAuthToken).mockReturnValue(null);
    vi.mocked(authService.getAuthUser).mockReturnValue({ isExpired: true });

    const { result } = renderHook(() => useAuth(), { wrapper: AuthProvider });

    await act(async () => {
      result.current.setToken("invalid-token");
    });

    expect(authService.clearAuthToken).toHaveBeenCalled();
  });

  it("should clear token on init if token is expired", async () => {
    vi.mocked(authService.getAuthToken).mockReturnValue("expired-token");
    vi.mocked(authService.isTokenExpired).mockReturnValue(false); // token exists but user check fails
    vi.mocked(authService.getAuthUser).mockReturnValue({ isExpired: true });

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    expect(authService.clearAuthToken).toHaveBeenCalled();
  });

  it("should clear token when logout is called", async () => {
    vi.mocked(authService.getAuthToken).mockReturnValue("token");
    
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );

    await act(async () => {
      screen.getByText("Logout").click();
    });

    expect(authService.clearAuthToken).toHaveBeenCalled();
  });
});
