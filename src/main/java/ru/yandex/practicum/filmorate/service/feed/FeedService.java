package ru.yandex.practicum.filmorate.service.feed;

import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.FeedEventOperation;
import ru.yandex.practicum.filmorate.model.FeedEventType;

import java.util.List;

public interface FeedService {
    List<FeedEvent> getEventsByUserId(Integer id) throws NoDataFoundException;

    void saveEvent(Integer userId,
                   FeedEventType eventType,
                   FeedEventOperation eventOperation,
                   Integer entityId
    );
}
