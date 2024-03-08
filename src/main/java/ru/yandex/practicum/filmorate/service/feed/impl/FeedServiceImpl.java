package ru.yandex.practicum.filmorate.service.feed.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.FeedEventOperation;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.service.feed.FeedService;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedStorage feedStorage;

    @Override
    public List<FeedEvent> getEventsByUserId(Integer id) throws NoDataFoundException {
        return feedStorage.getEventsByUserId(id);
    }

    @Override
    public void saveEvent(Integer userId,
                          FeedEventType eventType,
                          FeedEventOperation eventOperation,
                          Integer entityId
    ) {
        log.info("Сохраняем евент пользователя {} {} {} {}", userId, eventType, eventOperation, entityId);
        FeedEvent event = FeedEvent.builder()
                .userId(userId)
                .eventType(eventType)
                .operation(eventOperation)
                .entityId(entityId)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        feedStorage.saveEvent(event);
    }

}
