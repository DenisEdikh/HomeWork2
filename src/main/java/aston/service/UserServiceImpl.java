package aston.service;

import aston.dal.FilmStorage;
import aston.dal.FilmStorageImpl;
import aston.dal.UserStorage;
import aston.dal.UserStorageImpl;
import aston.dto.FilmDto;
import aston.exception.NotFoundException;
import aston.mapper.FilmMapper;
import aston.model.User;
import aston.server.DataBase;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public UserServiceImpl(DataBase db) {
        this.userStorage = new UserStorageImpl(db);
        this.filmStorage = new FilmStorageImpl(db);
    }

    @Override
    public User createUser(User user) {
        return userStorage.create(user);
    }

    @Override
    public User getUser(Long id) {
        User user = userStorage.findOne(id)
                .orElseThrow(() -> new NotFoundException("user с id = %d отсутствует".formatted(id)));
        List<FilmDto> films = FilmMapper.toFilmDto(filmStorage.findMany(id));
        user.setFilms(films);
        return user;
    }
}
