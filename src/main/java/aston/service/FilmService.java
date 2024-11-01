package aston.service;

import aston.model.Film;

public interface FilmService {
    Film createFilm(Film film);

    void addLike(Long userId, Long filmId);

    Film getFilm(Long id);
}
