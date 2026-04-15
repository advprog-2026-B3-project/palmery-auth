import { describe, it, expect, vi, beforeEach } from "vitest";
import { 
  decodeJwt, 
  normalizeRole, 
  isTokenExpired, 
  getAuthUser, 
  isAuthenticated,
  authFetch
} from "./auth-service";
import * as authStorage from "./auth-storage";

vi.mock("./auth-storage", () => ({
  getToken: vi.fn(),
  setToken: vi.fn(),
  clearToken: vi.fn(),
}));

describe("auth-service", () => {
  const validPayload = {
    email: "test@example.com",
    name: "Test User",
    role: "WORKER",
    exp: Math.floor(Date.now() / 1000) + 3600,
  };

  const createToken = (payload: object) => {
    const header = btoa(JSON.stringify({ alg: "HS256", typ: "JWT" }));
    const body = btoa(JSON.stringify(payload)).replace(/=/g, "");
    return `${header}.${body}.signature`;
  };

  beforeEach(() => {
    vi.clearAllMocks();
    // @ts-ignore
    global.fetch = vi.fn();
  });

  describe("decodeJwt", () => {
    it("should decode a valid JWT", () => {
      const token = createToken(validPayload);
      const decoded = decodeJwt(token);
      expect(decoded).toMatchObject(validPayload);
    });

    it("should return null for invalid tokens", () => {
      expect(decodeJwt("invalid")).toBeNull();
      expect(decodeJwt("a.b")).toBeNull();
    });
  });

  describe("normalizeRole", () => {
    it("should normalize roles correctly", () => {
      expect(normalizeRole("WORKER")).toBe("BURUH");
      expect(normalizeRole("SUPIR")).toBe("SUPIR");
      expect(normalizeRole("SUPERVISOR")).toBe("MANDOR");
      expect(normalizeRole("ADMIN")).toBe("ADMIN");
      expect(normalizeRole("unknown")).toBe("UNKNOWN");
      expect(normalizeRole(undefined)).toBeNull();
    });
  });

  describe("isTokenExpired", () => {
    it("should return false for non-expired token", () => {
      const token = createToken(validPayload);
      expect(isTokenExpired(token)).toBe(false);
    });

    it("should return true for expired token", () => {
      const token = createToken({ ...validPayload, exp: Math.floor(Date.now() / 1000) - 10 });
      expect(isTokenExpired(token)).toBe(true);
    });
  });

  describe("getAuthUser", () => {
    it("should return user info from token", () => {
      const token = createToken(validPayload);
      const user = getAuthUser(token);
      expect(user).toMatchObject({
        email: "test@example.com",
        name: "Test User",
        role: "BURUH",
        isExpired: false,
      });
    });
  });

  describe("isAuthenticated", () => {
    it("should return true if token exists and is valid", () => {
      vi.mocked(authStorage.getToken).mockReturnValue(createToken(validPayload));
      expect(isAuthenticated()).toBe(true);
    });

    it("should return false if no token", () => {
      vi.mocked(authStorage.getToken).mockReturnValue(null);
      expect(isAuthenticated()).toBe(false);
    });
  });

  describe("authFetch", () => {
    it("should add Authorization header if token exists", async () => {
      const token = createToken(validPayload);
      vi.mocked(authStorage.getToken).mockReturnValue(token);
      vi.mocked(fetch).mockResolvedValue({ ok: true, status: 200 } as Response);

      await authFetch("http://api.test");

      expect(fetch).toHaveBeenCalledWith("http://api.test", expect.objectContaining({
        headers: expect.any(Headers),
      }));
      
      const callHeaders = vi.mocked(fetch).mock.calls[0][1]?.headers as Headers;
      expect(callHeaders.get("Authorization")).toBe(`Bearer ${token}`);
    });

    it("should handle 401 response", async () => {
      const token = createToken(validPayload);
      vi.mocked(authStorage.getToken).mockReturnValue(token);
      vi.mocked(fetch).mockResolvedValue({ ok: false, status: 401 } as Response);

      await authFetch("http://api.test");

      expect(authStorage.clearToken).toHaveBeenCalled();
    });
  });
});
