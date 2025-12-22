"""Song entity representing a song with lyrics."""

from __future__ import annotations

from dataclasses import dataclass


@dataclass
class Song:
    """Song entity with title, artist, and lyrics information."""

    title: str
    artist: str
    lyrics: str | None = None
    url: str | None = None
    album: str | None = None
    release_date: str | None = None

    def __post_init__(self) -> None:
        """Validate required fields."""
        if not self.title:
            raise ValueError("Title cannot be empty")
        if not self.artist:
            raise ValueError("Artist cannot be empty")

    def has_lyrics(self) -> bool:
        """Check if the song has lyrics."""
        return self.lyrics is not None and len(self.lyrics.strip()) > 0
