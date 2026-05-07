// SearchFilterBar.jsx: Debounced search + status filter + date range filter component.
// Calls GET /api/minutes/search and passes results back up via onResults prop.

import { useState, useEffect, useRef } from "react";
import api from "../services/api";

const STATUS_OPTIONS = [
  { value: "", label: "All Statuses" },
  { value: "DRAFT",     label: "Draft" },
  { value: "PUBLISHED", label: "Published" },
  { value: "ARCHIVED",  label: "Archived" },
];

export default function SearchFilterBar({ onResults, onClear }) {
  const [query,     setQuery]     = useState("");
  const [status,    setStatus]    = useState("");
  const [dateFrom,  setDateFrom]  = useState("");
  const [dateTo,    setDateTo]    = useState("");
  const [searching, setSearching] = useState(false);
  const debounceRef = useRef(null);

  // ── Trigger search whenever any filter changes ────────────────────────────
  useEffect(() => {
    // If all filters are empty, clear results
    if (!query && !status && !dateFrom && !dateTo) {
      onClear?.();
      return;
    }

    clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(async () => {
      setSearching(true);
      try {
        // Primary: keyword search
        if (query) {
          const { data } = await api.get("/minutes/search", {
            params: { q: query, page: 0, size: 100 },
          });
          let results = data.content ?? [];

          // Client-side filter by status if selected
          if (status) {
            results = results.filter((r) => r.status === status);
          }
          // Client-side filter by date range
          if (dateFrom) {
            results = results.filter((r) => r.meetingDate >= dateFrom);
          }
          if (dateTo) {
            results = results.filter((r) => r.meetingDate <= dateTo);
          }
          onResults(results);
        } else {
          // No keyword — just filter the full list by status / date on the list page
          onResults(null, { status, dateFrom, dateTo });
        }
      } catch (err) {
        console.error("Search failed:", err);
      } finally {
        setSearching(false);
      }
    }, 400);

    return () => clearTimeout(debounceRef.current);
  }, [query, status, dateFrom, dateTo]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleClear = () => {
    setQuery("");
    setStatus("");
    setDateFrom("");
    setDateTo("");
    onClear?.();
  };

  const hasFilters = query || status || dateFrom || dateTo;

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-card p-4 mb-4 animate-fade-in">
      <div className="flex flex-col sm:flex-row gap-3">

        {/* Search input */}
        <div className="relative flex-1">
          <span className="absolute inset-y-0 left-3 flex items-center text-gray-400 pointer-events-none">
            🔍
          </span>
          <input
            id="search-input"
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search by title or content…"
            className="
              w-full pl-9 pr-4 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-900
              focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent
              transition-shadow min-h-[44px]
            "
          />
          {searching && (
            <span className="absolute inset-y-0 right-3 flex items-center">
              <svg className="animate-spin h-4 w-4 text-brand-500" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
              </svg>
            </span>
          )}
        </div>

        {/* Status dropdown */}
        <select
          id="filter-status"
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          className="
            px-3 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-700
            focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent
            transition-shadow min-h-[44px] bg-white
          "
        >
          {STATUS_OPTIONS.map((o) => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>

        {/* Date range */}
        <div className="flex items-center gap-2">
          <input
            id="filter-date-from"
            type="date"
            value={dateFrom}
            onChange={(e) => setDateFrom(e.target.value)}
            className="
              px-3 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-700
              focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent
              transition-shadow min-h-[44px]
            "
          />
          <span className="text-gray-400 text-sm">–</span>
          <input
            id="filter-date-to"
            type="date"
            value={dateTo}
            onChange={(e) => setDateTo(e.target.value)}
            className="
              px-3 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-700
              focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent
              transition-shadow min-h-[44px]
            "
          />
        </div>

        {/* Clear button */}
        {hasFilters && (
          <button
            id="btn-clear-filters"
            onClick={handleClear}
            className="
              px-4 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-600
              hover:bg-gray-100 transition-colors whitespace-nowrap min-h-[44px]
            "
          >
            ✕ Clear
          </button>
        )}
      </div>
    </div>
  );
}
