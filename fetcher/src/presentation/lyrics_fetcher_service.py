"""Lyrics fetcher service that processes Redis messages."""

from __future__ import annotations

import asyncio
import logging
from typing import Any

from src.domain.entities.search_request import SearchRequest
from src.domain.repositories.message_repository import MessageRepository
from src.use_cases.search_lyrics import SearchLyricsUseCase

logger = logging.getLogger(__name__)


class LyricsFetcherService:
    """Service that listens to Redis requests and publishes results."""

    def __init__(
        self,
        message_repository: MessageRepository,
        search_lyrics_use_case: SearchLyricsUseCase,
        max_concurrent_tasks: int = 10,
    ) -> None:
        """
        Initialize the fetcher service.

        Args:
            message_repository: Repository for pub/sub operations
            search_lyrics_use_case: Use case for searching lyrics
            max_concurrent_tasks: Maximum number of concurrent tasks to process
        """
        self.message_repository = message_repository
        self.search_lyrics_use_case = search_lyrics_use_case
        self._running = False
        self._max_concurrent_tasks = max_concurrent_tasks
        self._semaphore: asyncio.Semaphore | None = None
        self._tasks: set[asyncio.Task[Any]] = set()

    async def _process_request(self, request: SearchRequest) -> None:
        """
        Process a single request asynchronously.

        Args:
            request: Search request to process
        """
        if self._semaphore is None:
            raise RuntimeError("Semaphore not initialized")

        async with self._semaphore:
            try:
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

            except Exception as e:
                logger.error(
                    f"Error processing request {request.title} - {request.artist}: {e}",
                    exc_info=True,
                )

    async def start(self) -> None:
        """Start the lyrics fetcher service."""
        logger.info("Starting lyrics fetcher service...")

        try:
            await self.message_repository.connect()
            self._running = True
            self._semaphore = asyncio.Semaphore(self._max_concurrent_tasks)

            logger.info(
                f"Service started. Waiting for requests (max {self._max_concurrent_tasks} concurrent tasks)..."
            )

            async for request in self.message_repository.subscribe_requests():
                if not self._running:
                    break

                # Create task for concurrent processing
                task = asyncio.create_task(self._process_request(request))
                self._tasks.add(task)
                task.add_done_callback(self._tasks.discard)

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

        # Wait for all running tasks to complete
        if self._tasks:
            logger.info(f"Waiting for {len(self._tasks)} tasks to complete...")
            await asyncio.gather(*self._tasks, return_exceptions=True)

        await self.message_repository.disconnect()
        logger.info("Service stopped")
