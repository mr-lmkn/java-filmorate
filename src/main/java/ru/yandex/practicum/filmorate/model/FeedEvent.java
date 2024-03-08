package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FeedEvent {
    private Long timestamp;
    @NotBlank
    private Integer userId;
    @NotBlank
    private FeedEventType eventType;
    @NotBlank
    private FeedEventOperation operation;
    private Integer eventId;
    @NotBlank
    private Integer entityId;
}
