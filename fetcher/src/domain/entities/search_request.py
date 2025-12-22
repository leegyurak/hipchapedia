"""Search request entity."""

from __future__ import annotations

from dataclasses import dataclass


@dataclass
class SearchRequest:
    """Search request entity with title and artist."""

    title: str
    artist: str

    def __post_init__(self) -> None:
        """Validate required fields."""
        if not self.title:
            raise ValueError("Title cannot be empty")
        if not self.artist:
            raise ValueError("Artist cannot be empty")

    def to_search_query(self) -> str:
        """Convert to search query string."""
        return f"{self.title} - {self.artist}"
