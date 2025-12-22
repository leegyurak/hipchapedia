"""Unit tests for SearchLyricsUseCase."""

from __future__ import annotations

from unittest.mock import AsyncMock

import pytest

from src.domain.entities.search_request import SearchRequest
from src.domain.entities.song import Song
from src.use_cases.search_lyrics import SearchLyricsUseCase


@pytest.fixture
def mock_lyrics_repository() -> AsyncMock:
    """Create a mock lyrics repository."""
    return AsyncMock()


@pytest.fixture
def use_case(mock_lyrics_repository: AsyncMock) -> SearchLyricsUseCase:
    """Create a SearchLyricsUseCase instance with mock repository."""
    return SearchLyricsUseCase(lyrics_repository=mock_lyrics_repository)


class TestSearchLyricsUseCase:
    """Tests for SearchLyricsUseCase."""

    async def test_execute_returns_song_when_found(
        self, use_case: SearchLyricsUseCase, mock_lyrics_repository: AsyncMock
    ) -> None:
        """Test that execute returns song when repository finds it."""
        # Arrange
        request = SearchRequest(title="Test Song", artist="Test Artist")
        expected_song = Song(
            title="Test Song", artist="Test Artist", lyrics="Test lyrics"
        )
        mock_lyrics_repository.search_song.return_value = expected_song

        # Act
        result = await use_case.execute(request)

        # Assert
        assert result == expected_song
        mock_lyrics_repository.search_song.assert_called_once_with(
            title="Test Song", artist="Test Artist"
        )

    async def test_execute_returns_none_when_not_found(
        self, use_case: SearchLyricsUseCase, mock_lyrics_repository: AsyncMock
    ) -> None:
        """Test that execute returns None when repository doesn't find song."""
        # Arrange
        request = SearchRequest(title="Unknown Song", artist="Unknown Artist")
        mock_lyrics_repository.search_song.return_value = None

        # Act
        result = await use_case.execute(request)

        # Assert
        assert result is None
        mock_lyrics_repository.search_song.assert_called_once_with(
            title="Unknown Song", artist="Unknown Artist"
        )

    async def test_execute_returns_none_on_repository_error(
        self, use_case: SearchLyricsUseCase, mock_lyrics_repository: AsyncMock
    ) -> None:
        """Test that execute returns None when repository raises error."""
        # Arrange
        request = SearchRequest(title="Error Song", artist="Error Artist")
        mock_lyrics_repository.search_song.side_effect = Exception("API Error")

        # Act
        result = await use_case.execute(request)

        # Assert
        assert result is None
