import os
import json
import logging
from datetime import datetime, timezone
from flask import Blueprint, request, jsonify
from services.groq_client import call_groq

logger = logging.getLogger(__name__)
describe_bp = Blueprint("describe", __name__)

# Load prompt template
PROMPT_PATH = os.path.join(os.path.dirname(__file__), "../prompts/describe_prompt.txt")
with open(PROMPT_PATH, "r") as f:
    DESCRIBE_PROMPT = f.read()

FALLBACK_DESCRIPTION = "Unable to generate AI description at this time. Please try again later."


@describe_bp.route("/describe", methods=["POST"])
def describe():
    """
    POST /describe
    Body: { "minutes_text": "..." }
    Returns: { "description": "...", "generated_at": "...", "is_fallback": false }
    """
    data = request.get_json()

    # Input validation
    if not data or not data.get("minutes_text"):
        return jsonify({"error": "minutes_text is required"}), 400

    minutes_text = data["minutes_text"].strip()

    # Sanitize — strip HTML tags and check for prompt injection
    if len(minutes_text) < 10:
        return jsonify({"error": "minutes_text is too short"}), 400

    if len(minutes_text) > 10000:
        return jsonify({"error": "minutes_text exceeds maximum length of 10000 characters"}), 400

    # Check for prompt injection attempts
    injection_keywords = ["ignore previous", "ignore all", "system prompt", "jailbreak"]
    if any(kw in minutes_text.lower() for kw in injection_keywords):
        return jsonify({"error": "Invalid input detected"}), 400

    try:
        prompt = DESCRIBE_PROMPT.format(minutes_text=minutes_text)
        description = call_groq(prompt, max_tokens=512, temperature=0.3)

        return jsonify({
            "description": description,
            "generated_at": datetime.now(timezone.utc).isoformat(),
            "is_fallback": False
        }), 200

    except Exception as e:
        logger.error(f"Describe endpoint error: {e}")
        return jsonify({
            "description": FALLBACK_DESCRIPTION,
            "generated_at": datetime.now(timezone.utc).isoformat(),
            "is_fallback": True
        }), 200
