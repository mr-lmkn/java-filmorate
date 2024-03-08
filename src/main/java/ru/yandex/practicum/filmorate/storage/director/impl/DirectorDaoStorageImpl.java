package ru.yandex.practicum.filmorate.storage.director.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class DirectorDaoStorageImpl implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDaoStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String sql = "select * from director";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director find(Integer id) throws NoDataFoundException {
        String sql = "select * from director where director_id = ?";
        try {
            Director director = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeDirector(rs), id);
            log.debug("Режиссер {} найден", director);
            return director;
        } catch (DataAccessException e) {
            log.debug("Режиссер с id {} не найден", id);
            throw new NoDataFoundException("Режиссер с id " + id + " не найден");
        }
    }

    @Override
    public int add(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");
        return simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue();
    }

    @Override
    public void update(Director director) throws NoDataFoundException {
        String sql = "update director set director_name = ? where director_id = ?";
        int updatedQuantity = jdbcTemplate.update(sql,
                director.getName(),
                director.getId());
        if (updatedQuantity == 0) {
            log.debug("Не найден фильм с id {}", director.getId());
            throw new NoDataFoundException("Не найден режиссер с id " + director.getId());
        }
    }

    @Override
    public void delete(Integer id) throws NoDataFoundException {
        String sql = "delete from director where director_id = ?";
        int deletedQuantity = jdbcTemplate.update(sql, id);
        if (deletedQuantity == 0) {
            log.debug("Не найден режиссер с id {}", id);
            throw new NoDataFoundException("Не найден режиссер с id " + id);
        } else {
            log.debug("Удален режиссер с id {}", id);
        }

    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        String name = rs.getString("director_name");
        int id = rs.getInt("director_id");
        return Director.builder()
                .name(name)
                .id(id)
                .build();
    }

}
