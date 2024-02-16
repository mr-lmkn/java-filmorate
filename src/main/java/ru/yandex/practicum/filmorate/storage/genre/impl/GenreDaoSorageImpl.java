package ru.yandex.practicum.filmorate.storage.genre.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Slf4j
public class GenreDaoSorageImpl implements GenreStorage {
    private final JdbcTemplate dataSource;

    @Override
    public List<Genre> getAllGenre() {
        log.info("Извлечение всех жанров");
        ArrayList<Genre> genreList = new ArrayList<Genre>();
        SqlRowSet genreRows = dataSource.queryForRowSet("SELECT * FROM GENRE ORDER BY GENRE_ID");
        while (genreRows.next()) {
            Genre genre = mapGenreRow(genreRows);
            genreList.add(genre);
            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());
        }
        log.info("Конец списка фильмов");
        return genreList;
    }

    @Override
    public Genre getGenreById(Integer id) throws NoDataFoundException {
        log.info("Извлечение жанра {}", id);
        SqlRowSet genreRows = dataSource.queryForRowSet(
                "SELECT GENRE_ID, GENRE_NAME FROM GENRE WHERE GENRE_ID = ?",
                id
        );
        if (genreRows.next()) {
            Genre genre = mapGenreRow(genreRows);
            log.info("Рейтинг: {} {}", genre.getId(), genre.getName());
            return genre;
        } else {
            String msg = String.format("Нет жанра с 'id'=%s.", id);
            log.info(msg);
            throw new NoDataFoundException(msg);
        }
    }

    @Override
    public Set<Genre> getGenresByFilmId(Integer filmId) {
        log.info("Извлечение жанра фильма {}", filmId);
        LinkedHashSet<Genre> outSet = new LinkedHashSet<>();
        SqlRowSet genreRows = dataSource.queryForRowSet(
                "SELECT g.GENRE_ID, g.GENRE_NAME "
                        + " FROM GENRE g JOIN FILM_GENRES fg ON g.GENRE_ID = fg.GENRE_ID"
                        + " WHERE fg.FILM_ID = ?",
                filmId
        );

        while (genreRows.next()) {
            Genre genre = mapGenreRow(genreRows);
            outSet.add(genre);
            log.info("Рейтинг фильма {}: {} {}", filmId, genre.getId(), genre.getName());

        }

        return outSet;
    }

    @Override
    public Set<Genre> linkGenresToFilmId(Integer filmId, Set<Genre> setGenre) throws NoDataFoundException {
        log.info("Привязка списка жанров");
        LinkedHashSet<Genre> outSet = new LinkedHashSet<>();
        List<Integer> genreIds = setGenre.stream()
                .map(Genre::getId)
                .collect(Collectors.toList());

        log.info("удаляем все");
        String deleteQery = "DELETE FROM FILM_GENRES g WHERE g.FILM_ID = ? ";
        dataSource.update(deleteQery, filmId);

        if (!genreIds.isEmpty()) {
            log.info("Добавляем");
            for (Genre genre : setGenre) {
                outSet.add(addGenreToFilmId(filmId, genre));
            }
            /*log.info("удаляем ненужные");
            // String deleteQery = //String.format(
            //     "DELETE FROM FILM_GENRES g WHERE g.FILM_ID = ? AND g.GENRE_ID NOT IN (?)" ;
            //  , String.join(",", Collections.nCopies(setGenre.size(), "?"))
            //);
            //dataSource.update(deleteQery, filmId, genreIds.toArray());

            MapSqlParameterSource parameters = new MapSqlParameterSource();
           // parameters.addValue("filmId", filmId);
            parameters.addValue("genreIds", genreIds.get(0));

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("genreIds", genreIds);

Странно тут с параметрами. Не работает маппинг. Идея была - удалить все лишние после того, как успешно завершится добавление.

            dataSource.update("DELETE FROM FILM_GENRES g WHERE g.FILM_ID = "+filmId+" AND g.GENRE_ID NOT IN (:genreIds)",
                    paramMap); */
        }
        return outSet;
    }

    @Override
    public Genre addGenreToFilmId(Integer filmId, Genre genre) throws NoDataFoundException {
        log.info("Привязываем жанр {} к фильму {}", filmId, genre);
        String insQuery = "MERGE INTO FILM_GENRES g "
                + " USING VALUES (?, ?) as u(FILM_ID, GENRE_ID)"
                + " ON u.FILM_ID = g.FILM_ID AND u.GENRE_ID = g.GENRE_ID "
                + " WHEN NOT MATCHED THEN "
                + " INSERT  (FILM_ID,GENRE_ID) VALUES (?,?)";
        String msg;
        Genre outGenre = genre;

        try {
            dataSource.update(insQuery, filmId, genre.getId(), filmId, genre.getId());
            outGenre = getGenreById(genre.getId());
            log.info("установлен жанр фильма {}: {} {}", filmId, genre.getId(), genre.getName());
        } catch (DataIntegrityViolationException e) {
            msg = String.format(
                    "Невозможно установить жанр %s фильма %s. %s",
                    genre.getId(),
                    filmId,
                    e.fillInStackTrace()
            );
            log.info(msg);
            throw new NoDataFoundException(msg);
        }

        return outGenre;
    }

    private Genre mapGenreRow(SqlRowSet genreRows) {
        Genre genre = new Genre(
                genreRows.getInt("GENRE_ID")
                , genreRows.getString("GENRE_NAME")
        );
        return genre;
    }
}
