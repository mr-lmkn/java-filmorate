package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoDataFoundException;
import ru.yandex.practicum.filmorate.exception.WrongFilmDataException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film_directors.FilmDirectorsStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@AllArgsConstructor
@Slf4j
public class FilmDaoStorageImpl implements FilmStorage {
    private final JdbcTemplate dataSource;
    private GenreStorage genreStorage;
    private final FilmDirectorsStorage filmDirectorsStorage;

    @Override
    public List<Film> getAllFilms() {
        ArrayList<Film> films = new ArrayList<Film>();
        SqlRowSet filmsRows = dataSource.queryForRowSet(
                "SELECT  f.FILM_ID "
                        + ", f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION "
                        + ", f.RATING_ID, r.RATING_CODE, r.RATING_NAME, r.RATING_DESCRIPTION "
                        // Не получается получить данные по имени поля
                        + ", LISTAGG (l.USER_ID,',') as `LIKERS` "
                        + ", (SELECT GROUP_CONCAT (l.USER_ID) FROM FILM_LIKES l WHERE f.FILM_ID = l.FILM_ID) AS lkrs "
                        + ", GROUP_CONCAT(d.DIRECTOR_ID) as directors_ids"
                        + ", GROUP_CONCAT(d.DIRECTOR_NAME) as directors_names"
                        + " FROM FILMS f "
                        + " LEFT JOIN RATING r ON f.RATING_ID = r.RATING_ID "
                        + " LEFT JOIN FILM_LIKES l ON f.FILM_ID = l.FILM_ID "
                        + " LEFT JOIN FILM_DIRECTOR AS fd ON fd.FILM_ID = f.FILM_ID"
                        + " LEFT JOIN DIRECTOR AS d on d.DIRECTOR_ID = fd.DIRECTOR_ID"
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
                " SELECT f.FILM_ID " +
                        ", f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION " +
                        ", f.RATING_ID, r.RATING_CODE, r.RATING_NAME, r.RATING_DESCRIPTION "
                        + ", GROUP_CONCAT(d.DIRECTOR_ID) as directors_ids"
                        + ", GROUP_CONCAT(d.DIRECTOR_NAME) as directors_names"
                        + " FROM FILMS f LEFT JOIN RATING r ON f.RATING_ID = r.RATING_ID"
                        + " LEFT JOIN FILM_DIRECTOR AS fd ON fd.FILM_ID = f.FILM_ID"
                        + " LEFT JOIN DIRECTOR AS d on d.DIRECTOR_ID = fd.DIRECTOR_ID"
                        + " WHERE f.FILM_ID = ? "
                        + "GROUP BY f.FILM_ID",
                id);
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
        log.info("Build    {}", film.toString());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        Map<String, Object> parameters = mapFilmQueryParameters(film);
        Integer id = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
        log.info("Generated filmId - " + id);

        filmDirectorsStorage.create(id, getDirectorsIds(film));

        log.info(film.getGenres().toString());
        Set<Genre> newGenres = genreStorage.linkGenresToFilmId(id, film.getGenres());
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
                            + " WHERE FILM_ID = ?",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    id
            );
            genreStorage.linkGenresToFilmId(id, film.getGenres());

