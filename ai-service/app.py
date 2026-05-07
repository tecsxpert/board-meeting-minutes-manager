from flask import Flask
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

app = Flask(__name__)

limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"]
)

# Register route blueprints here as they are built
# from routes.describe import describe_bp
# from routes.recommend import recommend_bp
# from routes.report import report_bp
# app.register_blueprint(describe_bp)
# app.register_blueprint(recommend_bp)
# app.register_blueprint(report_bp)

@app.route("/health", methods=["GET"])
def health():
    return {"status": "ok", "service": "ai-service"}, 200


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
