export default function ApiDocsPage() {
    const swaggerUrl = "http://localhost:8080/swagger-ui.html";

    return (
        <div className="max-w-3xl mx-auto">
            <h1 className="text-2xl font-bold text-gray-800 mb-4">
                API Documentation
            </h1>
            <div className="bg-white rounded-xl shadow p-6">
                <p className="text-gray-600 mb-4">
                    Full API documentation is available via Swagger UI once the backend is running.
                </p>
                <button
                    onClick={() => window.open(swaggerUrl, "_blank")}
                    className="bg-blue-700 text-white px-5 py-2 rounded-lg hover:bg-blue-800 transition text-sm font-medium"
                >
                    Open Swagger UI
                </button>
                <div className="mt-6 text-sm text-gray-500">
                    <p className="font-medium text-gray-700 mb-2">Available Endpoints:</p>
                    <ul className="space-y-1 list-disc list-inside">
                        <li>GET /api/minutes - List all meetings</li>
                        <li>GET /api/minutes/:id - Get meeting by ID</li>
                        <li>POST /api/minutes - Create new meeting</li>
                        <li>PUT /api/minutes/:id - Update meeting</li>
                        <li>DELETE /api/minutes/:id - Soft delete</li>
                        <li>GET /api/minutes/search - Search meetings</li>
                        <li>GET /api/minutes/export - Download CSV</li>
                        <li>POST /api/minutes/upload - Upload file</li>
                        <li>GET /api/minutes/stats - Dashboard stats</li>
                    </ul>
                </div>
            </div>
        </div>
    );
}