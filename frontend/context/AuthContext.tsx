"use client";

import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { getToken as readToken, setToken as writeToken, clearToken as dropToken } from "@/lib/auth-storage";

type AuthContextValue = {
  token: string | null;
  setToken: (token: string | null) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue>({
  token: null,
  setToken: () => {},
  logout: () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setTokenState] = useState<string | null>(null);

  useEffect(() => {
    setTokenState(readToken());
  }, []);

  const setToken = (value: string | null) => {
    if (value) {
      writeToken(value);
      setTokenState(value);
    } else {
      dropToken();
      setTokenState(null);
    }
  };

  const logout = () => {
    dropToken();
    setTokenState(null);
  };

  const value = useMemo(
    () => ({
      token,
      setToken,
      logout,
    }),
    [token]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}

