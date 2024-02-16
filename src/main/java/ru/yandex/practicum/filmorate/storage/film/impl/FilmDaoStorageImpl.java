package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.*;

@Repository
@AllArgsConstructor
@Slf4j
public class FilmDaoStorageImpl implements FilmStorage {
    private final JdbcTemplate dataSource;
    // private MpaStorage mpaStorage; - переделал слегка мапинг
    private GenreStorage genreStorage;

    @Override
    public List<Film> getAllFilms() {
        ArrayList<Film> films = new ArrayList<Film>();
        SqlRowSet filmsRows = dataSource.queryForRowSet(
                "SELECT  f.FILM_ID "
                        + ", f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION "
                        + ", f.RATING_ID, r.RATING_CODE, r.RATING_NAME, r.RATING_DESCRIPTION "
                        + ", STRING_AGG (l.USER_ID, ',') as `LIKERS` "
                        + " FROM FILMS f "
                        + " LEFT JOIN RATING r ON f.RATING_ID = r.RATING_ID "
                        + " LEFT JOIN FILM_LIKES l ON f.FILM_ID = l.FILM_ID "
                        + " GROUP BY f.FILM_ID "
                        + ", f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION "
                        + ", f.RATING_ID, r.RATING_CODE, r.RATING_NAME, r.RATING_DESCRIPTION "
        );
        while (filmsRows.next()) {
            Film film = mapFilmRow(filmsRows);
            films.add(film);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
        }
        log.info("Конец списка фильмов");
        return films;
    }

    @Override
    public Film getFilmById(Integer id) throws NoDataFoundException {
        SqlRowSet filmRows = dataSource.queryForRowSet(
                " SELECT f.*, r.RATING_CODE, r.RATING_NAME, r.RATING_DESCRIPTION "
                        + " FROM FILMS f LEFT JOIN RATING r ON f.RATING_ID = r.RATING_ID"
                        + " WHERE FILM_ID = ? "
                , id);
        if (filmRows.next()) {
            Film film = mapFilmRow(filmRows);
            log.info("Фильм {} название {}", film.getId(), film.getName());
            return film;
        } else {
            String msg = String.format("Нет фильма с 'id'=%s.", id);
            log.info(msg);
            throw new NoDataFoundException(msg);
        }
    }

    @Override
    public Film createFilm(Film film) throws NoDataFoundException {
        Film outFilm = new Film();
        log.info("Запись нового фильма");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        Map<String, Object> parameters = mapFilmQueryParameters(film);
        Integer id = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
        log.info("Generated filmId - " + id);

        log.info(film.getGenres().toString());
        Set<Genre> newGenres = genreStorage.linkGenresToFilmId(id,film.getGenres());
        film.setGenres(newGenres);
        film.setId(id);

        try {
            outFilm = getFilmById(id);
        } catch (NoDataFoundException e) {
            log.info("Ошибка чтения нового фильма: {}", e.fillInStackTrace());
        }

        log.info("Операция создание фильма выполнена уcпешно");
        return outFilm;
    }

    @Override
    public Film updateFilm(Film film) throws WrongFilmDataException, NoDataFoundException {
        Integer id = film.getId();
        String doDo = "Oбновление фильма";
        log.info("{} {}", doDo, id);

        if (id != null && id > 0) {
            Integer updaterRows = dataSource.update(
                    "UPDATE FILMS SET "
                            + " FILM_NAME = ?,"
                            + " DESCRIPTION = ?,"
                            + " RELEASE_DATE = ?,"
                            + " DURATION = ?,"
                            + " RATING_ID = ? "
                            + " WHERE FILM_ID = ?"
                    , film.getName()
                    , film.getDescription()
                    , film.getReleaseDate()
                    , film.getDuration()
                    , film.getMpa().getId()
                    , id
            );
            genreStorage.linkGenresToFilmId(id,film.getGenres());

            if (updaterRows > 0) {
                log.info("Операция {} выполнена уcпешно", doDo);
                return getFilmById(id);
            } else {
                String msg = String.format("Нет фильма с 'id' %s. Обновление не возможно.", id);
                log.info(msg);
                throw new NoDataFoundException(msg);
            }
        }

        String msg = String.format("Не указан 'id' %s. Обновление не возможно.", id);
        log.info(msg);
        throw new WrongFilmDataException(msg);
    }

    @Override
    public void delete(Integer id) throws WrongFilmDataException {
        String doDo = "Удаление фильма";
        log.info("{} {}", doDo, id);
        String msg;

        if (id != null && id > 0) {
            Integer updaterRows = dataSource.update(
                    "DELETE FROM FILMS WHERE USER_ID = ?"
                    , id
            );

            if (updaterRows > 0) {
                log.info("Фильм {} удален", id);
            } else {
                msg = String.format("Нет фильма с ID %s", id);
                log.info(msg);
                throw new WrongFilmDataException(msg);
            }
        }

    }

