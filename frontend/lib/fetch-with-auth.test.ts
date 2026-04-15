import { describe, it, expect, vi, beforeEach } from "vitest";
import { fetchWithAuth } from "./fetch-with-auth";
import * as authStorage from "./auth-storage";
import * as authService from "./auth-service";

vi.mock("./auth-storage", () => ({
  getToken: vi.fn(),
  clearToken: vi.fn(),
}));

vi.mock("./auth-service", () => ({
  decodeJwt: vi.fn(),
}));

describe("fetch-with-auth", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // @ts-ignore
    global.fetch = vi.fn();
  });

  it("should add Authorization header if token is valid", async () => {
    vi.mocked(authStorage.getToken).mockReturnValue("valid-token");
    vi.mocked(authService.decodeJwt).mockReturnValue({ exp: Math.floor(Date.now() / 1000) + 100 });
    vi.mocked(fetch).mockResolvedValue({ ok: true, status: 200 } as Response);

    await fetchWithAuth("http://test.com");

    const callHeaders = vi.mocked(fetch).mock.calls[0][1]?.headers as Headers;
    expect(callHeaders.get("Authorization")).toBe("Bearer valid-token");
  });

  it("should throw error and clear token if expired", async () => {
    vi.mocked(authStorage.getToken).mockReturnValue("expired-token");
    vi.mocked(authService.decodeJwt).mockReturnValue({ exp: Math.floor(Date.now() / 1000) - 100 });

    await expect(fetchWithAuth("http://test.com")).rejects.toThrow("Token expired");
    expect(authStorage.clearToken).toHaveBeenCalled();
  });
});
