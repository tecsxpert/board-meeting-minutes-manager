// AiResponsePanel.jsx: Displays AI-generated description and recommendations for a board minutes record.
// Shows a skeleton loader when props are null; parses recommendations as a numbered list.

export default function AiResponsePanel({ aiDescription, aiRecommendations }) {
  const isEmpty = !aiDescription && !aiRecommendations;

  // ── Skeleton loader ───────────────────────────────────────────────────────
  if (isEmpty) {
    return (
      <div className="bg-blue-50 border border-blue-100 rounded-xl p-5 animate-pulse">
        <div className="flex items-center gap-2 mb-4">
          <span className="text-2xl">🤖</span>
          <div className="h-4 w-28 bg-blue-200 rounded" />
        </div>
        <div className="space-y-2 mb-4">
          <div className="h-3 w-full bg-blue-200 rounded" />
          <div className="h-3 w-5/6 bg-blue-200 rounded" />
          <div className="h-3 w-4/6 bg-blue-200 rounded" />
        </div>
        <div className="h-3 w-32 bg-blue-200 rounded mb-2" />
        <div className="space-y-1.5">
          {[1, 2, 3].map((i) => (
            <div key={i} className="flex gap-2">
              <div className="h-3 w-4 bg-blue-200 rounded flex-shrink-0" />
              <div className="h-3 bg-blue-200 rounded flex-1" />
            </div>
          ))}
        </div>
        <p className="text-xs text-blue-400 mt-4 italic">AI analysis not yet available for this record.</p>
      </div>
    );
  }

  // ── Parse recommendations as a numbered list ──────────────────────────────
  const recommendationLines = aiRecommendations
    ? aiRecommendations
        .split("\n")
        .map((line) => line.trim())
        .filter(Boolean)
    : [];

  return (
    <div className="bg-blue-50 border border-blue-100 rounded-xl p-5 animate-fade-in">
      {/* Header */}
      <div className="flex items-center gap-2 mb-4">
        <span className="text-2xl">🤖</span>
        <h3 className="text-brand-600 font-semibold text-sm tracking-wide uppercase">
          AI Analysis
        </h3>
      </div>

      {/* Description */}
      {aiDescription && (
        <div className="mb-4">
          <p className="text-xs font-semibold text-blue-700 uppercase tracking-wide mb-1">
            Summary
          </p>
          <p className="text-sm text-gray-700 leading-relaxed">{aiDescription}</p>
        </div>
      )}

      {/* Recommendations */}
      {recommendationLines.length > 0 && (
        <div>
          <p className="text-xs font-semibold text-blue-700 uppercase tracking-wide mb-2">
            Recommendations
          </p>
          <ol className="space-y-1.5">
            {recommendationLines.map((line, idx) => {
              // Strip leading "1." "2." etc if already present in the text
              const text = line.replace(/^\d+\.\s*/, "");
              return (
                <li key={idx} className="flex gap-2.5 text-sm text-gray-700">
                  <span className="flex-shrink-0 w-5 h-5 rounded-full bg-brand-500 text-white text-xs font-bold flex items-center justify-center mt-0.5">
                    {idx + 1}
                  </span>
                  <span className="leading-relaxed">{text}</span>
                </li>
              );
            })}
          </ol>
        </div>
      )}
    </div>
  );
}