            if (updaterRows > 0) {
                log.info("Операция {} выполнена уcпешно", doDo);
                filmDirectorsStorage.update(id, getDirectorsIds(film));
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
            dataSource.update(
                    "DELETE FROM FILM_LIKES WHERE FILM_ID = ?",
                    id
            );
            dataSource.update(
                    "DELETE FROM FILM_GENRES WHERE FILM_ID = ?",
                    id
            );

            Integer updaterRows = dataSource.update(
                    "DELETE FROM FILMS WHERE FILM_ID = ?",
                    id
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
                            + "   INSERT (FILM_ID,USER_ID) VALUES(?, ?)",
                    filmId,
                    userId,
                    filmId,
                    userId
            );
        } catch (DataIntegrityViolationException e) {
            String msg = String.format("Ошибка записи лайка для фильма %s от пользователя %s. %s",
                    filmId, userId, e.fillInStackTrace());
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
                "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?",
                filmId,
                userId
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
                        + ", GROUP_CONCAT(d.DIRECTOR_ID) as directors_ids"
                        + ", GROUP_CONCAT(d.DIRECTOR_NAME) as directors_names"
                        + " FROM FILMS f "
                        + " JOIN RATING r ON r.RATING_ID = f.RATING_ID "
                        + " LEFT JOIN fl ON fl.FILM_ID = f.FILM_ID "
                        + " LEFT JOIN FILM_DIRECTOR AS fd ON fd.FILM_ID = f.FILM_ID"
                        + " LEFT JOIN DIRECTOR AS d on d.DIRECTOR_ID = fd.DIRECTOR_ID"
                        + " GROUP BY f.FILM_ID "
                        + " ORDER BY `LIKES_CNT` DESC NULLS LAST"
                        + " LIMIT ?;",
                limit
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

    @Override
    public List<Film> findFilmsByDirector(Integer directorId, String sortBy) throws NoDataFoundException {
        String commonSqlFirstPart = "select f.FILM_ID AS film_id," +
                " f.FILM_NAME AS film_name," +
                " f.DESCRIPTION AS description," +
                " f.RELEASE_DATE AS release_date," +
                " f.DURATION AS duration," +
                " f.rating_id AS rating_id, " +
                " rating.rating_name AS rating_name, " +
                " rating.rating_code AS rating_code, " +
                " rating.rating_description AS rating_description, " +
                " GROUP_CONCAT(d.director_id) AS directors_ids," +
                " GROUP_CONCAT(d.director_name) AS directors_names,";
        String commonSqlSecondPart = " from FILMS as f " +
                " left join rating on f.rating_id = rating.rating_id" +
                " join film_director as fd on fd.film_id = f.film_id" +
                " join director as d on d.director_id = fd.director_id";
        String commonSqlThirdPart = " where fd.director_id = ?" +
                " GROUP BY film_id";

        String sortByLikeSqlFirstPart = " count(fl.film_id) as likes_count";
        String sortByLikeSqlSecondPart = " left join film_likes as fl on fl.film_id = f.film_id";
        String sortByLikeSqlThirdPart = " ORDER BY likes_count DESC";

        String sortByYearSql = " ORDER BY release_date";

        StringBuilder sqlSb = new StringBuilder();
        String sql;
        if (sortBy.equals("year")) {
            sql = sqlSb
                    .append(commonSqlFirstPart)
                    .append(commonSqlSecondPart)
                    .append(commonSqlThirdPart)
                    .append(sortByYearSql)
                    .toString();
        } else {
            sql = sqlSb
                    .append(commonSqlFirstPart)
                    .append(sortByLikeSqlFirstPart)
                    .append(commonSqlSecondPart)
                    .append(sortByLikeSqlSecondPart)
                    .append(commonSqlThirdPart)
                    .append(sortByLikeSqlThirdPart)
                    .toString();
        }

        SqlRowSet filmsRows = dataSource.queryForRowSet(sql, directorId);
        if (filmsRows.next()) {
            List<Film> films = new ArrayList<>();
            do {
                Film film = mapFilmRow(filmsRows);
                films.add(film);
                log.info("Найден фильм: {} {}", film.getId(), film.getName());
            } while (filmsRows.next());
            log.info("Конец списка фильмов");
            return films;
        } else {
            throw new NoDataFoundException("Список фильмов пустой");
        }
    }

    @Override
    public List<Film> getCommonFavouriteFilms(Integer userId, Integer friendId) {
        String sql = "select f.*," +
                " rating.rating_name AS rating_name, " +
                " rating.rating_code AS rating_code, " +
                " rating.rating_description AS rating_description, " +
                " GROUP_CONCAT(d.director_id) AS directors_ids," +
                " GROUP_CONCAT(d.director_name) AS directors_names," +
                " COUNT(fl3.film_id) as like_count" +
                " from films as f" +
                " join film_likes as fl1 on f.film_id = fl1.film_id" +
                " join film_likes as fl2 on fl1.film_id = fl2.film_id" +
                " left join film_likes as fl3 on f.film_id = fl3.film_id" +
                " left join rating on f.rating_id = rating.rating_id" +
                " left join film_director as fd on fd.film_id = f.film_id" +
                " left join director as d on d.director_id = fd.director_id" +
                " where fl1.user_id = ? and fl2.user_id = ?" +
                " group by f.film_id" +
                " order by like_count DESC";

        SqlRowSet filmsRows = dataSource.queryForRowSet(sql, userId, friendId);
        List<Film> films = new ArrayList<>();
        while (filmsRows.next()) {
            Film film = mapFilmRow(filmsRows);
            films.add(film);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
        }
        log.info("Конец списка фильмов");
        return films;
    }

    private Film mapFilmRow(SqlRowSet filmRows) {
        Integer filmId = filmRows.getInt("FILM_ID");
        Set<Integer> likes = getFilmLikes(filmId);
        Set<Genre> genre = genreStorage.getGenresByFilmId(filmId);

        TreeSet<Director> directors = new TreeSet<>(Comparator.comparing(Director::getId));
        try {
            String rsDirectorsId = filmRows.getString("directors_ids");
            String rsDirectorsName = filmRows.getString("directors_names");
            if (rsDirectorsId != null) {
                String[] directorsId = rsDirectorsId.split(",");
                String[] directorName = rsDirectorsName.split(",");
                for (int i = 0; i < directorsId.length; i++) {
                    Director director = Director.builder()
                            .id(Integer.parseInt(directorsId[i]))
                            .name(directorName[i])
                            .build();
                    directors.add(director);
                }
            }
        } catch (InvalidResultSetAccessException e) {
            log.info("В запросе не запрошены режиссеры");
        }

        Film film = Film.builder()
                .id(filmId)
                .name(filmRows.getString("FILM_NAME"))
                .description(filmRows.getString("DESCRIPTION"))
                .releaseDate(filmRows.getDate("RELEASE_DATE").toLocalDate())
                .duration(filmRows.getInt("DURATION"))
                .mpa(new Mpa(filmRows.getInt("RATING_ID"),
                        filmRows.getString("RATING_CODE"),
                        filmRows.getString("RATING_NAME"),
                        filmRows.getString("RATING_DESCRIPTION"))
                )
                .genres(genre)
                .likes(likes)
                .directors(directors)
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
                        + " WHERE f.FILM_ID = ? ;",
                filmId
        );
        while (likesRows.next()) {
            Integer userId = likesRows.getInt("USER_ID");
            likes.add(userId);
            log.info("Фильм {} лайкал пользователь {}", filmId, userId);
        }
        log.info("Конец списка лайков...");
        return likes;
    }

