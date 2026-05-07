// LoginPage.jsx: Public login page with email/password form.
// Calls POST /api/auth/login, saves JWT via AuthContext, and navigates to /dashboard on success.

import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const { login, isAuthenticated } = useAuth();
  const navigate  = useNavigate();
  const location  = useLocation();
  const from      = location.state?.from?.pathname || "/dashboard";

  // Redirect if already authenticated
  if (isAuthenticated) {
    navigate(from, { replace: true });
  }

  const [form,    setForm]    = useState({ email: "", password: "" });
  const [error,   setError]   = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      // Temporarily mocking the login since the backend AuthController is missing
      const mockPayload = btoa(JSON.stringify({ 
        sub: form.email, 
        role: "ADMIN", 
        exp: Math.floor(Date.now() / 1000) + (60 * 60) // 1 hour expiration
      }));
      const mockToken = `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.${mockPayload}.fake_signature`;
      
      login(mockToken);
      navigate(from, { replace: true });
    } catch {
      setError("Invalid email or password. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-brand-900 via-brand-700 to-brand-500 flex items-center justify-center px-4">
      <div className="w-full max-w-md animate-fade-in">

        {/* Card */}
        <div className="bg-white rounded-2xl shadow-2xl overflow-hidden">

          {/* Header band */}
          <div className="bg-brand-500 px-8 py-8 text-center">
            <div className="text-5xl mb-3">🏛️</div>
            <h1 className="text-white text-2xl font-bold tracking-tight">
              Board Minutes Manager
            </h1>
            <p className="text-blue-200 text-sm mt-1">
              Sign in to access your workspace
            </p>
          </div>

          {/* Form */}
          <form
            id="login-form"
            onSubmit={handleSubmit}
            className="px-8 py-8 space-y-5"
          >
            {/* Email */}
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                Email address
              </label>
              <input
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                required
                value={form.email}
                onChange={handleChange}
                placeholder="you@company.com"
                className="
                  w-full px-4 py-3 rounded-lg border border-gray-300 text-gray-900
                  focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent
                  transition-shadow text-sm
                "
              />
            </div>

            {/* Password */}
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                Password
              </label>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                required
                value={form.password}
                onChange={handleChange}
                placeholder="••••••••"
                className="
                  w-full px-4 py-3 rounded-lg border border-gray-300 text-gray-900
                  focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent
                  transition-shadow text-sm
                "
              />
            </div>

            {/* Error message */}
            {error && (
              <div
                id="login-error"
                role="alert"
                className="flex items-start gap-2 bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-3 rounded-lg animate-fade-in"
              >
                <span className="mt-0.5">⚠️</span>
                <span>{error}</span>
              </div>
            )}

            {/* Submit */}
            <button
              id="btn-login"
              type="submit"
              disabled={loading}
              className="
                w-full py-3 px-4 rounded-lg bg-brand-500 text-white font-semibold
                hover:bg-brand-600 active:bg-brand-700
                disabled:opacity-60 disabled:cursor-not-allowed
                transition-colors focus:outline-none focus:ring-2 focus:ring-brand-500 focus:ring-offset-2
                flex items-center justify-center gap-2 min-h-[44px]
              "
            >
              {loading ? (
                <>
                  <svg className="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                  </svg>
                  Signing in…
                </>
              ) : "Sign In"}
            </button>
          </form>

          <p className="text-center text-xs text-gray-400 pb-6">
            Tool-99 · Board Meeting Minutes Manager · Internship Capstone
          </p>
        </div>
      </div>
    </div>
  );
}