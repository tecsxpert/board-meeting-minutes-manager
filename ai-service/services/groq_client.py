import os
import time
import logging
from groq import Groq

logger = logging.getLogger(__name__)

client = Groq(api_key=os.getenv("GROQ_API_KEY"))
MODEL = os.getenv("GROQ_MODEL", "llama-3.3-70b-versatile")


def call_groq(prompt: str, max_tokens: int = 1024, temperature: float = 0.3) -> str:
    """
    Call Groq API with 3-retry backoff on failure.
    Returns the response text or raises an exception.
    """
    last_error = None
    for attempt in range(3):
        try:
            response = client.chat.completions.create(
                model=MODEL,
                messages=[{"role": "user", "content": prompt}],
                max_tokens=max_tokens,
                temperature=temperature,
            )
            return response.choices[0].message.content.strip()
        except Exception as e:
            last_error = e
            wait = 2 ** attempt  # 1s, 2s, 4s
            logger.warning(f"Groq API attempt {attempt + 1} failed: {e}. Retrying in {wait}s...")
            time.sleep(wait)

    logger.error(f"Groq API failed after 3 attempts: {last_error}")
    raise last_error
