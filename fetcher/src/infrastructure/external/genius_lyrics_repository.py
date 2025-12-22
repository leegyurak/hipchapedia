"""Genius API implementation of lyrics repository."""

from __future__ import annotations

import logging

import lyricsgenius

from src.domain.entities.song import Song
from src.domain.repositories.lyrics_repository import LyricsRepository

logger = logging.getLogger(__name__)


class GeniusLyricsRepository(LyricsRepository):
    """Genius API implementation for fetching song lyrics."""

    def __init__(self, api_token: str) -> None:
        """
        Initialize Genius API client.

        Args:
            api_token: Genius API access token
        """
        self.genius = lyricsgenius.Genius(api_token)
        # Configure genius client
        self.genius.verbose = False
        self.genius.remove_section_headers = True

    async def search_song(self, title: str, artist: str) -> Song | None:
        """
        Search for a song by title and artist using Genius API.

        Args:
            title: Song title
            artist: Artist name

        Returns:
            Song entity if found, None otherwise
        """
        try:
            query = f"{artist} - {title}"
            logger.debug(f"Searching Genius for: {query}")

            # lyricsgenius is synchronous, but we wrap it in async interface
            result = self.genius.search_songs(query)

            if not result or "hits" not in result or len(result["hits"]) == 0:
                logger.info(f"No results found for: {query}")
                return None

            # Get the first hit's result
            hit_result = result["hits"][0]["result"]

            # Extract basic info from search result
            song_id = hit_result["id"]
            song_title = hit_result["title"]
            song_url = hit_result["url"]

            # Get artist name from primary_artist
            artist_name = hit_result.get("primary_artist", {}).get("name", artist)

            # Get release date
            release_date = hit_result.get("release_date_for_display")

            # Fetch lyrics using the song ID
            lyrics = None
            try:
                song_details = self.genius.search_song(song_id=song_id)
                if song_details:
                    lyrics = song_details.lyrics
            except Exception as e:
                logger.warning(f"Could not fetch lyrics for song ID {song_id}: {e}")

            return Song(
                title=song_title,
                artist=artist_name,
                lyrics=lyrics,
                url=song_url,
                album=None,  # Album info not directly available in search results
                release_date=release_date,
            )

        except Exception as e:
            logger.error(f"Error fetching from Genius API: {e}", exc_info=True)
            return None
