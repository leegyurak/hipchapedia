"""Unit tests for LyricsFetcherService."""

from __future__ import annotations

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
        original_request = SearchRequest(title="original title", artist="original artist")

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
