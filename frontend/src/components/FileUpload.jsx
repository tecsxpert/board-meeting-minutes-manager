import { useState, useRef } from "react";
import api from "../services/api";

const ALLOWED_TYPES = ["application/pdf", "text/plain", "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"];
const MAX_SIZE_MB = 5;

export default function FileUpload({ meetingId, onUploadSuccess }) {
    const [dragging, setDragging] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
    const inputRef = useRef();

    const validateFile = (file) => {
        if (!ALLOWED_TYPES.includes(file.type)) {
            return "Only PDF, TXT, DOC, and DOCX files are allowed.";
        }
        if (file.size > MAX_SIZE_MB * 1024 * 1024) {
            return `File size must be under ${MAX_SIZE_MB}MB.`;
        }
        return null;
    };

    const handleUpload = async (file) => {
        const validationError = validateFile(file);
        if (validationError) { setError(validationError); return; }
        setUploading(true);
        setError(null);
        setSuccess(null);
        const formData = new FormData();
        formData.append("file", file);
        if (meetingId) formData.append("meetingId", meetingId);
        try {
            await api.post("/api/meetings/upload", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            setSuccess(`"${file.name}" uploaded successfully!`);
            if (onUploadSuccess) onUploadSuccess();
        } catch (err) {
            setError(err.response?.data?.message || "Upload failed. Please try again.");
        } finally {
            setUploading(false);
        }
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) handleUpload(file);
    };

    const handleDrop = (e) => {
        e.preventDefault();
        setDragging(false);
        const file = e.dataTransfer.files[0];
        if (file) handleUpload(file);
    };

    return (
        <div className="mt-4">
            <p className="text-sm font-medium text-gray-700 mb-2">Upload Supporting Document</p>
            <div
                onClick={() => inputRef.current.click()}
                onDragOver={(e) => { e.preventDefault(); setDragging(true); }}
                onDragLeave={() => setDragging(false)}
                onDrop={handleDrop}
                className={`border-2 border-dashed rounded-xl px-6 py-8 text-center cursor-pointer transition
          ${dragging ? "border-blue-500 bg-blue-50" : "border-gray-300 hover:border-blue-400 hover:bg-gray-50"}`}
            >
                <div className="text-3xl mb-2">📄</div>
                <p className="text-sm text-gray-600">
                    {uploading ? "Uploading..." : "Drag & drop a file here or click to browse"}
                </p>
                <p className="text-xs text-gray-400 mt-1">PDF, TXT, DOC, DOCX — max {MAX_SIZE_MB}MB</p>
                <input
                    ref={inputRef}
                    type="file"
                    className="hidden"
                    accept=".pdf,.txt,.doc,.docx"
                    onChange={handleFileChange}
                />
            </div>
            {uploading && (
                <div className="flex items-center gap-2 mt-3 text-sm text-blue-600">
                    <div className="animate-spin rounded-full h-4 w-4 border-2 border-blue-500 border-t-transparent" />
                    Uploading file...
                </div>
            )}
            {error && (
                <div className="mt-3 bg-red-50 border border-red-200 text-red-600 text-sm px-4 py-2 rounded-lg">
                    {error}
                </div>
            )}
            {success && (
                <div className="mt-3 bg-green-50 border border-green-200 text-green-600 text-sm px-4 py-2 rounded-lg">
                    ✓ {success}
                </div>
            )}
        </div>
    );
}