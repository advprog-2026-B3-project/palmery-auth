import { describe, it, expect, beforeEach, vi } from "vitest";
import { getToken, setToken, clearToken, TOKEN_STORAGE_KEY } from "./auth-storage";

describe("auth-storage", () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it("should return null if no token is stored", () => {
    expect(getToken()).toBeNull();
  });

  it("should store and retrieve token", () => {
    const testToken = "test-token-123";
    setToken(testToken);
    expect(getToken()).toBe(testToken);
    expect(localStorage.getItem(TOKEN_STORAGE_KEY)).toBe(testToken);
  });

  it("should clear token", () => {
    localStorage.setItem(TOKEN_STORAGE_KEY, "some-token");
    clearToken();
    expect(getToken()).toBeNull();
    expect(localStorage.getItem(TOKEN_STORAGE_KEY)).toBeNull();
  });

  it("should handle localStorage errors gracefully", () => {
    const spy = vi.spyOn(Storage.prototype, "getItem").mockImplementation(() => {
      throw new Error("Storage disabled");
    });
    
    expect(getToken()).toBeNull();
    spy.mockRestore();
  });
});
