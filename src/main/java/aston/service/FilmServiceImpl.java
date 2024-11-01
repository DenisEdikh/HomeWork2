package aston.service;

import aston.dal.FilmStorage;
import aston.dal.FilmStorageImpl;
import aston.dal.UserStorage;
import aston.dal.UserStorageImpl;
import aston.exception.NotFoundException;
import aston.model.Film;
import aston.server.DataBase;

public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmServiceImpl(DataBase db) {
        this.filmStorage = new FilmStorageImpl(db);
        this.userStorage = new UserStorageImpl(db);
    }

    @Override
    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        filmStorage.findOne(filmId)
                .orElseThrow(() -> new NotFoundException("film с id = %d отсутствует".formatted(filmId)));
        userStorage.findOne(userId)
                .orElseThrow(() -> new NotFoundException("User с id = %d отсутствует".formatted(userId)));
        filmStorage.addLike(userId, filmId);
    }

    @Override
    public Film getFilm(Long id) {
        return filmStorage.findOne(id)
                .orElseThrow(() -> new NotFoundException("film с id = %d отсутствует".formatted(id)));
    }
}
