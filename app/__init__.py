from flask import Flask
from dotenv import load_dotenv
import os

def create_app():
    load_dotenv() # This loads your API keys from .env
    app = Flask(__name__)
    
    from .routes import main_bp
    app.register_blueprint(main_bp)
    
    return app