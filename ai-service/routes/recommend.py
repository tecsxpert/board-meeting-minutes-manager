import os
import json
import logging
from datetime import datetime, timezone
from flask import Blueprint, request, jsonify
from services.groq_client import call_groq

logger = logging.getLogger(__name__)
recommend_bp = Blueprint("recommend", __name__)

# Load prompt template
PROMPT_PATH = os.path.join(os.path.dirname(__file__), "../prompts/recommend_prompt.txt")
with open(PROMPT_PATH, "r") as f:
    RECOMMEND_PROMPT = f.read()

FALLBACK_RECOMMENDATIONS = [
    {"action_type": "FOLLOW_UP", "description": "Review meeting outcomes and assign action items.", "priority": "HIGH"},
    {"action_type": "COMMUNICATION", "description": "Share meeting minutes with all stakeholders.", "priority": "MEDIUM"},
    {"action_type": "REVIEW", "description": "Schedule a follow-up meeting to track progress.", "priority": "LOW"}
]


@recommend_bp.route("/recommend", methods=["POST"])
def recommend():
    """
    POST /recommend
    Body: { "minutes_text": "..." }
    Returns: { "recommendations": [...], "generated_at": "...", "is_fallback": false }
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
        prompt = RECOMMEND_PROMPT.format(minutes_text=minutes_text)
        raw_response = call_groq(prompt, max_tokens=1024, temperature=0.3)

        # Parse JSON response
        # Strip markdown code blocks if present
        clean = raw_response.strip()
        if clean.startswith("```"):
            clean = clean.split("```")[1]
            if clean.startswith("json"):
                clean = clean[4:]
        clean = clean.strip()

        recommendations = json.loads(clean)

        # Validate structure
        if not isinstance(recommendations, list):
            raise ValueError("Response is not a list")

        return jsonify({
            "recommendations": recommendations,
            "generated_at": datetime.now(timezone.utc).isoformat(),
            "is_fallback": False
        }), 200

    except Exception as e:
        logger.error(f"Recommend endpoint error: {e}")
        return jsonify({
            "recommendations": FALLBACK_RECOMMENDATIONS,
            "generated_at": datetime.now(timezone.utc).isoformat(),
            "is_fallback": True
        }), 200
