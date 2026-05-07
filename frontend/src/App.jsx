import { BrowserRouter, Routes, Route, Navigate, NavLink } from "react-router-dom";
import { useState } from "react";
import { AuthProvider, useAuth } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import Dashboard from "./pages/Dashboard";
import MinutesList from "./pages/MinutesList";
import MinutesForm from "./pages/MinutesForm";
import MinutesDetail from "./pages/MinutesDetail";
import AnalyticsPage from "./pages/AnalyticsPage";
import ApiDocsPage from "./pages/ApiDocsPage";

const NAV_ITEMS = [
  { to: "/dashboard", label: "Dashboard", icon: "📊" },
  { to: "/minutes", label: "Minutes", icon: "📋" },
  { to: "/analytics", label: "Analytics", icon: "📈" },
  { to: "/api-docs", label: "API Docs", icon: "📄" },
];

function AppLayout({ children }) {
  const { user, logout } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="flex h-screen bg-gray-50 font-sans overflow-hidden">

      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black/40 z-20 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      <aside
        className={`
          fixed top-0 left-0 h-full z-30 flex flex-col
          bg-blue-700 text-white
          transition-all duration-300 ease-in-out
          ${sidebarOpen ? "w-64" : "w-0 lg:w-16 xl:w-64"}
          overflow-hidden
        `}
      >
        {/* Brand header */}
        <div className="flex items-center gap-3 px-4 py-5 border-b border-blue-600 min-h-[64px]">
          <span className="text-2xl flex-shrink-0">🏛️</span>
          <span className="font-bold text-sm leading-tight whitespace-nowrap xl:block hidden">
            Board Minutes<br />
            <span className="font-normal text-blue-200">Manager</span>
          </span>
          <span className="font-bold text-sm leading-tight whitespace-nowrap xl:hidden block">
            Board Minutes<br />
            <span className="font-normal text-blue-200">Manager</span>
          </span>
        </div>

        {/* Nav links */}
        <nav className="flex-1 py-4 space-y-1 px-2">
          {NAV_ITEMS.map(({ to, label, icon }) => (
            <NavLink
              key={to}
              to={to}
              onClick={() => setSidebarOpen(false)}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors min-h-[44px] ` +
                (isActive
                  ? "bg-white/20 text-white"
                  : "text-blue-100 hover:bg-white/10 hover:text-white")
              }
            >
              <span className="text-lg flex-shrink-0">{icon}</span>
              <span className="xl:block hidden whitespace-nowrap">{label}</span>
              <span className="xl:hidden whitespace-nowrap">{label}</span>
            </NavLink>
          ))}
        </nav>

        {/* User + logout */}
        <div className="border-t border-blue-600 px-3 py-4 space-y-2">
          <div className="flex items-center gap-3 px-2 py-1 text-xs text-blue-200">
            <span className="text-lg">👤</span>
            <span className="xl:block hidden truncate">{user?.username ?? user?.sub ?? "User"}</span>
          </div>
          <button
            onClick={logout}
            className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm font-medium text-red-200 hover:bg-red-500/20 transition-colors min-h-[44px]"
          >
            <span className="text-lg flex-shrink-0">🚪</span>
            <span className="xl:block hidden whitespace-nowrap">Sign Out</span>
            <span className="xl:hidden whitespace-nowrap">Sign Out</span>
          </button>
        </div>
      </aside>

      {/* Main content */}
      <div className="flex-1 flex flex-col lg:ml-16 xl:ml-64 min-w-0">
        <header className="flex items-center h-16 px-4 bg-white border-b border-gray-200 shadow-sm sticky top-0 z-10">
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="lg:hidden p-2 rounded-md text-gray-500 hover:text-gray-700 hover:bg-gray-100"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          <h1 className="ml-3 lg:ml-0 text-gray-800 font-semibold text-sm">
            Board Meeting Minutes Manager
          </h1>
        </header>

        <main className="flex-1 overflow-y-auto p-4 md:p-6">
          {children}
        </main>
      </div>
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public */}
          <Route path="/login" element={<LoginPage />} />

          {/* Redirect root */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />

          {/* Protected routes */}
          <Route path="/dashboard" element={
            <ProtectedRoute><AppLayout><Dashboard /></AppLayout></ProtectedRoute>
          } />
          <Route path="/minutes" element={
            <ProtectedRoute><AppLayout><MinutesList /></AppLayout></ProtectedRoute>
          } />
          <Route path="/minutes/new" element={
            <ProtectedRoute><AppLayout><MinutesForm /></AppLayout></ProtectedRoute>
          } />
          <Route path="/minutes/:id" element={
            <ProtectedRoute><AppLayout><MinutesDetail /></AppLayout></ProtectedRoute>
          } />
          <Route path="/minutes/:id/edit" element={
            <ProtectedRoute><AppLayout><MinutesForm /></AppLayout></ProtectedRoute>
          } />
          <Route path="/analytics" element={
            <ProtectedRoute><AppLayout><AnalyticsPage /></AppLayout></ProtectedRoute>
          } />
          <Route path="/api-docs" element={
            <ProtectedRoute><AppLayout><ApiDocsPage /></AppLayout></ProtectedRoute>
          } />

          {/* Catch-all */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;