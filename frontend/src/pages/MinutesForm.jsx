// MinutesForm.jsx: Dual-purpose create/edit form for board minutes.
// When :id param exists, prefetches data and populates fields; submits PUT or POST accordingly.

import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";

const STATUS_OPTIONS = ["DRAFT", "PUBLISHED", "ARCHIVED"];

export default function MinutesForm() {
  const { id }     = useParams();   // present on edit route
  const navigate   = useNavigate();
  const isEdit     = Boolean(id);

  const [form, setForm] = useState({
    title:       "",
    meetingDate: "",
    attendees:   "",
    content:     "",
    status:      "DRAFT",
    aiDescription:     "",
    aiRecommendations: "",
  });

  const [errors,     setErrors]     = useState({});
  const [loading,    setLoading]    = useState(isEdit);
  const [submitting, setSubmitting] = useState(false);
  const [fetchError, setFetchError] = useState("");

  // ── Pre-fetch for edit mode ──────────────────────────────────────────────
  useEffect(() => {
    if (!isEdit) return;
    (async () => {
      try {
        const { data } = await api.get(`/minutes/${id}`);
        setForm({
          title:             data.title       ?? "",
          meetingDate:       data.meetingDate ?? "",
          attendees:         data.attendees   ?? "",
          content:           data.content     ?? "",
          status:            data.status      ?? "DRAFT",
          aiDescription:     data.aiDescription     ?? "",
          aiRecommendations: data.aiRecommendations ?? "",
        });
      } catch {
        setFetchError("Failed to load record. It may have been deleted.");
      } finally {
        setLoading(false);
      }
    })();
  }, [id, isEdit]);

  // ── Validation ────────────────────────────────────────────────────────────
  const validate = () => {
    const errs = {};
    if (!form.title.trim())       errs.title       = "Title is required";
    if (!form.meetingDate)        errs.meetingDate  = "Meeting date is required";
    return errs;
  };

  // ── Submit ────────────────────────────────────────────────────────────────
  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length > 0) {
      setErrors(errs);
      return;
    }

    setSubmitting(true);
    setErrors({});
    try {
      if (isEdit) {
        await api.put(`/minutes/${id}`, form);
      } else {
        await api.post("/minutes", form);
      }
      navigate("/minutes");
    } catch (err) {
      const serverErrors = err.response?.data?.fieldErrors;
      if (serverErrors) {
        setErrors(serverErrors);
      } else {
        setErrors({ _general: err.response?.data?.message ?? "Save failed. Please try again." });
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: undefined }));
  };

  // ── Loading skeleton ──────────────────────────────────────────────────────
  if (loading) {
    return (
      <div className="max-w-3xl mx-auto space-y-4 animate-pulse">
        <div className="h-8 w-48 bg-gray-200 rounded" />
        <div className="bg-white rounded-xl border p-6 space-y-4">
          {[1,2,3,4].map(i => <div key={i} className="h-10 bg-gray-100 rounded" />)}
        </div>
      </div>
    );
  }

  if (fetchError) {
    return (
      <div className="max-w-3xl mx-auto">
        <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-center text-red-700">
          <div className="text-3xl mb-2">⚠️</div>
          <p className="font-medium">{fetchError}</p>
          <button onClick={() => navigate("/minutes")} className="mt-4 text-sm text-brand-600 underline">
            Back to list
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto animate-fade-in">

      {/* Page header */}
      <div className="flex items-center gap-3 mb-6">
        <button
          onClick={() => navigate("/minutes")}
          className="p-2 rounded-lg text-gray-500 hover:bg-gray-100 transition-colors"
          aria-label="Go back"
        >
          ←
        </button>
        <div>
          <h2 className="text-xl font-bold text-gray-900">
            {isEdit ? "Edit Minutes" : "New Minutes"}
          </h2>
          <p className="text-sm text-gray-500 mt-0.5">
            {isEdit ? `Editing record ${id}` : "Create a new board minutes record"}
          </p>
        </div>
      </div>

      {/* Form card */}
      <form id="minutes-form" onSubmit={handleSubmit} noValidate>
        <div className="bg-white rounded-xl border border-gray-200 shadow-card p-6 space-y-5">

          {/* General error */}
          {errors._general && (
            <div className="bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-3 rounded-lg">
              ⚠️ {errors._general}
            </div>
          )}

          {/* Title */}
          <div>
            <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-1">
              Title <span className="text-red-500">*</span>
            </label>
            <input
              id="title"
              name="title"
              type="text"
              value={form.title}
              onChange={handleChange}
              placeholder="e.g. Q1 2026 Strategy Review"
              className={`
                w-full px-4 py-2.5 rounded-lg border text-sm text-gray-900
                focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent transition-shadow
                ${errors.title ? "border-red-400 bg-red-50" : "border-gray-300"}
              `}
            />
            {errors.title && (
              <p id="error-title" className="mt-1 text-xs text-red-600">{errors.title}</p>
            )}
          </div>

          {/* Meeting Date + Status — 2 column */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label htmlFor="meetingDate" className="block text-sm font-medium text-gray-700 mb-1">
                Meeting Date <span className="text-red-500">*</span>
              </label>
              <input
                id="meetingDate"
                name="meetingDate"
                type="date"
                value={form.meetingDate}
                onChange={handleChange}
                className={`
                  w-full px-4 py-2.5 rounded-lg border text-sm text-gray-900
                  focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent transition-shadow
                  ${errors.meetingDate ? "border-red-400 bg-red-50" : "border-gray-300"}
                `}
              />
              {errors.meetingDate && (
                <p id="error-date" className="mt-1 text-xs text-red-600">{errors.meetingDate}</p>
              )}
            </div>

            <div>
              <label htmlFor="status" className="block text-sm font-medium text-gray-700 mb-1">
                Status
              </label>
              <select
                id="status"
                name="status"
                value={form.status}
                onChange={handleChange}
                className="w-full px-4 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-900 bg-white focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent transition-shadow"
              >
                {STATUS_OPTIONS.map((s) => (
                  <option key={s} value={s}>{s}</option>
                ))}
              </select>
            </div>
          </div>

          {/* Attendees */}
          <div>
            <label htmlFor="attendees" className="block text-sm font-medium text-gray-700 mb-1">
              Attendees
            </label>
            <textarea
              id="attendees"
              name="attendees"
              rows={2}
              value={form.attendees}
              onChange={handleChange}
              placeholder="e.g. CEO, CFO, CTO, Board Members"
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent transition-shadow resize-y"
            />
          </div>

          {/* Content */}
          <div>
            <label htmlFor="content" className="block text-sm font-medium text-gray-700 mb-1">
              Meeting Content / Notes
            </label>
            <textarea
              id="content"
              name="content"
              rows={6}
              value={form.content}
              onChange={handleChange}
              placeholder="Enter meeting minutes, decisions made, action items…"
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent transition-shadow resize-y"
            />
          </div>

          {/* AI Description */}
          <div>
            <label htmlFor="aiDescription" className="block text-sm font-medium text-gray-700 mb-1">
              🤖 AI Description <span className="text-gray-400 font-normal">(optional)</span>
            </label>
            <textarea
              id="aiDescription"
              name="aiDescription"
              rows={2}
              value={form.aiDescription}
              onChange={handleChange}
              placeholder="AI-generated summary…"
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent transition-shadow resize-y"
            />
          </div>

          {/* AI Recommendations */}
          <div>
            <label htmlFor="aiRecommendations" className="block text-sm font-medium text-gray-700 mb-1">
              🤖 AI Recommendations <span className="text-gray-400 font-normal">(optional, one per line)</span>
            </label>
            <textarea
              id="aiRecommendations"
              name="aiRecommendations"
              rows={3}
              value={form.aiRecommendations}
              onChange={handleChange}
              placeholder="1. Recommendation one&#10;2. Recommendation two"
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-900 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-transparent transition-shadow resize-y"
            />
          </div>

          {/* Actions */}
          <div className="flex items-center justify-end gap-3 pt-2 border-t border-gray-100">
            <button
              type="button"
              onClick={() => navigate("/minutes")}
              className="px-4 py-2.5 rounded-lg border border-gray-300 text-sm text-gray-700 hover:bg-gray-50 transition-colors min-h-[44px]"
            >
              Cancel
            </button>
            <button
              id="btn-submit"
              type="submit"
              disabled={submitting}
              className="
                px-6 py-2.5 rounded-lg bg-brand-500 text-white text-sm font-medium
                hover:bg-brand-600 disabled:opacity-60 disabled:cursor-not-allowed
                transition-colors flex items-center gap-2 min-h-[44px]
              "
            >
              {submitting ? (
                <>
                  <svg className="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                  </svg>
                  Saving…
                </>
              ) : isEdit ? "Save Changes" : "Create Record"}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
