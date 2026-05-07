// MinutesDetail.jsx: Detail view for a single board minutes record.
// Displays all fields, AI analysis panel, and Edit/Delete action buttons.

import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";
import AiResponsePanel from "../components/AiResponsePanel";

const STATUS_BADGE = {
  DRAFT:     "bg-blue-100  text-blue-700  border-blue-200",
  PUBLISHED: "bg-green-100 text-green-700 border-green-200",
  ARCHIVED:  "bg-gray-100  text-gray-600  border-gray-200",
};

function Field({ label, value, multiline = false }) {
  if (!value) return null;
  return (
    <div>
      <dt className="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-1">{label}</dt>
      <dd className={`text-sm text-gray-800 ${multiline ? "whitespace-pre-wrap leading-relaxed" : ""}`}>
        {value}
      </dd>
    </div>
  );
}

export default function MinutesDetail() {
  const { id }   = useParams();
  const navigate = useNavigate();

  const [data,     setData]     = useState(null);
  const [loading,  setLoading]  = useState(true);
  const [error,    setError]    = useState("");
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const { data: res } = await api.get(`/minutes/${id}`);
        setData(res);
      } catch {
        setError("Record not found or you do not have permission to view it.");
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  const handleDelete = async () => {
    if (!window.confirm("Permanently delete this record? This action cannot be undone.")) return;
    setDeleting(true);
    try {
      await api.delete(`/minutes/${id}`);
      navigate("/minutes");
    } catch {
      alert("Delete failed. Please try again.");
      setDeleting(false);
    }
  };

  // ── Loading skeleton ──────────────────────────────────────────────────────
  if (loading) {
    return (
      <div className="max-w-4xl mx-auto space-y-4 animate-pulse">
        <div className="h-8 w-64 bg-gray-200 rounded" />
        <div className="bg-white rounded-xl border p-6 space-y-4">
          {[1,2,3,4,5].map(i => <div key={i} className="h-5 bg-gray-100 rounded w-3/4" />)}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-4xl mx-auto">
        <div className="bg-red-50 border border-red-200 rounded-xl p-8 text-center text-red-700">
          <div className="text-4xl mb-3">🔍</div>
          <p className="font-medium">{error}</p>
          <button onClick={() => navigate("/minutes")} className="mt-4 text-sm text-brand-600 underline">
            Back to list
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-5 animate-fade-in">

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
        <div className="flex items-start gap-3">
          <button
            onClick={() => navigate("/minutes")}
            className="mt-1 p-2 rounded-lg text-gray-500 hover:bg-gray-100 transition-colors"
          >
            ←
          </button>
          <div>
            <div className="flex items-center gap-2 flex-wrap">
              <h2 className="text-xl font-bold text-gray-900">{data.title}</h2>
              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${STATUS_BADGE[data.status] ?? "bg-gray-100 text-gray-500"}`}>
                {data.status}
              </span>
            </div>
            <p className="text-sm text-gray-500 mt-0.5">Meeting Date: {data.meetingDate}</p>
          </div>
        </div>

        {/* Actions */}
        <div className="flex items-center gap-2 sm:flex-shrink-0">
          <button
            id="btn-edit"
            onClick={() => navigate(`/minutes/${id}/edit`)}
            className="px-4 py-2.5 rounded-lg bg-amber-500 text-white text-sm font-medium hover:bg-amber-600 transition-colors min-h-[44px]"
          >
            ✏️ Edit
          </button>
          <button
            id="btn-delete"
            onClick={handleDelete}
            disabled={deleting}
            className="px-4 py-2.5 rounded-lg bg-red-500 text-white text-sm font-medium hover:bg-red-600 disabled:opacity-60 transition-colors min-h-[44px]"
          >
            {deleting ? "Deleting…" : "🗑 Delete"}
          </button>
        </div>
      </div>

      {/* Detail card */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-card p-6">
        <dl className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <Field label="Attendees"    value={data.attendees} />
          <Field label="Created"      value={data.createdAt?.replace("T", " ").slice(0, 16)} />
          <div className="md:col-span-2">
            <Field label="Meeting Content" value={data.content} multiline />
          </div>
        </dl>
      </div>

      {/* AI Analysis */}
      <AiResponsePanel
        aiDescription={data.aiDescription}
        aiRecommendations={data.aiRecommendations}
      />

      {/* Metadata footer */}
      <div className="text-xs text-gray-400 flex flex-wrap gap-4 pb-2">
        <span>ID: {data.id}</span>
        <span>Last updated: {data.updatedAt?.replace("T", " ").slice(0, 16)}</span>
      </div>
    </div>
  );
}