    @Override
    public Film addLike(Integer filmId, Integer userId) throws NoDataFoundException {
        log.info("Привязка лайка к фильму {}", filmId);
        log.info("от пользователя {}", userId);
        try {
            Integer insertedRows = dataSource.update(
                    "MERGE INTO FILM_LIKES l "
                            + " USING VALUES (?, ?) as u(FILM_ID, USER_ID)"
                            + " ON u.FILM_ID = l.FILM_ID AND u.USER_ID = l.USER_ID "
                            + " WHEN NOT MATCHED THEN "
                            + "   INSERT (FILM_ID,USER_ID) VALUES(?, ?)"
                    , filmId
                    , userId
                    , filmId
                    , userId
            );
        } catch (DataIntegrityViolationException e) {
            String msg = String.format("Ошибка записи лайка для фильма %s от пользователя %s. %s"
                    , filmId, userId, e.fillInStackTrace());
            log.info(msg);
            throw new NoDataFoundException(msg);
        }

        return getFilmById(filmId);
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) throws NoDataFoundException {
        log.info("Добавления лайка фильму {}", filmId);
        log.info("от пользователя {}", userId);
        Integer deletedRows = dataSource.update(
                "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?"
                , filmId
                , userId
        );

        if (deletedRows == 0) {
            String msg = String.format("Нет данных о лайке фильма %s от пользователя %s", filmId, userId);
            throw new NoDataFoundException(msg);
        }

        return getFilmById(filmId);
    }

    @Override
    public List<Film> getPopular(Integer limit) throws NoDataFoundException {
        ArrayList<Film> films = new ArrayList<Film>();
        SqlRowSet filmsRows = dataSource.queryForRowSet(
                " WITH fl AS ( "
                        + " SELECT FILM_ID, COUNT(DISTINCT USER_ID) AS `LIKES_CNT`"
                        + " FROM FILM_LIKES "
                        + " GROUP BY FILM_ID "
                        + ")"
                        + " SELECT f.* "
                        + " , r.RATING_CODE, r.RATING_NAME, r.RATING_DESCRIPTION "
                        + " FROM FILMS f "
                        + " JOIN RATING r ON r.RATING_ID = f.RATING_ID "
                        + " LEFT JOIN fl ON fl.FILM_ID = f.FILM_ID "
                        + " ORDER BY `LIKES_CNT` DESC NULLS LAST"
                        + " LIMIT ?;"
                , limit
        );

        if (filmsRows.wasNull()) {
            throw new NoDataFoundException("Список фильмов пустой");
        }

        while (filmsRows.next()) {
            Film film = mapFilmRow(filmsRows);
            films.add(film);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
        }
        log.info("Конец списка популярных фильмов");
        return films;
    }

    private Film mapFilmRow(SqlRowSet filmRows) {
        Integer filmId = filmRows.getInt("FILM_ID");
        Set<Integer> likes = getFilmLikes(filmId);
        Set<Genre> genre = genreStorage.getGenresByFilmId(filmId);

        //log.info("Набор лайков: {}", filmRows.getString("LIKERS"));
        /*Set likes = new HashSet<>(); //getFilmLikes(filmId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            likes = objectMapper.readValue(filmRows.getString("LIKES"), Set.class);
        } catch (JsonProcessingException ignore) {} */

       /* Mpa mpa = new Mpa();
        try {
            mpa = mpaStorage.getMpaById(filmRows.getInt("RATING_ID"));
        } catch (NoDataFoundException ignore) {
        } */

        Film film = Film.builder()
                .id(filmId)
                .name(filmRows.getString("FILM_NAME"))
                .description(filmRows.getString("DESCRIPTION"))
                .releaseDate(filmRows.getDate("RELEASE_DATE").toLocalDate())
                .duration(filmRows.getInt("DURATION"))
                .mpa(new Mpa(filmRows.getInt("RATING_ID")
                        , filmRows.getString("RATING_CODE")
                        , filmRows.getString("RATING_NAME")
                        , filmRows.getString("RATING_DESCRIPTION"))
                )
                .genres(genre)
                .likes(likes)
                .build();
        return film;
    }

    private Map<String, Object> mapFilmQueryParameters(Film film) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("FILM_ID", film.getId());
        parameters.put("FILM_NAME", film.getName());
        parameters.put("DESCRIPTION", film.getDescription());
        parameters.put("RELEASE_DATE", film.getReleaseDate());
        parameters.put("DURATION", film.getDuration());
        parameters.put("RATING_ID", film.getMpa().getId());

        return parameters;
    }

    private Set<Integer> getFilmLikes(Integer filmId) {
        Set<Integer> likes = new HashSet<Integer>();
        log.info("Извлечение списка лайков для фильма {}", filmId);
        SqlRowSet likesRows = dataSource.queryForRowSet(
                "SELECT USER_ID "
                        + " FROM FILM_LIKES f "
                        + " WHERE f.FILM_ID = ? ;"
                , filmId
        );
        while (likesRows.next()) {
            Integer userId = likesRows.getInt("USER_ID");
            likes.add(userId);
            log.info("Фильм {} лайкал пользователь {}", filmId, userId);
        }
        log.info("Конец списка лайков...");
        return likes;

    }

}
