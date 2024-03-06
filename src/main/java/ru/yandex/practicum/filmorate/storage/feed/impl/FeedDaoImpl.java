package ru.yandex.practicum.filmorate.storage.feed.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.FeedEventOperation;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
@Slf4j
public class FeedDaoImpl implements FeedStorage {
    private final JdbcTemplate dataSource;
    private final UserStorage userStorage;

    @Override
    public List<FeedEvent> getEventsByUserId(Integer id) throws NoDataFoundException {
        User user = userStorage.getUserById(id);
        List<FeedEvent> eventsList = new ArrayList<FeedEvent>();
        SqlRowSet rows = dataSource.queryForRowSet(
                " SELECT e.EVENT_ID, "
                        + " e.USER_ID, "
                        + " e.EVENT_TYPE, "
                        + " e.EVENT_OPERATION, "
                        + " e.ENTITY_ID, "
                        + " e.TIMESTAMP, "
                        + " FROM EVENTS e"
                        + " WHERE USER_ID = ? "
                        + " ORDER BY TIMESTAMP ",
                id
        );
        while (rows.next()) {
            FeedEvent event = mapEventRow(rows);
            log.info("Событие: {}", event.getEventType());
            eventsList.add(event);
        }
        return eventsList;
    }

    @Override
    public void saveEvent(FeedEvent event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("EVENTS")
                .usingGeneratedKeyColumns("EVENT_ID");
        Map<String, Object> parameters = mapEventQueryParameters(event);
        Integer id = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
        log.info("Generated event-id: " + id);
    }

    private FeedEvent mapEventRow(SqlRowSet eventRows) {
        FeedEventType eventType = FeedEventType.valueOf(eventRows.getString("EVENT_TYPE"));
        FeedEventOperation eventOperation = FeedEventOperation.valueOf(
                eventRows.getString("EVENT_OPERATION"));

        log.info("Получено событие: USER_ID = {}, EVENT_TYPE = {}, EVENT_OPERATION = {}",
                eventRows.getInt("USER_ID"),
                eventType,
                eventOperation);

        return FeedEvent.builder()
                .eventId(eventRows.getInt("EVENT_ID"))
                .userId(eventRows.getInt("USER_ID"))
                .eventType(eventType)
                .operation(eventOperation)
                .entityId(eventRows.getInt("ENTITY_ID"))
                .timestamp(eventRows.getLong("TIMESTAMP"))
                .build();
    }

    private Map<String, Object> mapEventQueryParameters(FeedEvent event) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("EVENT_ID", event.getEventId());
        parameters.put("USER_ID", event.getUserId());
        parameters.put("EVENT_TYPE", event.getEventType().toString());
        parameters.put("EVENT_OPERATION", event.getOperation().toString());
        parameters.put("ENTITY_ID", event.getEntityId());
        parameters.put("TIMESTAMP", event.getTimestamp());

        log.info("Сохраняем событие: USER_ID = {}, EVENT_TYPE = {}, EVENT_OPERATION = {}",
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString());

        return parameters;
    }

}

