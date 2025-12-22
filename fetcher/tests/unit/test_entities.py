"""Unit tests for domain entities."""

from __future__ import annotations

import pytest

from src.domain.entities.search_request import SearchRequest
from src.domain.entities.song import Song


class TestSong:
    """Tests for Song entity."""

    def test_create_song_with_required_fields(self) -> None:
        """Test creating a song with only required fields."""
        song = Song(title="Test Song", artist="Test Artist")

        assert song.title == "Test Song"
        assert song.artist == "Test Artist"
        assert song.lyrics is None
        assert song.url is None
        assert song.album is None
        assert song.release_date is None

    def test_create_song_with_all_fields(self) -> None:
        """Test creating a song with all fields."""
        song = Song(
            title="Test Song",
            artist="Test Artist",
            lyrics="Test lyrics",
            url="https://genius.com/test",
            album="Test Album",
            release_date="2024-01-01",
        )

        assert song.title == "Test Song"
        assert song.artist == "Test Artist"
        assert song.lyrics == "Test lyrics"
        assert song.url == "https://genius.com/test"
        assert song.album == "Test Album"
        assert song.release_date == "2024-01-01"

    def test_song_with_empty_title_raises_error(self) -> None:
        """Test that creating a song with empty title raises ValueError."""
        with pytest.raises(ValueError, match="Title cannot be empty"):
            Song(title="", artist="Test Artist")

    def test_song_with_empty_artist_raises_error(self) -> None:
        """Test that creating a song with empty artist raises ValueError."""
        with pytest.raises(ValueError, match="Artist cannot be empty"):
            Song(title="Test Song", artist="")

    def test_has_lyrics_returns_true_when_lyrics_exist(self) -> None:
        """Test has_lyrics returns True when lyrics are present."""
        song = Song(title="Test Song", artist="Test Artist", lyrics="Test lyrics")

        assert song.has_lyrics() is True

    def test_has_lyrics_returns_false_when_lyrics_none(self) -> None:
        """Test has_lyrics returns False when lyrics are None."""
        song = Song(title="Test Song", artist="Test Artist", lyrics=None)

        assert song.has_lyrics() is False

    def test_has_lyrics_returns_false_when_lyrics_empty(self) -> None:
        """Test has_lyrics returns False when lyrics are empty string."""
        song = Song(title="Test Song", artist="Test Artist", lyrics="   ")

        assert song.has_lyrics() is False


class TestSearchRequest:
    """Tests for SearchRequest entity."""

    def test_create_search_request(self) -> None:
        """Test creating a search request."""
        request = SearchRequest(title="Test Song", artist="Test Artist")

        assert request.title == "Test Song"
        assert request.artist == "Test Artist"

    def test_search_request_with_empty_title_raises_error(self) -> None:
        """Test that creating a request with empty title raises ValueError."""
        with pytest.raises(ValueError, match="Title cannot be empty"):
            SearchRequest(title="", artist="Test Artist")

    def test_search_request_with_empty_artist_raises_error(self) -> None:
        """Test that creating a request with empty artist raises ValueError."""
        with pytest.raises(ValueError, match="Artist cannot be empty"):
            SearchRequest(title="Test Song", artist="")

    def test_to_search_query(self) -> None:
        """Test converting search request to query string."""
        request = SearchRequest(title="Test Song", artist="Test Artist")

        assert request.to_search_query() == "Test Song - Test Artist"
