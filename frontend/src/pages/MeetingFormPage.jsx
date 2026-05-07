import { useEffect, useState } from "react";
import { getMeetingById, createMeeting, updateMeeting } from "../services/meetingsService";

const EMPTY_FORM = {
    title: "",
    meetingDate: "",
    location: "",
    attendees: "",
    agenda: "",
    minutesText: "",
    status: "DRAFT",
};

const STATUS_OPTIONS = ["DRAFT", "PUBLISHED", "ARCHIVED"];

export default function MeetingFormPage({ meetingId = null }) {
    const isEdit = Boolean(meetingId);

    const [form, setForm] = useState(EMPTY_FORM);
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [success, setSuccess] = useState(false);
    const [apiError, setApiError] = useState(null);

    // Load existing record when editing
    useEffect(() => {
        if (isEdit) {
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setLoading(true);
            getMeetingById(meetingId)
                .then((res) => setForm(res.data))
                .catch(() => setApiError("Failed to load meeting details."))
                .finally(() => setLoading(false));
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [meetingId]);

    const validate = () => {
        const e = {};
        if (!form.title.trim()) e.title = "Title is required.";
        if (form.title.length > 255) e.title = "Title must be under 255 characters.";
        if (!form.meetingDate) e.meetingDate = "Meeting date is required.";
        if (!form.minutesText.trim()) e.minutesText = "Minutes text is required.";
        if (!form.status) e.status = "Status is required.";
        return e;
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
        // Clear error on change
        setErrors((prev) => ({ ...prev, [name]: undefined }));
    };

    const handleSubmit = async () => {
        const validationErrors = validate();
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }

        setSaving(true);
        setApiError(null);
        try {
            if (isEdit) {
                await updateMeeting(meetingId, form);
            } else {
                await createMeeting(form);
            }
            setSuccess(true);
            setTimeout(() => (window.location.href = "/"), 1500);
        } catch (err) {
            setApiError(
                err.response?.data?.message || "Something went wrong. Please try again."
            );
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="animate-spin rounded-full h-10 w-10 border-4 border-blue-600 border-t-transparent" />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-2xl mx-auto bg-white rounded-xl shadow p-8">

                {/* Header */}
                <div className="flex items-center justify-between mb-6">
                    <h1 className="text-2xl font-bold text-gray-800">
                        {isEdit ? "Edit Meeting Minutes" : "New Meeting Minutes"}
                    </h1>
                    <button
                        onClick={() => (window.location.href = "/")}
                        className="text-sm text-gray-500 hover:text-gray-700"
                    >
                        ← Back to list
                    </button>
                </div>

                {/* Success Banner */}
                {success && (
                    <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg mb-4">
                        ✓ Meeting {isEdit ? "updated" : "created"} successfully! Redirecting...
                    </div>
                )}

                {/* API Error */}
                {apiError && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4">
                        {apiError}
                    </div>
                )}

                {/* Form Fields */}
                <div className="space-y-5">

                    {/* Title */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Title <span className="text-red-500">*</span>
                        </label>
                        <input
                            type="text"
                            name="title"
                            value={form.title}
                            onChange={handleChange}
                            placeholder="e.g. Q2 Board Review Meeting"
                            className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 
                ${errors.title ? "border-red-400" : "border-gray-300"}`}
                        />
                        {errors.title && <p className="text-red-500 text-xs mt-1">{errors.title}</p>}
                    </div>

                    {/* Meeting Date */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Meeting Date <span className="text-red-500">*</span>
                        </label>
                        <input
                            type="date"
                            name="meetingDate"
                            value={form.meetingDate}
                            onChange={handleChange}
                            className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500
                ${errors.meetingDate ? "border-red-400" : "border-gray-300"}`}
                        />
                        {errors.meetingDate && <p className="text-red-500 text-xs mt-1">{errors.meetingDate}</p>}
                    </div>

                    {/* Location */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Location</label>
                        <input
                            type="text"
                            name="location"
                            value={form.location}
                            onChange={handleChange}
                            placeholder="e.g. Conference Room B"
                            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    {/* Attendees */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Attendees</label>
                        <input
                            type="text"
                            name="attendees"
                            value={form.attendees}
                            onChange={handleChange}
                            placeholder="e.g. John, Sarah, Mike"
                            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    {/* Agenda */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Agenda</label>
                        <textarea
                            name="agenda"
                            value={form.agenda}
                            onChange={handleChange}
                            rows={3}
                            placeholder="List agenda items..."
                            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
                        />
                    </div>

                    {/* Minutes Text */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Minutes <span className="text-red-500">*</span>
                        </label>
                        <textarea
                            name="minutesText"
                            value={form.minutesText}
                            onChange={handleChange}
                            rows={5}
                            placeholder="Write full meeting minutes here..."
                            className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none
                ${errors.minutesText ? "border-red-400" : "border-gray-300"}`}
                        />
                        {errors.minutesText && <p className="text-red-500 text-xs mt-1">{errors.minutesText}</p>}
                    </div>

                    {/* Status */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Status <span className="text-red-500">*</span>
                        </label>
                        <select
                            name="status"
                            value={form.status}
                            onChange={handleChange}
                            className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500
                ${errors.status ? "border-red-400" : "border-gray-300"}`}
                        >
                            {STATUS_OPTIONS.map((s) => (
                                <option key={s} value={s}>{s}</option>
                            ))}
                        </select>
                        {errors.status && <p className="text-red-500 text-xs mt-1">{errors.status}</p>}
                    </div>

                </div>

                {/* Submit Button */}
                <div className="mt-8 flex gap-3">
                    <button
                        onClick={handleSubmit}
                        disabled={saving}
                        className="bg-blue-700 text-white px-6 py-2 rounded-lg hover:bg-blue-800 transition disabled:opacity-50"
                    >
                        {saving ? "Saving..." : isEdit ? "Update Meeting" : "Create Meeting"}
                    </button>
                    <button
                        onClick={() => (window.location.href = "/")}
                        className="border border-gray-300 text-gray-600 px-6 py-2 rounded-lg hover:bg-gray-50 transition"
                    >
                        Cancel
                    </button>
                </div>

            </div>
        </div>
    );
}