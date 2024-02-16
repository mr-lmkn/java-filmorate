package ru.yandex.practicum.filmorate.storage.mpa.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
@Slf4j
public class MpaDaoStorageImpl implements MpaStorage {
    private final JdbcTemplate dataSource;

    @Override
    public Mpa getMpaById(Integer id) throws NoDataFoundException {
        log.info("Извлечение рейтинга {}", id);
        SqlRowSet mpaRows = dataSource.queryForRowSet("SELECT * FROM RATING WHERE RATING_ID = ?", id);
        if (mpaRows.next()) {
            Mpa mpa = mapMpaRow(mpaRows);
            log.info("Рейтинг: {} {}", mpa.getId(), mpa.getRatingCode());
            return mpa;
        } else {
            String msg = String.format("Нет рейтинг с 'id'=%s.", id);
            log.info(msg);
            throw new NoDataFoundException(msg);
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        log.info("Извлечение всех рейтиногов");
        ArrayList<Mpa> mpaList = new ArrayList<Mpa>();
        SqlRowSet mpaRows = dataSource.queryForRowSet("SELECT * FROM RATING");
        while (mpaRows.next()) {
            Mpa mpa = mapMpaRow(mpaRows);
            mpaList.add(mpa);
            log.info("Найден рейтинг: {} {}", mpa.getId(), mpa.getRatingCode());
        }
        log.info("Конец списка рейтингов");
        return mpaList;
    }

    private Mpa mapMpaRow(SqlRowSet mpaRows) {
        Mpa mpa = new Mpa(
                mpaRows.getInt("RATING_ID"),
                mpaRows.getString("RATING_CODE"),
                mpaRows.getString("RATING_NAME"),
                mpaRows.getString("RATING_DESCRIPTION")
        );
        return mpa;
    }

}
