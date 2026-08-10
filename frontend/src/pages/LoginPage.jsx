import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { loginUser } from "../services/authService";

export default function LoginPage() {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [form, setForm] = useState({ username: "", password: "" });
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
        setError(null);
    };

    const validate = () => {
        if (!form.username.trim()) return "Username is required.";
        if (!form.password) return "Password is required.";
        if (form.password.length < 6) return "Password must be at least 6 characters.";
        return null;
    };

    const handleSubmit = async () => {
        const validationError = validate();
        if (validationError) {
            setError(validationError);
            return;
        }

        setLoading(true);
        setError(null);
        try {
            const res = await loginUser(form.username, form.password);
            // Backend returns { token }
            login(res.data.token, {
                username: form.username,
                role: "USER",
            });
            navigate("/");
        } catch (err) {
            setError(
                err.response?.status === 401
                    ? "Invalid username or password."
                    : "Login failed. Please try again."
            );
        } finally {
            setLoading(false);
        }
    };

    // Allow Enter key to submit
    const handleKeyDown = (e) => {
        if (e.key === "Enter") handleSubmit();
    };

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
            <div className="bg-white rounded-xl shadow-md w-full max-w-md p-8">

                {/* Logo / Title */}
                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-14 h-14 bg-blue-700 rounded-full mb-3">
                        <svg className="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                d="M9 12h6m-6 4h6m2 4H7a2 2 0 01-2-2V6a2 2 0 012-2h5l2 2h5a2 2 0 012 2v12a2 2 0 01-2 2z" />
                        </svg>
                    </div>
                    <h1 className="text-2xl font-bold text-gray-800">Board Meeting Minutes</h1>
                    <p className="text-sm text-gray-500 mt-1">Sign in to your account</p>
                </div>

                {/* Error */}
                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 text-sm px-4 py-3 rounded-lg mb-4">
                        {error}
                    </div>
                )}

                {/* Username */}
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
                    <input
                        type="text"
                        name="username"
                        value={form.username}
                        onChange={handleChange}
                        onKeyDown={handleKeyDown}
                        placeholder="Enter your username"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>

                {/* Password */}
                <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                    <input
                        type="password"
                        name="password"
                        value={form.password}
                        onChange={handleChange}
                        onKeyDown={handleKeyDown}
                        placeholder="Enter your password"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>

                {/* Submit */}
                <button
                    onClick={handleSubmit}
                    disabled={loading}
                    className="w-full bg-blue-700 text-white py-2 rounded-lg hover:bg-blue-800 transition font-medium disabled:opacity-50"
                >
                    {loading ? "Signing in..." : "Sign In"}
                </button>

            </div>
        </div>
    );
}