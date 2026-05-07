from flask import Blueprint, jsonify

main_bp = Blueprint('main', __name__)

@main_bp.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        "status": "active", 
        "role": "AI Developer 1",
        "message": "Project Cam AI Server is running!"
    }), 200