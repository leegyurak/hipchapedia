"""Configuration settings for the application."""

from __future__ import annotations

import os
from dataclasses import dataclass


@dataclass
class Config:
    """Application configuration."""

    # Genius API
    genius_api_token: str

    # Redis
    redis_host: str = "localhost"
    redis_port: int = 6379
    redis_db: int = 0
    redis_password: str | None = None
    redis_request_channel: str = "lyrics:requests"
    redis_result_channel: str = "lyrics:results"

    # Logging
    log_level: str = "INFO"

    @classmethod
    def from_env(cls) -> Config:
        """
        Load configuration from environment variables.

        Returns:
            Config instance
        """
        return cls(
            genius_api_token=os.getenv(
                "GENIUS_API_TOKEN",
                "TZMYC5TFeNDmvuS73xLBtyp5_Ehlh3_wnBu8DSwBn5VcQatOSLeKks536S6P1aA7",
            ),
            redis_host=os.getenv("REDIS_HOST", "localhost"),
            redis_port=int(os.getenv("REDIS_PORT", "6379")),
            redis_db=int(os.getenv("REDIS_DB", "0")),
            redis_password=os.getenv("REDIS_PASSWORD"),
            redis_request_channel=os.getenv("REDIS_REQUEST_CHANNEL", "lyrics:requests"),
            redis_result_channel=os.getenv("REDIS_RESULT_CHANNEL", "lyrics:results"),
            log_level=os.getenv("LOG_LEVEL", "INFO"),
        )
