"""Repository interface for fetching lyrics."""

from __future__ import annotations

from abc import ABC, abstractmethod

from src.domain.entities.song import Song


class LyricsRepository(ABC):
    """Abstract repository for fetching song lyrics."""

    @abstractmethod
    async def search_song(self, title: str, artist: str) -> Song | None:
        """
        Search for a song by title and artist.

        Args:
            title: Song title
            artist: Artist name

        Returns:
            Song entity if found, None otherwise
        """
        pass
