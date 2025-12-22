"""Main application entry point."""

from __future__ import annotations

import asyncio
import logging
import signal
import sys

from src.config import Config
from src.infrastructure.external.genius_lyrics_repository import (
    GeniusLyricsRepository,
)
from src.infrastructure.messaging.redis_message_repository import (
    RedisMessageRepository,
)
from src.presentation.lyrics_fetcher_service import LyricsFetcherService
from src.use_cases.search_lyrics import SearchLyricsUseCase


def setup_logging(log_level: str) -> None:
    """
    Setup logging configuration.

    Args:
        log_level: Logging level (DEBUG, INFO, WARNING, ERROR, CRITICAL)
    """
    logging.basicConfig(
        level=getattr(logging, log_level.upper()),
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
        handlers=[logging.StreamHandler(sys.stdout)],
    )


def create_service(config: Config) -> LyricsFetcherService:
    """
    Create and wire up the service with all dependencies.

    Args:
        config: Application configuration

    Returns:
        Configured LyricsFetcherService instance
    """
    # Create repositories
    lyrics_repository = GeniusLyricsRepository(api_token=config.genius_api_token)

    message_repository = RedisMessageRepository(
        host=config.redis_host,
        port=config.redis_port,
        db=config.redis_db,
        password=config.redis_password,
        request_channel=config.redis_request_channel,
        result_channel=config.redis_result_channel,
    )

    # Create use case
    search_lyrics_use_case = SearchLyricsUseCase(lyrics_repository=lyrics_repository)

    # Create service
    service = LyricsFetcherService(
        message_repository=message_repository,
        search_lyrics_use_case=search_lyrics_use_case,
    )

    return service


async def main() -> None:
    """Main application function."""
    # Load configuration
    config = Config.from_env()

    # Setup logging
    setup_logging(config.log_level)

    logger = logging.getLogger(__name__)
    logger.info("Starting Lyrics Fetcher application")

    # Create service
    service = create_service(config)

    # Setup signal handlers for graceful shutdown
    loop = asyncio.get_running_loop()

    def signal_handler() -> None:
        logger.info("Received shutdown signal")
        asyncio.create_task(service.stop())

    for sig in (signal.SIGINT, signal.SIGTERM):
        loop.add_signal_handler(sig, signal_handler)

    # Start service
    try:
        await service.start()
    except KeyboardInterrupt:
        logger.info("Interrupted by user")
    except Exception as e:
        logger.error(f"Application error: {e}", exc_info=True)
        sys.exit(1)


if __name__ == "__main__":
    asyncio.run(main())
