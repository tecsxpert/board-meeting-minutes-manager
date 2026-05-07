// MinutesList.jsx: Paginated table of board minutes with search/filter bar, status badges, and CRUD actions.
// Fetches GET /api/minutes on mount and on page change; delete calls DELETE /api/minutes/{id}.

import { useState, useEffect, useCallback } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../services/api";
import SearchFilterBar from "../components/SearchFilterBar";

// ── Status badge ─────────────────────────────────────────────────────────────
const STATUS_BADGE = {
  DRAFT:     "bg-blue-100  text-blue-700  border-blue-200",
  PUBLISHED: "bg-green-100 text-green-700 border-green-200",
  ARCHIVED:  "bg-gray-100  text-gray-600  border-gray-200",
};

function StatusBadge({ status }) {
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${STATUS_BADGE[status] ?? "bg-gray-100 text-gray-500"}`}>
      {status}
    </span>
  );
}

// ── Spinner ───────────────────────────────────────────────────────────────────
function Spinner() {
  return (
    <div className="flex justify-center items-center py-16">
      <svg className="animate-spin h-8 w-8 text-brand-500" fill="none" viewBox="0 0 24 24">
        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
      </svg>
    </div>
  );
}

export default function MinutesList() {
  const navigate = useNavigate();

  const [minutes,       setMinutes]       = useState([]);
  const [totalPages,    setTotalPages]    = useState(0);
  const [currentPage,   setCurrentPage]   = useState(0);
  const [loading,       setLoading]       = useState(true);
  const [searchResults, setSearchResults] = useState(null); // null = show paginated list
  const [deletingId,    setDeletingId]    = useState(null);

  const PAGE_SIZE = 10;

  // ── Fetch paginated list ─────────────────────────────────────────────────
  const fetchMinutes = useCallback(async (page = 0) => {
    setLoading(true);
    try {
      const { data } = await api.get("/minutes", {
        params: { page, size: PAGE_SIZE },
      });
      setMinutes(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
      setCurrentPage(data.number ?? 0);
    } catch (err) {
      console.error("Failed to fetch minutes:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchMinutes(0);
  }, [fetchMinutes]);

  // ── Delete ────────────────────────────────────────────────────────────────
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this record?")) return;
    setDeletingId(id);
    try {
      await api.delete(`/minutes/${id}`);
      // Refresh current page (or go to previous if last item on page)
      const refreshPage = minutes.length === 1 && currentPage > 0
        ? currentPage - 1
        : currentPage;
      setSearchResults(null);
      fetchMinutes(refreshPage);
    } catch (err) {
      console.error("Delete failed:", err);
      alert("Failed to delete. Please try again.");
    } finally {
      setDeletingId(null);
    }
  };

  // ── Determine displayed rows ──────────────────────────────────────────────
  const displayedRows = searchResults !== null ? searchResults : minutes;

  return (
    <div className="space-y-4 animate-fade-in">

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h2 className="text-xl font-bold text-gray-900">Board Minutes</h2>
          <p className="text-sm text-gray-500 mt-0.5">Manage all board meeting records</p>
        </div>
        <Link
          id="btn-new-minute"
          to="/minutes/new"
          className="
            inline-flex items-center gap-2 px-4 py-2.5 rounded-lg
            bg-brand-500 text-white text-sm font-medium
            hover:bg-brand-600 transition-colors min-h-[44px]
          "
        >
          ＋ New Minutes
        </Link>
      </div>

      {/* Search & filter bar */}
      <SearchFilterBar
        onResults={(results) => setSearchResults(results)}
        onClear={() => setSearchResults(null)}
      />

      {/* Table card */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-card overflow-hidden">
        {loading ? (
          <Spinner />
        ) : displayedRows.length === 0 ? (
          <div className="text-center py-16 text-gray-400">
            <div className="text-4xl mb-3">📭</div>
            <p className="font-medium">No records found</p>
            <p className="text-sm mt-1">Try adjusting your search or create a new record.</p>
          </div>
        ) : (
          <>
            {/* Table — scrollable on mobile */}
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    {["Title", "Meeting Date", "Status", "Actions"].map((h) => (
                      <th
                        key={h}
                        className="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                      >
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-100">
                  {displayedRows.map((row) => (
                    <tr
                      key={row.id}
                      className="hover:bg-gray-50 transition-colors"
                    >
                      <td className="px-4 py-3.5 text-sm font-medium text-gray-900 max-w-xs">
                        <span className="line-clamp-1">{row.title}</span>
                      </td>
                      <td className="px-4 py-3.5 text-sm text-gray-600 whitespace-nowrap">
                        {row.meetingDate}
                      </td>
                      <td className="px-4 py-3.5">
                        <StatusBadge status={row.status} />
                      </td>
                      <td className="px-4 py-3.5">
                        <div className="flex items-center gap-2">
                          <button
                            id={`btn-view-${row.id}`}
                            onClick={() => navigate(`/minutes/${row.id}`)}
                            className="px-2.5 py-1.5 text-xs font-medium text-brand-600 bg-brand-50 hover:bg-brand-100 rounded-md transition-colors min-h-[32px]"
                          >
                            View
                          </button>
                          <button
                            id={`btn-edit-${row.id}`}
                            onClick={() => navigate(`/minutes/${row.id}/edit`)}
                            className="px-2.5 py-1.5 text-xs font-medium text-amber-600 bg-amber-50 hover:bg-amber-100 rounded-md transition-colors min-h-[32px]"
                          >
                            Edit
                          </button>
                          <button
                            id={`btn-delete-${row.id}`}
                            onClick={() => handleDelete(row.id)}
                            disabled={deletingId === row.id}
                            className="px-2.5 py-1.5 text-xs font-medium text-red-600 bg-red-50 hover:bg-red-100 rounded-md transition-colors disabled:opacity-50 min-h-[32px]"
                          >
                            {deletingId === row.id ? "…" : "Delete"}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination — only for non-search mode */}
            {searchResults === null && totalPages > 1 && (
              <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100 bg-gray-50">
                <button
                  id="btn-prev-page"
                  onClick={() => fetchMinutes(currentPage - 1)}
                  disabled={currentPage === 0}
                  className="px-3 py-1.5 text-sm text-gray-600 rounded-md border border-gray-300 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed transition-colors min-h-[36px]"
                >
                  ← Previous
                </button>
                <span className="text-sm text-gray-500">
                  Page {currentPage + 1} of {totalPages}
                </span>
                <button
                  id="btn-next-page"
                  onClick={() => fetchMinutes(currentPage + 1)}
                  disabled={currentPage + 1 >= totalPages}
                  className="px-3 py-1.5 text-sm text-gray-600 rounded-md border border-gray-300 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed transition-colors min-h-[36px]"
                >
                  Next →
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
