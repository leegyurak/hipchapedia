"""Lyrics fetcher service that processes Redis messages."""

from __future__ import annotations

import asyncio
import logging

from src.domain.repositories.message_repository import MessageRepository
from src.use_cases.search_lyrics import SearchLyricsUseCase

logger = logging.getLogger(__name__)


class LyricsFetcherService:
    """Service that listens to Redis requests and publishes results."""

    def __init__(
        self,
        message_repository: MessageRepository,
        search_lyrics_use_case: SearchLyricsUseCase,
    ) -> None:
        """
        Initialize the fetcher service.

        Args:
            message_repository: Repository for pub/sub operations
            search_lyrics_use_case: Use case for searching lyrics
        """
        self.message_repository = message_repository
        self.search_lyrics_use_case = search_lyrics_use_case
        self._running = False

    async def start(self) -> None:
        """Start the lyrics fetcher service."""
        logger.info("Starting lyrics fetcher service...")

        try:
            await self.message_repository.connect()
            self._running = True

            logger.info("Service started. Waiting for requests...")

            async for request in self.message_repository.subscribe_requests():
                if not self._running:
                    break

                logger.info(f"Processing request: {request.title} - {request.artist}")

                # Search for lyrics
                song = await self.search_lyrics_use_case.execute(request)

                if song:
                    # Publish result with original request info
                    await self.message_repository.publish_result(song, request)
                    logger.info(f"Successfully processed: {song.title}")
                else:
                    logger.warning(
                        f"No results found for: {request.title} - {request.artist}"
                    )

        except asyncio.CancelledError:
            logger.info("Service cancelled")
            raise
        except Exception as e:
            logger.error(f"Error in service: {e}", exc_info=True)
            raise
        finally:
            await self.stop()

    async def stop(self) -> None:
        """Stop the lyrics fetcher service."""
        logger.info("Stopping lyrics fetcher service...")
        self._running = False
        await self.message_repository.disconnect()
        logger.info("Service stopped")
