// AnalyticsPage.jsx: Advanced analytics with period selector, LineChart for trends, and PieChart for status.
// Fetches GET /api/minutes/analytics?period=Xm and GET /api/minutes/stats.

import { useState, useEffect } from "react";
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, Legend,
  PieChart, Pie, Cell, Tooltip as PieTooltip, Legend as PieLegend,
} from "recharts";
import api from "../services/api";

// ── Period selector options ───────────────────────────────────────────────────
const PERIODS = [
  { label: "3 Months", value: "3m"  },
  { label: "6 Months", value: "6m"  },
  { label: "1 Year",   value: "12m" },
];

// ── Pie chart colors ──────────────────────────────────────────────────────────
const PIE_COLORS = {
  DRAFT:     "#1B4F8A",   // brand blue
  PUBLISHED: "#16a34a",   // green
  ARCHIVED:  "#9ca3af",   // gray
};

// ── Custom tooltip for line chart ─────────────────────────────────────────────
function LineTooltip({ active, payload, label }) {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-white border border-gray-200 rounded-lg px-3 py-2 shadow-lg text-sm">
      <p className="font-medium text-gray-700">{label}</p>
      <p className="text-brand-500 font-bold">{payload[0].value} minutes</p>
    </div>
  );
}

// ── Custom tooltip for pie chart ──────────────────────────────────────────────
function PieCustomTooltip({ active, payload }) {
  if (!active || !payload?.length) return null;
  const { name, value } = payload[0];
  return (
    <div className="bg-white border border-gray-200 rounded-lg px-3 py-2 shadow-lg text-sm">
      <p className="font-medium text-gray-700">{name}</p>
      <p className="font-bold" style={{ color: PIE_COLORS[name] }}>{value} records</p>
    </div>
  );
}

export default function AnalyticsPage() {
  const [period,   setPeriod]   = useState("6m");
  const [trend,    setTrend]    = useState([]);
  const [pieData,  setPieData]  = useState([]);
  const [loading,  setLoading]  = useState(true);

  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        const [trendRes, statsRes] = await Promise.allSettled([
          api.get("/minutes/analytics", { params: { period } }),
          api.get("/minutes/stats"),
        ]);

        if (trendRes.status === "fulfilled") {
          setTrend(trendRes.value.data ?? []);
        }

        if (statsRes.status === "fulfilled") {
          const s = statsRes.value.data;
          setPieData([
            { name: "DRAFT",     value: s.draft     ?? 0 },
            { name: "PUBLISHED", value: s.published ?? 0 },
            { name: "ARCHIVED",  value: s.archived  ?? 0 },
          ].filter(d => d.value > 0));
        }
      } catch (err) {
        console.error("Analytics fetch error:", err);
      } finally {
        setLoading(false);
      }
    })();
  }, [period]);

  return (
    <div className="space-y-6 animate-fade-in">

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h2 className="text-xl font-bold text-gray-900">Analytics</h2>
          <p className="text-sm text-gray-500 mt-0.5">Trends and status distribution of board minutes</p>
        </div>

        {/* Period selector */}
        <div className="flex items-center gap-1 bg-gray-100 p-1 rounded-lg">
          {PERIODS.map(({ label, value }) => (
            <button
              key={value}
              id={`btn-period-${value}`}
              onClick={() => setPeriod(value)}
              className={`
                px-4 py-1.5 rounded-md text-sm font-medium transition-colors min-h-[36px]
                ${period === value
                  ? "bg-white text-brand-600 shadow-sm"
                  : "text-gray-500 hover:text-gray-700"}
              `}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      {/* Charts grid — stacked on mobile, side by side on large */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">

        {/* Line chart */}
        <div className="bg-white rounded-xl border border-gray-200 shadow-card p-6">
          <h3 className="text-base font-semibold text-gray-900 mb-4">
            Minutes Over Time
            <span className="ml-2 text-xs font-normal text-gray-400">
              ({PERIODS.find(p => p.value === period)?.label})
            </span>
          </h3>
          {loading ? (
            <div className="h-56 bg-gray-50 rounded-lg animate-pulse" />
          ) : trend.length === 0 ? (
            <div className="h-56 flex items-center justify-center text-gray-400 text-sm text-center">
              <div>
                <div className="text-3xl mb-2">📈</div>
                <p>No trend data available.</p>
                <p className="text-xs mt-1">Requires <code className="bg-gray-100 px-1 rounded">/api/minutes/analytics</code></p>
              </div>
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={220}>
              <LineChart data={trend} margin={{ top: 5, right: 10, left: -10, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="month" tick={{ fontSize: 12, fill: "#6b7280" }} />
                <YAxis allowDecimals={false} tick={{ fontSize: 12, fill: "#6b7280" }} />
                <Tooltip content={<LineTooltip />} />
                <Line
                  type="monotone"
                  dataKey="count"
                  stroke="#1B4F8A"
                  strokeWidth={2.5}
                  dot={{ r: 4, fill: "#1B4F8A", strokeWidth: 0 }}
                  activeDot={{ r: 6 }}
                  name="Minutes"
                />
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>

        {/* Pie chart */}
        <div className="bg-white rounded-xl border border-gray-200 shadow-card p-6">
          <h3 className="text-base font-semibold text-gray-900 mb-4">
            Status Distribution
          </h3>
          {loading ? (
            <div className="h-56 bg-gray-50 rounded-lg animate-pulse" />
          ) : pieData.length === 0 ? (
            <div className="h-56 flex items-center justify-center text-gray-400 text-sm">
              <div className="text-center">
                <div className="text-3xl mb-2">🥧</div>
                <p>No status data available.</p>
              </div>
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  innerRadius={40}
                  dataKey="value"
                  nameKey="name"
                  paddingAngle={3}
                  label={({ name, percent }) =>
                    `${name} ${(percent * 100).toFixed(0)}%`
                  }
                  labelLine={false}
                >
                  {pieData.map((entry) => (
                    <Cell key={entry.name} fill={PIE_COLORS[entry.name] ?? "#9ca3af"} />
                  ))}
                </Pie>
                <PieTooltip content={<PieCustomTooltip />} />
                <PieLegend
                  formatter={(value) => (
                    <span className="text-xs text-gray-600">{value}</span>
                  )}
                />
              </PieChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>

      {/* Summary stats */}
      {!loading && pieData.length > 0 && (
        <div className="bg-white rounded-xl border border-gray-200 shadow-card p-5">
          <h3 className="text-sm font-semibold text-gray-700 mb-3">Status Breakdown</h3>
          <div className="flex flex-wrap gap-4">
            {pieData.map(({ name, value }) => (
              <div key={name} className="flex items-center gap-2">
                <span
                  className="w-3 h-3 rounded-full flex-shrink-0"
                  style={{ backgroundColor: PIE_COLORS[name] }}
                />
                <span className="text-sm text-gray-600">
                  <strong>{name}</strong>: {value}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