    private List<Integer> getDirectorsIds(Film film) {
        Set<Director> directors = film.getDirectors();
        List<Integer> directorIds = new ArrayList<>();
        if (directors != null) {
            for (Director director : directors) {
                directorIds.add(director.getId());
            }
        }
        return directorIds;
    }

    public List<Film> getSearch(String query, String by) {
        ArrayList<Film> films = new ArrayList<Film>();
        if (by.equals("title")) {
            SqlRowSet filmsRows = dataSource.queryForRowSet("WITH fl AS (SELECT FILM_ID, COUNT(DISTINCT USER_ID) AS `LIKES_CNT` " +
                                                                "FROM FILM_LIKES " +
                                                                "GROUP BY FILM_ID)" +
                                                                "SELECT f.*," +
                                                                "r.RATING_CODE, r.RATING_NAME, r.RATING_DESCRIPTION " +
                                                                "FROM FILMS f " +
                                                                "JOIN RATING r ON r.RATING_ID = f.RATING_ID " +
                                                                "LEFT JOIN fl ON fl.FILM_ID = f.FILM_ID " +
                                                                "WHERE UPPER(FILM_NAME) LIKE CONCAT('%',?,'%')" +
                                                                "ORDER BY `LIKES_CNT` DESC NULLS LAST ", query.toUpperCase());
            while (filmsRows.next()) {
                Film film = mapFilmRow(filmsRows);
                films.add(film);
                log.info("Найден фильм: {} {}", film.getId(), film.getName());
            }
            log.info("Конец списка фильмов");
        } else if (by.equals("director")) {
            SqlRowSet filmsRows = dataSource.queryForRowSet("WITH fl AS (SELECT FILM_ID, COUNT(DISTINCT USER_ID) AS `LIKES_CNT` " +
                                                            "FROM FILM_LIKES " +
                                                            "GROUP BY FILM_ID)" +
                                                            "SELECT f.*," +
                                                            "r.RATING_CODE, r.RATING_NAME, r.RATING_DESCRIPTION, GROUP_CONCAT(d.director_id) AS DIRECTORS_IDS, GROUP_CONCAT(d.director_name) AS DIRECTORS_NAMES " +
                                                            "FROM FILMS f " +
                                                            "JOIN RATING r ON r.RATING_ID = f.RATING_ID " +
                                                            "LEFT JOIN fl ON fl.FILM_ID = f.FILM_ID " +
                                                            "LEFT JOIN film_director AS fd ON fd.FILM_ID = f.FILM_ID " +
                                                            "LEFT JOIN director AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                                                            "GROUP BY f.film_id " +
                                                            "HAVING UPPER(DIRECTORS_NAMES) LIKE CONCAT('%',?,'%')" +
                                                            "ORDER BY `LIKES_CNT` DESC NULLS LAST ", query.toUpperCase());
            while (filmsRows.next()) {
                Film film = mapFilmRow(filmsRows);
                films.add(film);
                log.info("Найден фильм: {} {}", film.getId(), film.getName());
            }
            log.info("Конец списка фильмов");
        } else if (by.equals("director,title") || by.equals("title,director")) {
            SqlRowSet filmsRows = dataSource.queryForRowSet("WITH fl AS (SELECT FILM_ID, COUNT(DISTINCT USER_ID) AS `LIKES_CNT` " +
                                                            "FROM FILM_LIKES " +
                                                            "GROUP BY FILM_ID)" +
                                                            "SELECT f.*," +
                                                            "r.RATING_CODE, r.RATING_NAME, r.RATING_DESCRIPTION, GROUP_CONCAT(d.director_id) AS DIRECTORS_IDS, GROUP_CONCAT(d.director_name) AS DIRECTORS_NAMES " +
                                                            "FROM FILMS f " +
                                                            "JOIN RATING r ON r.RATING_ID = f.RATING_ID " +
                                                            "LEFT JOIN fl ON fl.FILM_ID = f.FILM_ID " +
                                                            "LEFT JOIN film_director AS fd ON fd.FILM_ID = f.FILM_ID " +
                                                            "LEFT JOIN director AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                                                            "GROUP BY f.film_id " +
                                                            "HAVING UPPER(FILM_NAME) LIKE CONCAT('%',?,'%') OR UPPER(DIRECTORS_NAMES) LIKE CONCAT('%',?,'%') " +
                                                            "ORDER BY `LIKES_CNT` DESC NULLS LAST ", query.toUpperCase(), query.toUpperCase());
            while (filmsRows.next()) {
                Film film = mapFilmRow(filmsRows);
                films.add(film);
                log.info("Найден фильм: {} {}", film.getId(), film.getName());
            }
            log.info("Конец списка фильмов");
        }
        return films;
    }

