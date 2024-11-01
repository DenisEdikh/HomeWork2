package aston.dal;

import aston.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    void addLike(Long userId, Long filmId);

    Optional<Film> findOne(Long id);

    List<Film> findMany(Long user_id);
}
