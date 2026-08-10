import os
import json
import logging
from datetime import datetime, timezone
from flask import Blueprint, request, jsonify
from services.groq_client import call_groq

logger = logging.getLogger(__name__)
report_bp = Blueprint("report", __name__)

# Load prompt template
PROMPT_PATH = os.path.join(os.path.dirname(__file__), "../prompts/report_prompt.txt")
with open(PROMPT_PATH, "r") as f:
    REPORT_PROMPT = f.read()

FALLBACK_REPORT = {
    "title": "Meeting Report Unavailable",
    "summary": "Unable to generate AI report at this time. Please try again later.",
    "overview": "The AI service is temporarily unavailable.",
    "key_items": ["Please retry when the service is available"],
    "recommendations": ["Contact support if the issue persists"]
}


@report_bp.route("/generate-report", methods=["POST"])
def generate_report():
    """
    POST /generate-report
    Body: { "minutes_text": "..." }
    Returns: { "title": "...", "summary": "...", "overview": "...", "key_items": [...], "recommendations": [...], "is_fallback": false }
    """
    data = request.get_json()

    # Input validation
    if not data or not data.get("minutes_text"):
        return jsonify({"error": "minutes_text is required"}), 400

    minutes_text = data["minutes_text"].strip()

    if len(minutes_text) < 10:
        return jsonify({"error": "minutes_text is too short"}), 400

    if len(minutes_text) > 10000:
        return jsonify({"error": "minutes_text exceeds maximum length"}), 400

    # Check for prompt injection
    injection_keywords = ["ignore previous", "ignore all", "system prompt", "jailbreak"]
    if any(kw in minutes_text.lower() for kw in injection_keywords):
        return jsonify({"error": "Invalid input detected"}), 400

    try:
        prompt = REPORT_PROMPT.format(minutes_text=minutes_text)
        raw_response = call_groq(prompt, max_tokens=2048, temperature=0.3)

        # Parse JSON response
        clean = raw_response.strip()
        if clean.startswith("```"):
            clean = clean.split("```")[1]
            if clean.startswith("json"):
                clean = clean[4:]
        clean = clean.strip()

        report = json.loads(clean)

        # Validate required fields
        required_fields = ["title", "summary", "overview", "key_items", "recommendations"]
        for field in required_fields:
            if field not in report:
                raise ValueError(f"Missing field: {field}")

        report["generated_at"] = datetime.now(timezone.utc).isoformat()
        report["is_fallback"] = False

        return jsonify(report), 200

    except Exception as e:
        logger.error(f"Generate report endpoint error: {e}")
        fallback = FALLBACK_REPORT.copy()
        fallback["generated_at"] = datetime.now(timezone.utc).isoformat()
        fallback["is_fallback"] = True
        return jsonify(fallback), 200