    @Override
    public List<Film> getRecommendations(int userId) throws NoDataFoundException {
        ArrayList<Film> films = new ArrayList<Film>();

        SqlRowSet rs = dataSource.queryForRowSet(
                "SELECT * FROM FILMS" +
                        "    JOIN RATING ON FILMS.RATING_ID = RATING.RATING_ID" +
                        "         WHERE FILMS.FILM_ID IN (" +
                        "             SELECT FILM_ID FROM FILM_LIKES" +
                        "                            WHERE USER_ID IN (" +
                        "                                SELECT FL1.USER_ID FROM FILM_LIKES AS FL1" +
                        "                                    RIGHT JOIN FILM_LIKES FL2 ON FL2.FILM_ID = FL1.FILM_ID" +
                        "                                                   GROUP BY FL1.USER_ID, FL2.USER_ID" +
                        "                                                   HAVING FL1.USER_ID IS NOT NULL AND" +
                        "                                                   FL1.USER_ID != ? AND FL2.USER_ID = ?" +
                        "                                                   ORDER BY COUNT(FL1.USER_ID) DESC" +
                        "                                                   LIMIT 1" +
                        "                                )" +
                        "                              AND FILM_ID NOT IN (" +
                        "                                  SELECT FILM_ID FROM FILM_LIKES WHERE USER_ID = ?" +
                        "                                  )" +
                        "             )",
                userId,userId,userId
        );
        while (rs.next()) {
            Film film = mapFilmRow(rs);
            films.add(film);
        }
        log.info("Конец списка фильмов с рекомендациями...");
        return films;

    }
}