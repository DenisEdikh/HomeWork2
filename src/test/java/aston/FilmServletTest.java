package aston;

import aston.exception.InternalServerException;
import aston.exception.NotFoundException;
import aston.model.Film;
import aston.service.FilmService;
import aston.servlets.FilmServlet;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FilmServletTest {
    private FilmService filmService;
    private FilmServlet filmServlet;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private StringWriter responseWriter;
    private Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws Exception {
        filmService = mock(FilmService.class);
        filmServlet = new FilmServlet(filmService);
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        when(res.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void doPostWhenAllValid() throws Exception {
        Film film = new Film(1L, "Terminator");
        String filmJson = gson.toJson(film);

        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(filmJson)));
        when(filmService.createFilm(film)).thenReturn(film);

        filmServlet.doPost(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_CREATED);
        Film newFilm = gson.fromJson(responseWriter.toString(), Film.class);
        assertEquals("Terminator", newFilm.getTitle());
    }

    @Test
    public void doPostWhenBadRequest() throws Exception {
        Film film = new Film(1L, "Terminator");
        String filmJson = gson.toJson(film);

        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(filmJson)));
        when(filmService.createFilm(film)).thenReturn(null);

        filmServlet.doPost(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void doPostWhenInternalServerError() throws Exception {
        Film film = new Film(1L, "Terminator");
        String filmJson = gson.toJson(film);

        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(filmJson)));
        when(filmService.createFilm(film)).thenThrow(new InternalServerException("Server error"));

        filmServlet.doPost(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void doGetWhenAllValid() throws Exception {
        Long filmId = 1L;
        Film film = new Film(filmId, "Terminator");

        when(req.getPathInfo()).thenReturn("/%d".formatted(filmId));
        when(filmService.getFilm(filmId)).thenReturn(film);

        filmServlet.doGet(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_OK);
        Film newFilm = gson.fromJson(responseWriter.toString(), Film.class);
        assertEquals("Terminator", newFilm.getTitle());
    }

    @Test
    public void doGetWhenBadRequest() throws Exception {
        when(req.getPathInfo()).thenReturn("/[a-zA-Z]");

        filmServlet.doGet(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void doGetWhenNotFoundFilm() throws Exception {
        Long filmId = 1L;
        Film film = new Film(filmId, "Terminator");

        when(req.getPathInfo()).thenReturn("/%d".formatted(filmId));
        when(filmService.getFilm(filmId))
                .thenThrow(new NotFoundException("film с id = %d отсутствует".formatted(filmId)));

        filmServlet.doGet(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void doPutWhenAllValid() throws Exception {
        Long filmId = 1L;
        Long userId = 1L;

        when(req.getPathInfo()).thenReturn("/%d/like/%d".formatted(filmId, userId));

        filmServlet.doPut(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void doPutWhenBadRequest() throws Exception {
        when(req.getPathInfo()).thenReturn("/[a-zA-z]/like/[a-zA-z]");

        filmServlet.doPut(req, res);
        verify(res, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}

