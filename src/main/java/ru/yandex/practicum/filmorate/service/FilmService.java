package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.WrongFilmData;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
public class FilmService {

    private static Map<Integer, Film> films = new HashMap<>();
    private static Integer filmsMapKeyCounter = 0;

    public List<Film> getAllFilms() {
        return  new ArrayList<>(films.values());
    }

    public Film getFilmById(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        log.warn("Фильма с ИД {} - нет в системе", id);
        return null;
    }

    public Optional<Film> getFilmByName(String name) {
        // Думаю, эта функция будет заменена, хотел все-таки вернуть нулл из MAP. Получилось... Гм. мдеее.
        // Надобыло брать LIST
        // Надо бы посимпатичнее это вот написать
        Film existsSameFilm;
        try {
            existsSameFilm = films.entrySet()
                    .stream()
                    .filter(a -> Objects.equals(a.getValue().getName(), name))
                    .findFirst()
                    .get()
                    .getValue();
        } catch (NoSuchElementException e) {
            existsSameFilm = null;
        }
        return Optional.ofNullable(existsSameFilm);
    }

    public Film createOrUpdateFilm(Optional<Integer> optionalId, Film film) throws WrongFilmData {
        // Не уверен, что это верное решение. Наверное, можно как-то использовать билдер и валидатор
        // Вместо этого класса или пихать это в "сервис"
        Integer filmMapKey;
        Integer filmId = film.getId();
        //String login = film.getLogin();
        String name = film.getName();
        String doDo = "";
        Optional<Film> existsSameFilm = getFilmByName(name);


        if (optionalId.isPresent()) {
            doDo = "обновление фильма";
            filmMapKey = optionalId.get();
            log.info("Инициировано {} {}", doDo, filmMapKey);
            boolean notSameId = !filmMapKey.equals(filmId);

            if (filmMapKey < 0 || notSameId) {
                String msg = String.format("Ошибка заполнения поля 'id' %s != %s ", filmMapKey, filmId);
                log.info(msg);
                throw new WrongFilmData(msg);
            }

            if (!films.containsKey(filmMapKey)) {
                String msg = String.format("Нет фильма с 'id' %s. Обновление не возможно.", filmMapKey);
                log.info(msg);
                throw new WrongFilmData(msg);
            }

            if (existsSameFilm.isPresent()) {
                if (existsSameFilm.get().getId() != filmMapKey) {
                    String msg = String.format("Не возможно обновить фильм наименование %s уже используется ", name);
                    log.info(msg);
                    throw new WrongFilmData(msg);
                }
            }

        } else {
            doDo = "создание фильма";
            log.info("Инициировано {}", doDo);
            if (existsSameFilm.isPresent()) {
                String msg = String.format("Не возможно создать фильма логин %s занят ", name);
                log.info(msg);
                throw new WrongFilmData(msg);
            }
            filmMapKey = ++filmsMapKeyCounter;
            film.setId(filmsMapKeyCounter);
        }

        films.put(filmMapKey, film);
        log.info("Операция {} выполнена уcпешно", doDo);

        return film;
    }


    public void delete(Integer id) throws WrongFilmData {
        if (!films.containsKey(id)) {
            String msg = String.format("Нет фильма с ID %s", id);
            log.info(msg);
            throw new WrongFilmData(msg);
        } else {
            films.remove(id);
            log.info("Фильм {} удален", id);
        }
    }

    public static void flushFilms() {
        films = new HashMap<>();
        filmsMapKeyCounter = 0;
    }

}
