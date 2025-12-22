"""Repository interface for message pub/sub."""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import AsyncIterator

from src.domain.entities.search_request import SearchRequest
from src.domain.entities.song import Song


class MessageRepository(ABC):
    """Abstract repository for message pub/sub operations."""

    @abstractmethod
    def subscribe_requests(self) -> AsyncIterator[SearchRequest]:
        """
        Subscribe to search requests.

        Yields:
            SearchRequest objects from the message queue
        """
        ...

    @abstractmethod
    async def publish_result(self, song: Song, original_request: SearchRequest | None = None) -> None:
        """
        Publish song result.

        Args:
            song: Song entity to publish
        """
        pass

    @abstractmethod
    async def connect(self) -> None:
        """Establish connection to message broker."""
        pass

    @abstractmethod
    async def disconnect(self) -> None:
        """Close connection to message broker."""
        pass
