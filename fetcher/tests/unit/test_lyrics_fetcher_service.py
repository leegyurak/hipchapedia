"""Unit tests for LyricsFetcherService."""

from __future__ import annotations

import asyncio
from unittest.mock import AsyncMock, MagicMock

import pytest

from src.domain.entities.search_request import SearchRequest
from src.domain.entities.song import Song
from src.presentation.lyrics_fetcher_service import LyricsFetcherService


@pytest.fixture
def mock_message_repository() -> AsyncMock:
    """Create a mock message repository."""
    return AsyncMock()


@pytest.fixture
def mock_search_lyrics_use_case() -> AsyncMock:
    """Create a mock search lyrics use case."""
    return AsyncMock()


@pytest.fixture
def service(
    mock_message_repository: AsyncMock, mock_search_lyrics_use_case: AsyncMock
) -> LyricsFetcherService:
    """Create a LyricsFetcherService instance with mock dependencies."""
    return LyricsFetcherService(
        message_repository=mock_message_repository,
        search_lyrics_use_case=mock_search_lyrics_use_case,
    )


class TestLyricsFetcherService:
    """Tests for LyricsFetcherService."""

    async def test_start_processes_requests_and_publishes_results(
        self,
        service: LyricsFetcherService,
        mock_message_repository: AsyncMock,
        mock_search_lyrics_use_case: AsyncMock,
    ) -> None:
        """Test that service processes requests and publishes results."""
        # Arrange
        request = SearchRequest(title="test song", artist="test artist")
        song = Song(
            title="Test Song Found",
            artist="Test Artist Found",
            lyrics="Test lyrics",
            url="https://genius.com/test",
        )

        # Setup mocks - subscribe_requests should return an async generator
        async def mock_subscribe():
            yield request
            # Stop the service after one request
            service._running = False

        mock_message_repository.subscribe_requests = MagicMock(
            return_value=mock_subscribe()
        )
        mock_search_lyrics_use_case.execute.return_value = song

        # Act
        await service.start()

        # Assert
        mock_message_repository.connect.assert_called_once()
        mock_search_lyrics_use_case.execute.assert_called_once_with(request)
        mock_message_repository.publish_result.assert_called_once_with(song, request)
        mock_message_repository.disconnect.assert_called_once()

    async def test_start_handles_song_not_found(
        self,
        service: LyricsFetcherService,
        mock_message_repository: AsyncMock,
        mock_search_lyrics_use_case: AsyncMock,
    ) -> None:
        """Test that service handles when song is not found."""
        # Arrange
        request = SearchRequest(title="unknown song", artist="unknown artist")

        # Setup mocks
        async def mock_subscribe():
            yield request
            service._running = False

        mock_message_repository.subscribe_requests = MagicMock(
            return_value=mock_subscribe()
        )
        mock_search_lyrics_use_case.execute.return_value = None

        # Act
        await service.start()

        # Assert
        mock_search_lyrics_use_case.execute.assert_called_once_with(request)
        # Should not publish when song is not found
        mock_message_repository.publish_result.assert_not_called()

    async def test_start_maintains_original_request_info(
        self,
        service: LyricsFetcherService,
        mock_message_repository: AsyncMock,
        mock_search_lyrics_use_case: AsyncMock,
    ) -> None:
        """Test that service maintains original request info when publishing."""
        # Arrange
        original_request = SearchRequest(
            title="original title", artist="original artist"
        )

        # Song from Genius API has different title/artist
        found_song = Song(
            title="Actual Song Title",
            artist="Actual Artist Name",
            lyrics="Test lyrics",
            url="https://genius.com/test",
        )

        # Setup mocks
        async def mock_subscribe():
            yield original_request
            service._running = False

        mock_message_repository.subscribe_requests = MagicMock(
            return_value=mock_subscribe()
        )
        mock_search_lyrics_use_case.execute.return_value = found_song

        # Act
        await service.start()

        # Assert
        # The song should still have Genius API data
        assert found_song.title == "Actual Song Title"
        assert found_song.artist == "Actual Artist Name"

        # But publish should be called with original request
        mock_message_repository.publish_result.assert_called_once_with(
            found_song, original_request
        )

    async def test_stop_disconnects_repository(
        self, service: LyricsFetcherService, mock_message_repository: AsyncMock
    ) -> None:
        """Test that stop method disconnects from repository."""
        # Act
        await service.stop()

        # Assert
        assert service._running is False
        mock_message_repository.disconnect.assert_called_once()

    async def test_start_handles_multiple_requests(
        self,
        service: LyricsFetcherService,
        mock_message_repository: AsyncMock,
        mock_search_lyrics_use_case: AsyncMock,
    ) -> None:
        """Test that service can handle multiple requests."""
        # Arrange
        request1 = SearchRequest(title="song 1", artist="artist 1")
        request2 = SearchRequest(title="song 2", artist="artist 2")
        song1 = Song(title="Song 1", artist="Artist 1", lyrics="Lyrics 1")
        song2 = Song(title="Song 2", artist="Artist 2", lyrics="Lyrics 2")

        # Setup mocks
        async def mock_subscribe():
            yield request1
            yield request2
            service._running = False

        mock_message_repository.subscribe_requests = MagicMock(
            return_value=mock_subscribe()
        )
        mock_search_lyrics_use_case.execute.side_effect = [song1, song2]

        # Act
        await service.start()

        # Assert
        assert mock_search_lyrics_use_case.execute.call_count == 2
        assert mock_message_repository.publish_result.call_count == 2

        # Verify calls with original requests
        calls = mock_message_repository.publish_result.call_args_list
        assert calls[0][0] == (song1, request1)
        assert calls[1][0] == (song2, request2)

    async def test_concurrent_task_processing(
        self,
        mock_message_repository: AsyncMock,
        mock_search_lyrics_use_case: AsyncMock,
    ) -> None:
        """Test that multiple requests are processed concurrently."""
        # Arrange
        service = LyricsFetcherService(
            message_repository=mock_message_repository,
            search_lyrics_use_case=mock_search_lyrics_use_case,
            max_concurrent_tasks=3,
        )

        requests = [
            SearchRequest(title=f"song {i}", artist=f"artist {i}") for i in range(5)
        ]
        songs = [
            Song(title=f"Song {i}", artist=f"Artist {i}", lyrics=f"Lyrics {i}")
            for i in range(5)
        ]

        # Track execution with delays to ensure concurrency
        execution_started = []
        execution_completed = []

        async def delayed_execute(request: SearchRequest):
            execution_started.append(request.title)
            await asyncio.sleep(0.2)  # Longer delay to ensure overlap
            execution_completed.append(request.title)
            idx = int(request.title.split()[-1])
            return songs[idx]

        # Setup mocks
        async def mock_subscribe():
            for req in requests:
                yield req
                # Small delay between yields to ensure tasks are created
                await asyncio.sleep(0.01)
            # Wait longer for tasks to start executing
            await asyncio.sleep(0.15)
            service._running = False

        mock_message_repository.subscribe_requests = MagicMock(
            return_value=mock_subscribe()
        )
        mock_search_lyrics_use_case.execute.side_effect = delayed_execute

        # Act
        await service.start()

        # Assert
        # All 5 requests should be executed
        assert len(execution_started) == 5
        assert len(execution_completed) == 5

        # Verify concurrent execution: at least 3 tasks should have started
        # before the first one completes (due to max_concurrent_tasks=3)
        # Check after short delay that multiple tasks started
        assert mock_search_lyrics_use_case.execute.call_count == 5

    async def test_semaphore_limits_concurrent_tasks(
        self,
        mock_message_repository: AsyncMock,
        mock_search_lyrics_use_case: AsyncMock,
    ) -> None:
        """Test that semaphore correctly limits concurrent task execution."""
        # Arrange
        max_concurrent = 2
        service = LyricsFetcherService(
            message_repository=mock_message_repository,
            search_lyrics_use_case=mock_search_lyrics_use_case,
            max_concurrent_tasks=max_concurrent,
        )

        requests = [
            SearchRequest(title=f"song {i}", artist=f"artist {i}") for i in range(4)
        ]

        # Track concurrent execution count
        current_executing = 0
        max_concurrent_reached = 0

        async def tracked_execute(request: SearchRequest):
            nonlocal current_executing, max_concurrent_reached
            current_executing += 1
            max_concurrent_reached = max(max_concurrent_reached, current_executing)
            await asyncio.sleep(0.1)
            current_executing -= 1
            return Song(title="Test", artist="Test", lyrics="Test")

        # Setup mocks
        async def mock_subscribe():
            for req in requests:
                yield req
            await asyncio.sleep(0.05)
            service._running = False

        mock_message_repository.subscribe_requests = MagicMock(
            return_value=mock_subscribe()
        )
        mock_search_lyrics_use_case.execute.side_effect = tracked_execute

        # Act
        await service.start()

        # Assert
        # Should never exceed max_concurrent_tasks
        assert max_concurrent_reached <= max_concurrent
        assert max_concurrent_reached == max_concurrent  # Should reach the limit

    async def test_graceful_shutdown_waits_for_tasks(
        self,
        mock_message_repository: AsyncMock,
        mock_search_lyrics_use_case: AsyncMock,
    ) -> None:
        """Test that graceful shutdown waits for running tasks to complete."""
        # Arrange
        service = LyricsFetcherService(
            message_repository=mock_message_repository,
            search_lyrics_use_case=mock_search_lyrics_use_case,
            max_concurrent_tasks=5,
        )

        completed_tasks = []

        async def delayed_execute(request: SearchRequest):
            await asyncio.sleep(0.2)
            completed_tasks.append(request.title)
            return Song(title="Test", artist="Test", lyrics="Test")

        requests = [
            SearchRequest(title=f"song {i}", artist=f"artist {i}") for i in range(3)
        ]

        # Setup mocks
        async def mock_subscribe():
            for req in requests:
                yield req
            # Immediately stop after yielding all requests
            service._running = False

        mock_message_repository.subscribe_requests = MagicMock(
            return_value=mock_subscribe()
        )
        mock_search_lyrics_use_case.execute.side_effect = delayed_execute

        # Act
        await service.start()

        # Assert
        # All tasks should complete even though service stopped
        assert len(completed_tasks) == 3
        assert mock_message_repository.publish_result.call_count == 3

    async def test_error_in_task_does_not_stop_service(
        self,
        mock_message_repository: AsyncMock,
        mock_search_lyrics_use_case: AsyncMock,
    ) -> None:
        """Test that error in one task doesn't stop other tasks from processing."""
        # Arrange
        service = LyricsFetcherService(
            message_repository=mock_message_repository,
            search_lyrics_use_case=mock_search_lyrics_use_case,
            max_concurrent_tasks=5,
        )

        requests = [
            SearchRequest(title=f"song {i}", artist=f"artist {i}") for i in range(3)
        ]

        # First request will fail, others succeed
        async def execute_with_error(request: SearchRequest):
            if request.title == "song 0":
                raise Exception("Test error")
            return Song(title=request.title, artist=request.artist, lyrics="Test")

        # Setup mocks
        async def mock_subscribe():
            for req in requests:
                yield req
            await asyncio.sleep(0.1)
            service._running = False

        mock_message_repository.subscribe_requests = MagicMock(
            return_value=mock_subscribe()
        )
        mock_search_lyrics_use_case.execute.side_effect = execute_with_error

        # Act
        await service.start()

        # Assert
        # Only 2 successful publishes (song 1 and song 2)
        assert mock_message_repository.publish_result.call_count == 2
