import os
import time
import logging
from flask import Flask, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from dotenv import load_dotenv

load_dotenv()

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# Rate limiting — 30 requests per minute per IP
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"]
)

# Register route blueprints
from routes.describe import describe_bp
from routes.recommend import recommend_bp
from routes.report import report_bp

app.register_blueprint(describe_bp)
app.register_blueprint(recommend_bp)
app.register_blueprint(report_bp)

# Track startup time for uptime calculation
START_TIME = time.time()


@app.route("/health", methods=["GET"])
def health():
    uptime_seconds = int(time.time() - START_TIME)
    return jsonify({
        "service": "ai-service",
        "status": "ok",
        "model": os.getenv("GROQ_MODEL", "llama-3.3-70b-versatile"),
        "uptime_seconds": uptime_seconds
    }), 200


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
