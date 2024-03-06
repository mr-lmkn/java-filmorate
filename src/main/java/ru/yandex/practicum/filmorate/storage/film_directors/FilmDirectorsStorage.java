package ru.yandex.practicum.filmorate.storage.film_directors;

import java.util.List;

public interface FilmDirectorsStorage {

    void create(int filmId, List<Integer> directorsIds);

    void update(int filmId, List<Integer> directorsIds);
}
