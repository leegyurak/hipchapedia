"""Redis implementation of message repository."""

from __future__ import annotations

import json
import logging
from collections.abc import AsyncIterator

import redis.asyncio as redis

from src.domain.entities.search_request import SearchRequest
from src.domain.entities.song import Song
from src.domain.repositories.message_repository import MessageRepository

logger = logging.getLogger(__name__)


class RedisMessageRepository(MessageRepository):
    """Redis pub/sub implementation for message operations."""

    def __init__(
        self,
        host: str = "localhost",
        port: int = 6379,
        db: int = 0,
        password: str | None = None,
        request_channel: str = "lyrics:requests",
        result_channel: str = "lyrics:results",
    ) -> None:
        """
        Initialize Redis connection parameters.

        Args:
            host: Redis host
            port: Redis port
            db: Redis database number
            password: Redis password (optional)
            request_channel: Channel for incoming search requests
            result_channel: Channel for publishing results
        """
        self.host = host
        self.port = port
        self.db = db
        self.password = password
        self.request_channel = request_channel
        self.result_channel = result_channel
        self.client: redis.Redis | None = None
        self.pubsub: redis.client.PubSub | None = None

    async def connect(self) -> None:
        """Establish connection to Redis."""
        try:
            self.client = redis.Redis(
                host=self.host,
                port=self.port,
                db=self.db,
                password=self.password,
                decode_responses=True,
            )
            ping_result = self.client.ping()
            if hasattr(ping_result, "__await__"):
                await ping_result
            logger.info(f"Connected to Redis at {self.host}:{self.port}")

            self.pubsub = self.client.pubsub()
            await self.pubsub.subscribe(self.request_channel)
            logger.info(f"Subscribed to channel: {self.request_channel}")

        except Exception as e:
            logger.error(f"Failed to connect to Redis: {e}", exc_info=True)
            raise

    async def disconnect(self) -> None:
        """Close connection to Redis."""
        try:
            if self.pubsub:
                await self.pubsub.unsubscribe(self.request_channel)
                await self.pubsub.close()
                logger.info("Unsubscribed and closed pubsub")

            if self.client:
                await self.client.close()
                logger.info("Closed Redis connection")

        except Exception as e:
            logger.error(f"Error during disconnect: {e}", exc_info=True)

    def subscribe_requests(self) -> AsyncIterator[SearchRequest]:
        """
        Subscribe to search requests from Redis pub/sub.

        Yields:
            SearchRequest objects parsed from Redis messages
        """
        return self._subscribe_requests_impl()

    async def _subscribe_requests_impl(self) -> AsyncIterator[SearchRequest]:
        """Internal implementation of subscribe_requests."""
        if not self.pubsub:
            raise RuntimeError("Not connected to Redis. Call connect() first.")

        logger.info("Started listening for search requests")

        try:
            async for message in self.pubsub.listen():
                if message["type"] == "message":
                    try:
                        data = json.loads(message["data"])
                        request = SearchRequest(
                            title=data["title"], artist=data["artist"]
                        )
                        logger.debug(f"Received request: {request}")
                        yield request

                    except (json.JSONDecodeError, KeyError, ValueError) as e:
                        logger.error(
                            f"Invalid message format: {message['data']}, error: {e}"
                        )
                        continue

        except Exception as e:
            logger.error(f"Error in subscribe_requests: {e}", exc_info=True)
            raise

    async def publish_result(
        self, song: Song, original_request: SearchRequest | None = None
    ) -> None:
        """
        Publish song result to Redis.

        Args:
            song: Song entity to publish
            original_request: Original search request to maintain key consistency
        """
        if not self.client:
            raise RuntimeError("Not connected to Redis. Call connect() first.")

        try:
            result = {
                "title": song.title,
                "artist": song.artist,
                "lyrics": song.lyrics,
                "url": song.url,
                "album": song.album,
                "release_date": song.release_date,
            }

            # Add original request info for key matching
            if original_request:
                result["request_title"] = original_request.title
                result["request_artist"] = original_request.artist

            message = json.dumps(result, ensure_ascii=False)
            await self.client.publish(self.result_channel, message)

            if original_request:
                logger.info(
                    f"Published result for request: {original_request.title} by {original_request.artist} "
                    f"(found: {song.title} by {song.artist})"
                )
            else:
                logger.info(f"Published result for: {song.title} by {song.artist}")

        except Exception as e:
            logger.error(f"Error publishing result: {e}", exc_info=True)
            raise
