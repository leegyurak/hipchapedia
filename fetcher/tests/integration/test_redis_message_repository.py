"""Integration tests for Redis message repository."""

from __future__ import annotations

import asyncio
import json

import pytest
import redis.asyncio as redis

from src.domain.entities.search_request import SearchRequest
from src.domain.entities.song import Song
from src.infrastructure.messaging.redis_message_repository import (
    RedisMessageRepository,
)

# Mark all tests in this module as integration tests
pytestmark = pytest.mark.integration


@pytest.fixture
async def redis_client() -> redis.Redis:
    """Create a Redis client for testing."""
    client = redis.Redis(host="localhost", port=6379, db=15, decode_responses=True)
    try:
        await client.ping()
    except redis.ConnectionError:
        pytest.skip("Redis is not available")

    yield client

    # Cleanup
    await client.flushdb()
    await client.close()


@pytest.fixture
async def repository() -> RedisMessageRepository:
    """Create a Redis message repository for testing."""
    repo = RedisMessageRepository(
        host="localhost",
        port=6379,
        db=15,
        request_channel="test:requests",
        result_channel="test:results",
    )

    try:
        await repo.connect()
    except Exception:
        pytest.skip("Redis is not available")

    yield repo

    await repo.disconnect()


class TestRedisMessageRepository:
    """Integration tests for RedisMessageRepository."""

    async def test_connect_and_disconnect(self) -> None:
        """Test connecting and disconnecting from Redis."""
        repo = RedisMessageRepository(host="localhost", port=6379, db=15)

        try:
            await repo.connect()
            assert repo.client is not None
            assert repo.pubsub is not None

            await repo.disconnect()
        except Exception:
            pytest.skip("Redis is not available")

    async def test_publish_result(
        self, repository: RedisMessageRepository, redis_client: redis.Redis
    ) -> None:
        """Test publishing a song result."""
        # Subscribe to the result channel
        pubsub = redis_client.pubsub()
        await pubsub.subscribe("test:results")

        # Skip subscription message
        async for message in pubsub.listen():
            if message["type"] == "subscribe":
                break

        # Publish a song
        song = Song(
            title="Test Song",
            artist="Test Artist",
            lyrics="Test lyrics",
            url="https://genius.com/test",
        )

        await repository.publish_result(song)

        # Receive the message
        received = False
        async for message in pubsub.listen():
            if message["type"] == "message":
                data = json.loads(message["data"])
                assert data["title"] == "Test Song"
                assert data["artist"] == "Test Artist"
                assert data["lyrics"] == "Test lyrics"
                assert data["url"] == "https://genius.com/test"
                # Should not have request fields when original_request is not provided
                assert "request_title" not in data
                assert "request_artist" not in data
                received = True
                break

        await pubsub.close()
        assert received

    async def test_publish_result_with_original_request(
        self, repository: RedisMessageRepository, redis_client: redis.Redis
    ) -> None:
        """Test publishing a song result with original request info."""
        # Subscribe to the result channel
        pubsub = redis_client.pubsub()
        await pubsub.subscribe("test:results")

        # Skip subscription message
        async for message in pubsub.listen():
            if message["type"] == "subscribe":
                break

        # Create original request and song with different values
        original_request = SearchRequest(
            title="original title",
            artist="original artist",
        )

        song = Song(
            title="Actual Song Title",
            artist="Actual Artist Name",
            lyrics="Test lyrics",
            url="https://genius.com/test",
        )

        await repository.publish_result(song, original_request)

        # Receive the message
        received = False
        async for message in pubsub.listen():
            if message["type"] == "message":
                data = json.loads(message["data"])
                # Song data should be from Genius API
                assert data["title"] == "Actual Song Title"
                assert data["artist"] == "Actual Artist Name"
                assert data["lyrics"] == "Test lyrics"
                assert data["url"] == "https://genius.com/test"
                # Request info should be from original request
                assert data["request_title"] == "original title"
                assert data["request_artist"] == "original artist"
                received = True
                break

        await pubsub.close()
        assert received

    async def test_subscribe_requests(
        self, repository: RedisMessageRepository, redis_client: redis.Redis
    ) -> None:
        """Test subscribing to search requests."""

        # Create a task to listen for requests
        async def listen_for_requests() -> SearchRequest | None:
            async for request in repository.subscribe_requests():
                return request
            return None

        listen_task = asyncio.create_task(listen_for_requests())

        # Give the subscription time to be ready
        await asyncio.sleep(0.1)

        # Publish a request
        request_data = json.dumps({"title": "Test Song", "artist": "Test Artist"})
        await redis_client.publish("test:requests", request_data)

        # Wait for the request with timeout
        try:
            request = await asyncio.wait_for(listen_task, timeout=2.0)
            assert request is not None
            assert request.title == "Test Song"
            assert request.artist == "Test Artist"
        except asyncio.TimeoutError:
            pytest.fail("Did not receive request in time")

    async def test_subscribe_requests_invalid_json(
        self, repository: RedisMessageRepository, redis_client: redis.Redis
    ) -> None:
        """Test that invalid JSON messages are skipped."""

        received_requests: list[SearchRequest] = []

        async def listen_for_requests() -> None:
            count = 0
            async for request in repository.subscribe_requests():
                received_requests.append(request)
                count += 1
                if count >= 1:
                    break

        listen_task = asyncio.create_task(listen_for_requests())

        # Give the subscription time to be ready
        await asyncio.sleep(0.1)

        # Publish invalid JSON
        await redis_client.publish("test:requests", "invalid json")

        # Publish a valid request
        valid_request = json.dumps({"title": "Valid Song", "artist": "Valid Artist"})
        await redis_client.publish("test:requests", valid_request)

        # Wait for the task with timeout
        try:
            await asyncio.wait_for(listen_task, timeout=2.0)
            assert len(received_requests) == 1
            assert received_requests[0].title == "Valid Song"
        except asyncio.TimeoutError:
            pytest.fail("Did not receive valid request in time")
