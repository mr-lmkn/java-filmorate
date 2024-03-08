package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.List;

public interface FeedStorage {
    List<FeedEvent> getEventsByUserId(Integer id) throws NoDataFoundException;

    void saveEvent(FeedEvent event);
}
