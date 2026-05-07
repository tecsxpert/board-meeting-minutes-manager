import { useState } from "react";
import api from "../services/api";

export default function AiPanel({ meetingId, minutesText }) {
    const [activeTab, setActiveTab] = useState(null);
    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState(null);
    const [error, setError] = useState(null);

    const callAi = async (type) => {
        setActiveTab(type);
        setLoading(true);
        setResult(null);
        setError(null);

        const endpoints = {
            describe: `/api/meetings/${meetingId}/ai/describe`,
            recommend: `/api/meetings/${meetingId}/ai/recommend`,
            report: `/api/meetings/${meetingId}/ai/generate-report`,
        };

        try {
            const response = await api.post(endpoints[type], { minutesText });
            setResult(response.data);
        } catch {
            setError("AI service unavailable. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="mt-6 border border-gray-200 rounded-lg shadow-sm bg-white">
            {/* Panel Header */}
            <div className="bg-primary px-4 py-3 rounded-t-lg flex items-center gap-2">
                <span className="text-white text-lg">🤖</span>
                <h2 className="text-white font-semibold text-sm">AI Assistant</h2>
            </div>

            {/* Action Buttons */}
            <div className="flex gap-2 p-4 border-b border-gray-100">
                <button
                    onClick={() => callAi("describe")}
                    className={`px-3 py-2 rounded text-sm font-medium border transition-all ${activeTab === "describe"
                        ? "bg-primary text-white border-primary"
                        : "bg-white text-primary border-primary hover:bg-blue-50"
                        }`}
                >
                    📝 Describe
                </button>
                <button
                    onClick={() => callAi("recommend")}
                    className={`px-3 py-2 rounded text-sm font-medium border transition-all ${activeTab === "recommend"
                        ? "bg-primary text-white border-primary"
                        : "bg-white text-primary border-primary hover:bg-blue-50"
                        }`}
                >
                    💡 Recommend
                </button>
                <button
                    onClick={() => callAi("report")}
                    className={`px-3 py-2 rounded text-sm font-medium border transition-all ${activeTab === "report"
                        ? "bg-primary text-white border-primary"
                        : "bg-white text-primary border-primary hover:bg-blue-50"
                        }`}
                >
                    📊 Generate Report
                </button>
            </div>

            {/* Content Area */}
            <div className="p-4 min-h-32">

                {/* Loading Spinner */}
                {loading && (
                    <div className="flex items-center gap-3 text-gray-500">
                        <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-primary"></div>
                        <span className="text-sm">AI is thinking...</span>
                    </div>
                )}

                {/* Error */}
                {error && !loading && (
                    <div className="bg-red-50 border border-red-200 rounded p-3 text-red-600 text-sm">
                        ⚠️ {error}
                    </div>
                )}

                {/* Result Card */}
                {result && !loading && (
                    <div className="space-y-3">

                        {/* Fallback warning */}
                        {result.is_fallback && (
                            <div className="bg-yellow-50 border border-yellow-200 rounded p-2 text-yellow-700 text-xs">
                                ⚠️ AI returned a fallback response
                            </div>
                        )}

                        {/* Describe result */}
                        {activeTab === "describe" && (
                            <div className="bg-blue-50 border border-blue-100 rounded-lg p-4">
                                <p className="text-xs font-semibold text-primary uppercase mb-2">
                                    Description
                                </p>
                                <p className="text-sm text-gray-700 leading-relaxed">
                                    {result.description}
                                </p>
                                <p className="text-xs text-gray-400 mt-2">
                                    Generated at: {result.generated_at}
                                </p>
                            </div>
                        )}

                        {/* Recommend result */}
                        {activeTab === "recommend" && (
                            <div className="space-y-2">
                                <p className="text-xs font-semibold text-primary uppercase mb-2">
                                    Recommendations
                                </p>
                                {Array.isArray(result.recommendations) &&
                                    result.recommendations.map((rec, i) => (
                                        <div
                                            key={i}
                                            className="bg-green-50 border border-green-100 rounded-lg p-3"
                                        >
                                            <div className="flex justify-between items-center mb-1">
                                                <span className="text-xs font-semibold text-green-700">
                                                    {rec.action_type}
                                                </span>
                                                <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${rec.priority === "HIGH"
                                                    ? "bg-red-100 text-red-600"
                                                    : rec.priority === "MEDIUM"
                                                        ? "bg-yellow-100 text-yellow-600"
                                                        : "bg-gray-100 text-gray-500"
                                                    }`}>
                                                    {rec.priority}
                                                </span>
                                            </div>
                                            <p className="text-sm text-gray-700">{rec.description}</p>
                                        </div>
                                    ))}
                            </div>
                        )}

                        {/* Report result */}
                        {activeTab === "report" && (
                            <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 space-y-3">
                                <p className="text-xs font-semibold text-primary uppercase">
                                    Generated Report
                                </p>
                                {result.title && (
                                    <h3 className="text-base font-bold text-gray-800">
                                        {result.title}
                                    </h3>
                                )}
                                {result.summary && (
                                    <p className="text-sm text-gray-600">{result.summary}</p>
                                )}
                                {result.overview && (
                                    <div>
                                        <p className="text-xs font-semibold text-gray-500 uppercase mb-1">
                                            Overview
                                        </p>
                                        <p className="text-sm text-gray-700">{result.overview}</p>
                                    </div>
                                )}
                                {result.key_items && (
                                    <div>
                                        <p className="text-xs font-semibold text-gray-500 uppercase mb-1">
                                            Key Items
                                        </p>
                                        <ul className="list-disc list-inside text-sm text-gray-700 space-y-1">
                                            {result.key_items.map((item, i) => (
                                                <li key={i}>{item}</li>
                                            ))}
                                        </ul>
                                    </div>
                                )}
                            </div>
                        )}

                    </div>
                )}

                {/* Default state — nothing selected yet */}
                {!loading && !result && !error && (
                    <p className="text-sm text-gray-400 text-center mt-4">
                        Click a button above to get AI insights for this meeting
                    </p>
                )}
            </div>
        </div>
    );
}