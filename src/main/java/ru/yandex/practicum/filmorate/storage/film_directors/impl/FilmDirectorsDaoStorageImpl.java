package ru.yandex.practicum.filmorate.storage.film_directors.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.film_directors.FilmDirectorsStorage;

import java.util.List;

@Component
public class FilmDirectorsDaoStorageImpl implements FilmDirectorsStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDirectorsDaoStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
    }

    @Override
    public void create(int filmId, List<Integer> directorsIds) {
        if (!directorsIds.isEmpty()) {
            String sql = "merge into film_director (film_id, director_id) values (:film_id, :director_id)";
            namedParameterJdbcTemplate.batchUpdate(sql, directorsIds.stream()
                    .map(directorId -> new MapSqlParameterSource()
                            .addValue("film_id", filmId)
                            .addValue("director_id", directorId)).toArray(MapSqlParameterSource[]::new));
        }

    }

    @Override
    public void update(int filmId, List<Integer> directorsIds) {
        delete(filmId);
        create(filmId, directorsIds);
    }

    private void delete(int filmId) {
        String sql = "delete from film_director where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}
