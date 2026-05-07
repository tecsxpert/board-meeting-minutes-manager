// Dashboard.jsx: KPI dashboard with stats cards and a Recharts BarChart of minutes per month.
// Fetches GET /api/minutes/stats and GET /api/minutes/analytics?period=6m on mount.

import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, Cell,
} from "recharts";
import api from "../services/api";

// ── KPI Card ─────────────────────────────────────────────────────────────────
function KpiCard({ label, value, icon, color, loading }) {
  return (
    <div className={`bg-white rounded-xl border border-gray-200 shadow-card p-5 flex items-center gap-4 hover:shadow-card-hover transition-shadow`}>
      <div className={`w-12 h-12 rounded-xl flex items-center justify-center text-2xl flex-shrink-0 ${color}`}>
        {icon}
      </div>
      <div>
        <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide">{label}</p>
        {loading
          ? <div className="h-7 w-12 bg-gray-200 rounded animate-pulse mt-1" />
          : <p className="text-2xl font-bold text-gray-900 mt-0.5">{value}</p>
        }
      </div>
    </div>
  );
}

// ── Custom Tooltip ────────────────────────────────────────────────────────────
function CustomTooltip({ active, payload, label }) {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-white border border-gray-200 rounded-lg px-3 py-2 shadow-lg text-sm">
      <p className="font-medium text-gray-700">{label}</p>
      <p className="text-brand-500 font-bold">{payload[0].value} minutes</p>
    </div>
  );
}

export default function Dashboard() {
  const [stats,   setStats]   = useState(null);
  const [monthly, setMonthly] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const [statsRes, analyticsRes] = await Promise.allSettled([
          api.get("/minutes/stats"),
          api.get("/minutes/analytics", { params: { period: "6m" } }),
        ]);

        if (statsRes.status === "fulfilled") setStats(statsRes.value.data);

        if (analyticsRes.status === "fulfilled") {
          setMonthly(analyticsRes.value.data ?? []);
        } else {
          // Fallback: derive monthly data from stats if analytics endpoint not ready
          setMonthly([]);
        }
      } catch (err) {
        console.error("Dashboard fetch error:", err);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const kpis = [
    { label: "Total Minutes",  value: stats?.total     ?? 0, icon: "📋", color: "bg-brand-50  text-brand-500" },
    { label: "Published",      value: stats?.published ?? 0, icon: "✅", color: "bg-green-50  text-green-600" },
    { label: "Draft",          value: stats?.draft     ?? 0, icon: "✏️", color: "bg-blue-50   text-blue-600"  },
    { label: "Archived",       value: stats?.archived  ?? 0, icon: "📦", color: "bg-gray-100  text-gray-500"  },
  ];

  return (
    <div className="space-y-6 animate-fade-in">

      {/* Page header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-bold text-gray-900">Dashboard</h2>
          <p className="text-sm text-gray-500 mt-0.5">Overview of board meeting minutes activity</p>
        </div>
        <Link
          to="/minutes/new"
          className="px-4 py-2.5 rounded-lg bg-brand-500 text-white text-sm font-medium hover:bg-brand-600 transition-colors min-h-[44px] flex items-center"
        >
          ＋ New Minutes
        </Link>
      </div>

      {/* KPI Grid — 1col mobile / 2col tablet / 4col desktop */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
        {kpis.map((k) => (
          <KpiCard key={k.label} {...k} loading={loading} />
        ))}
      </div>

      {/* Chart card */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-card p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-base font-semibold text-gray-900">Minutes Per Month (6 months)</h3>
          <Link to="/analytics" className="text-xs text-brand-500 hover:underline">
            Full analytics →
          </Link>
        </div>

        {loading ? (
          <div className="h-56 bg-gray-50 rounded-lg animate-pulse" />
        ) : monthly.length === 0 ? (
          <div className="h-56 flex items-center justify-center text-gray-400 text-sm">
            <div className="text-center">
              <div className="text-3xl mb-2">📊</div>
              <p>No analytics data available yet.</p>
              <p className="text-xs mt-1">Analytics endpoint at <code className="bg-gray-100 px-1 rounded">/api/minutes/analytics</code></p>
            </div>
          </div>
        ) : (
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={monthly} margin={{ top: 5, right: 10, left: -10, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="month" tick={{ fontSize: 12, fill: "#6b7280" }} />
              <YAxis allowDecimals={false} tick={{ fontSize: 12, fill: "#6b7280" }} />
              <Tooltip content={<CustomTooltip />} />
              <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                {monthly.map((_, i) => (
                  <Cell key={i} fill="#1B4F8A" />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        )}
      </div>

      {/* Quick links */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {[
          { to: "/minutes",   icon: "📋", label: "View All Minutes",   desc: "Browse, search, and manage records"      },
          { to: "/analytics", icon: "📈", label: "Analytics",          desc: "Trends and status distribution charts"   },
          { to: "/minutes/new", icon: "＋", label: "New Minutes",      desc: "Create a new board meeting record"       },
        ].map(({ to, icon, label, desc }) => (
          <Link
            key={to}
            to={to}
            className="bg-white rounded-xl border border-gray-200 shadow-card p-5 hover:shadow-card-hover hover:border-brand-200 transition-all group"
          >
            <div className="text-2xl mb-2">{icon}</div>
            <p className="font-semibold text-gray-900 text-sm group-hover:text-brand-600 transition-colors">{label}</p>
            <p className="text-xs text-gray-500 mt-0.5">{desc}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}
