package aston;

import aston.dto.FilmDto;
import aston.exception.NotFoundException;
import aston.model.User;
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

public class UserServiceTestIT {
    private static DataBase db;
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
    public void testCreateFilm() {
        User user = new User();
        user.setName("J. Cameron");

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("J. Cameron", createdUser.getName());
    }

    @Test
    public void testGetFilm() {
        User user = new User();
        user.setName("J. Cameron");

        User createdUser = userService.createUser(user);
        User storedUser = userService.getUser(createdUser.getId());

        assertEquals(createdUser.getId(), storedUser.getId());
        assertEquals(createdUser.getName(), storedUser.getName());
    }
    
    @Test
    public void testGetFilm_NotFound() {
        Long userId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUser(userId));

        assertEquals("user с id = %d отсутствует".formatted(userId), exception.getMessage());
    }
}
