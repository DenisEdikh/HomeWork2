package aston;

import aston.dto.FilmDto;
import aston.exception.NotFoundException;
import aston.model.Film;
import aston.model.User;
import aston.server.DataBase;
import aston.service.FilmService;
import aston.service.FilmServiceImpl;
import aston.service.UserService;
import aston.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmServiceTestIT {
    private static DataBase db;
    private static FilmService filmService;
    private static UserService userService;

    @BeforeAll
    public static void setUp() {
        db = new DataBase(
                "jdbc:h2:mem:h2",
                "h2",
                "h2",
                DataBase.TypeBase.H2
        );
        db.initialize();
        filmService = new FilmServiceImpl(db);
        userService = new UserServiceImpl(db);
    }

    @BeforeEach
    public void updateTable() throws SQLException {
        String query = """
                DELETE FROM user_film;
                DELETE FROM films;
                DELETE FROM users;
                """;
        try (Statement stmt = db.getConnection().getConnection().createStatement()) {
            stmt.executeUpdate(query);
        }
    }

    @Test
    public void createFilm() {
        Film film = new Film();
        film.setTitle("Terminator");

        Film createdFilm = filmService.createFilm(film);

        assertNotNull(createdFilm.getId());
        assertEquals("Terminator", createdFilm.getTitle());
    }

    @Test
    public void getFilm() {
        Film film = new Film();
        film.setTitle("Terminator");
        Film createdFilm = filmService.createFilm(film);
        Film storedFilm = filmService.getFilm(createdFilm.getId());

        assertEquals(createdFilm.getId(), storedFilm.getId());
        assertEquals(createdFilm.getTitle(), storedFilm.getTitle());
    }

    @Test
    public void addLike() {
        Film film = new Film();
        film.setTitle("Terminator");
        User user = new User();
        user.setName("J. Cameron");
        User createdUser = userService.createUser(user);
        Film createdFilm = filmService.createFilm(film);

        filmService.addLike(createdUser.getId(), createdFilm.getId());
        User storedUser = userService.getUser(createdUser.getId());
        List<FilmDto> likeFilms = storedUser.getFilms();

        assertEquals(1, likeFilms.size());
        assertEquals(likeFilms.get(0).getTitle(), film.getTitle());
    }

    @Test
    public void getFilmWhenNotFound() {
        Long filmId = 999L;

        NotFoundException e = assertThrows(NotFoundException.class, () -> filmService.getFilm(filmId));

        assertEquals("film с id = %d отсутствует".formatted(filmId), e.getMessage());
    }
}
