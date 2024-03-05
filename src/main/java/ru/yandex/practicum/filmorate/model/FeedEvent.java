package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FeedEvent {
    private Long timestamp;
    private Integer userId;
    private FeedEventType eventType;
    private FeedEventOperation operation;
    private Integer eventId;
    private Integer entityId;
}
