"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import {
  AuthUser,
  clearAuthToken,
  getAuthToken,
  getAuthUser,
  isTokenExpired,
  saveAuthToken,
} from "@/lib/auth-service";

type AuthContextValue = {
  token: string | null;
  user: AuthUser | null;
  role: string | null;
  initialized: boolean;
  isAuthenticated: boolean;
  setToken: (token: string | null) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue>({
  token: null,
  user: null,
  role: null,
  initialized: false,
  isAuthenticated: false,
  setToken: () => {},
  logout: () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setTokenState] = useState<string | null>(null);
  const [user, setUserState] = useState<AuthUser | null>(null);
  const [initialized, setInitialized] = useState(false);

  useEffect(() => {
    const storedToken = getAuthToken();
    if (storedToken && !isTokenExpired(storedToken)) {
      const authUser = getAuthUser(storedToken);
      if (authUser && !authUser.isExpired) {
        setTokenState(storedToken);
        setUserState(authUser);
      } else {
        clearAuthToken();
        setTokenState(null);
        setUserState(null);
      }
    }
    setInitialized(true);
  }, []);

  const setToken = useCallback((value: string | null) => {
    if (!value) {
      clearAuthToken();
      setTokenState(null);
      setUserState(null);
      return;
    }

    const authUser = getAuthUser(value);
    if (!authUser || authUser.isExpired) {
      clearAuthToken();
      setTokenState(null);
      setUserState(null);
      return;
    }

    saveAuthToken(value);
    setTokenState(value);
    setUserState(authUser);
  }, []);

  const logout = useCallback(() => {
    clearAuthToken();
    setTokenState(null);
    setUserState(null);
  }, []);

  const value = useMemo(
    () => ({
      token,
      user,
      role: user?.role ?? null,
      initialized,
      isAuthenticated: Boolean(token && user && !user.isExpired),
      setToken,
      logout,
    }),
    [token, user, initialized, setToken, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}

