"""Use case for searching song lyrics."""

from __future__ import annotations

import logging

from src.domain.entities.search_request import SearchRequest
from src.domain.entities.song import Song
from src.domain.repositories.lyrics_repository import LyricsRepository

logger = logging.getLogger(__name__)


class SearchLyricsUseCase:
    """Use case for searching song lyrics."""

    def __init__(self, lyrics_repository: LyricsRepository) -> None:
        """
        Initialize the use case.

        Args:
            lyrics_repository: Repository for fetching lyrics
        """
        self.lyrics_repository = lyrics_repository

    async def execute(self, request: SearchRequest) -> Song | None:
        """
        Execute the search lyrics use case.

        Args:
            request: Search request with title and artist

        Returns:
            Song entity if found, None otherwise
        """
        logger.info(f"Searching for song: {request.to_search_query()}")

        try:
            song = await self.lyrics_repository.search_song(
                title=request.title, artist=request.artist
            )

            if song:
                logger.info(
                    f"Found song: {song.title} by {song.artist}, "
                    f"has lyrics: {song.has_lyrics()}"
                )
            else:
                logger.warning(f"Song not found: {request.to_search_query()}")

            return song

        except Exception as e:
            logger.error(f"Error searching for song: {e}", exc_info=True)
            return None
