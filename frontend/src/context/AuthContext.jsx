// AuthContext.jsx: React context providing authentication state, login/logout actions, and JWT decoding.
// Wraps the app with AuthProvider; exposes useAuth() hook for consuming components.

import { createContext, useContext, useState, useCallback } from "react";

const AuthContext = createContext(null);

/**
 * Safely decodes a JWT payload without verifying the signature.
 * Returns null if the token is malformed.
 */
function decodeJwt(token) {
  try {
    const base64Url = token.split(".")[1];
    const base64    = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => {
    const savedToken = localStorage.getItem("token");
    if (savedToken) {
      const decoded = decodeJwt(savedToken);
      if (decoded && decoded.exp * 1000 > Date.now()) {
        return savedToken;
      }
      localStorage.removeItem("token");
    }
    return null;
  });

  const [user, setUser] = useState(() => {
    const savedToken = localStorage.getItem("token");
    if (savedToken) {
      const decoded = decodeJwt(savedToken);
      if (decoded && decoded.exp * 1000 > Date.now()) {
        return decoded;
      }
    }
    return null;
  });

  /**
   * Called on successful login.
   * Saves token to localStorage, decodes JWT payload as user state.
   */
  const login = useCallback((jwtToken) => {
    localStorage.setItem("token", jwtToken);
    const decoded = decodeJwt(jwtToken);
    setToken(jwtToken);
    setUser(decoded);
  }, []);

  /**
   * Clears session and redirects to /login.
   */
  const logout = useCallback(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setToken(null);
    setUser(null);
    window.location.href = "/login";
  }, []);

  const isAuthenticated = Boolean(token);

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Hook to access authentication context from any component.
 */
// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within an <AuthProvider>");
  }
  return ctx;
}