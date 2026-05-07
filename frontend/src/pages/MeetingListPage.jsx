import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import Navbar from "../components/Navbar";

export default function MeetingListPage() {
    const [meetings, setMeetings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const navigate = useNavigate();

    const fetchMeetings = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await api.get(`/api/meetings?page=${page}&size=10`);
            setMeetings(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch {
            setError("Failed to load meetings. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        fetchMeetings();
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [page]);

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this meeting?")) return;
        try {
            await api.delete(`/api/meetings/${id}`);
            setMeetings((prev) => prev.filter((m) => m.id !== id));
        } catch {
            setError("Failed to delete meeting. Please try again.");
        }
    };

    // Loading state
    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50">
                <Navbar />
                <div className="flex justify-center items-center h-64">
                    <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-700"></div>
                    <span className="ml-3 text-gray-500">Loading meetings...</span>
                </div>
            </div>
        );
    }

    // Error state
    if (error) {
        return (
            <div className="min-h-screen bg-gray-50">
                <Navbar />
                <div className="flex justify-center items-center h-64">
                    <div className="text-red-500 text-center">
                        <p className="text-lg font-semibold">Something went wrong</p>
                        <p className="text-sm">{error}</p>
                        <button
                            onClick={fetchMeetings}
                            className="mt-4 px-4 py-2 bg-blue-700 text-white rounded hover:bg-blue-800"
                        >
                            Retry
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <Navbar />
            <div className="p-6 max-w-6xl mx-auto">

                {/* Header */}
                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-2xl font-bold text-gray-800">
                        Board Meeting Minutes
                    </h1>
                    <button
                        onClick={() => navigate("/meetings/new")}
                        className="px-4 py-2 bg-blue-700 text-white rounded-lg hover:bg-blue-800 text-sm font-medium transition"
                    >
                        + New Meeting
                    </button>
                </div>

                {/* Empty state */}
                {meetings.length === 0 ? (
                    <div className="flex flex-col justify-center items-center h-64 border-2 border-dashed border-gray-300 rounded-lg">
                        <p className="text-gray-400 text-lg">No meetings found</p>
                        <p className="text-gray-400 text-sm mt-1">
                            Create your first board meeting minutes
                        </p>
                        <button
                            onClick={() => navigate("/meetings/new")}
                            className="mt-4 px-4 py-2 bg-blue-700 text-white rounded-lg hover:bg-blue-800 text-sm transition"
                        >
                            + New Meeting
                        </button>
                    </div>
                ) : (
                    <>
                        {/* Table */}
                        <div className="overflow-x-auto rounded-lg shadow">
                            <table className="min-w-full bg-white border border-gray-200">
                                <thead className="bg-blue-700 text-white">
                                    <tr>
                                        <th className="px-4 py-3 text-left text-sm font-semibold">Title</th>
                                        <th className="px-4 py-3 text-left text-sm font-semibold">Meeting Date</th>
                                        <th className="px-4 py-3 text-left text-sm font-semibold">Status</th>
                                        <th className="px-4 py-3 text-left text-sm font-semibold">Created By</th>
                                        <th className="px-4 py-3 text-left text-sm font-semibold">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {meetings.map((meeting, index) => (
                                        <tr
                                            key={meeting.id}
                                            className={index % 2 === 0 ? "bg-white" : "bg-gray-50"}
                                        >
                                            {/* ← Title now clickable to detail page */}
                                            <td
                                                className="px-4 py-3 text-sm text-blue-700 font-medium cursor-pointer hover:underline"
                                                onClick={() => navigate(`/meetings/${meeting.id}`)}
                                            >
                                                {meeting.title}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {new Date(meeting.meetingDate).toLocaleDateString()}
                                            </td>
                                            <td className="px-4 py-3 text-sm">
                                                <span className={`px-2 py-1 rounded-full text-xs font-semibold ${meeting.status === "PUBLISHED"
                                                        ? "bg-green-100 text-green-700"
                                                        : meeting.status === "DRAFT"
                                                            ? "bg-yellow-100 text-yellow-700"
                                                            : "bg-gray-100 text-gray-600"
                                                    }`}>
                                                    {meeting.status}
                                                </span>
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {meeting.createdBy || "—"}
                                            </td>
                                            <td className="px-4 py-3 text-sm flex gap-3">
                                                <button
                                                    onClick={() => navigate(`/meetings/${meeting.id}/edit`)}
                                                    className="text-blue-600 hover:underline font-medium"
                                                >
                                                    Edit
                                                </button>
                                                <button
                                                    onClick={() => handleDelete(meeting.id)}
                                                    className="text-red-500 hover:underline font-medium"
                                                >
                                                    Delete
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        {/* Pagination */}
                        <div className="flex justify-between items-center mt-4">
                            <p className="text-sm text-gray-500">
                                Page {page + 1} of {totalPages}
                            </p>
                            <div className="flex gap-2">
                                <button
                                    onClick={() => setPage((p) => Math.max(p - 1, 0))}
                                    disabled={page === 0}
                                    className="px-3 py-1 text-sm border rounded disabled:opacity-40 hover:bg-gray-100"
                                >
                                    Previous
                                </button>
                                <button
                                    onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
                                    disabled={page >= totalPages - 1}
                                    className="px-3 py-1 text-sm border rounded disabled:opacity-40 hover:bg-gray-100"
                                >
                                    Next
                                </button>
                            </div>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}