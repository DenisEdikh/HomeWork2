package aston;

import aston.exception.InternalServerException;
import aston.exception.NotFoundException;
import aston.model.User;
import aston.service.UserService;
import aston.servlets.UserServlet;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServletTest {
    private UserService userService;
    private UserServlet userServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private StringWriter responseWriter;
    private Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws Exception {
        userService = mock(UserService.class);
        userServlet = new UserServlet(userService);
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        when(res.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void doPostWhenAllValid() throws Exception {
        User user = new User(1L, "J. Cameron", List.of());
        String userJson = gson.toJson(user);

        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(userJson)));
        when(userService.createUser(user)).thenReturn(user);

        userServlet.doPost(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_CREATED);
        User newUser = gson.fromJson(responseWriter.toString(), User.class);
        assertEquals("J. Cameron", newUser.getName());
    }

    @Test
    public void doPostWhenBadRequest() throws Exception {
        User user = new User(1L, "Terminator", List.of());
        String filmJson = gson.toJson(user);

        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(filmJson)));
        when(userService.createUser(user)).thenReturn(null);

        userServlet.doPost(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void doPostWhenInternalServerError() throws Exception {
        User user = new User(1L, "Terminator", List.of());
        String filmJson = gson.toJson(user);

        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(filmJson)));
        when(userService.createUser(user)).thenThrow(new InternalServerException("Server error"));

        userServlet.doPost(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void doGetWhenAllValid() throws Exception {
        Long userId = 1L;
        User user = new User(userId, "Terminator", List.of());

        when(req.getPathInfo()).thenReturn("/%d".formatted(userId));
        when(userService.getUser(userId)).thenReturn(user);

        userServlet.doGet(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_OK);
        User newFilm = gson.fromJson(responseWriter.toString(), User.class);
        assertEquals("Terminator", newFilm.getName());
    }

    @Test
    public void doGetWhenBadRequest() throws Exception {
        when(req.getPathInfo()).thenReturn("/[a-zA-Z]");

        userServlet.doGet(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void doGetWhenNotFoundFilm() throws Exception {
        Long userId = 1L;
        User user = new User(userId, "Terminator", List.of());

        when(req.getPathInfo()).thenReturn("/%d".formatted(userId));
        when(userService.getUser(userId))
                .thenThrow(new NotFoundException("user с id = %d отсутствует".formatted(userId)));

        userServlet.doGet(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}

