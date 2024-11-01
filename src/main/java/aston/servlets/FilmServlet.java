package aston.servlets;

import aston.exception.InternalServerException;
import aston.exception.NotFoundException;
import aston.model.Film;
import aston.server.DataBase;
import aston.service.FilmService;
import aston.service.FilmServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class FilmServlet extends HttpServlet {
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    private final FilmService filmService;

    public FilmServlet(DataBase db) {
        this.filmService = new FilmServiceImpl(db);
    }

    // Конструктор для тестов
    public FilmServlet(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Film film = gson.fromJson(req.getReader(), Film.class);
            Film filmStored = filmService.createFilm(film);
            if (filmStored != null) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(filmStored));
            } else {
                sendBadRequest(resp);
            }
        } catch (InternalServerException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        }
    }


    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Optional<Long> optFilmId = getIdFilmFromRequest(req);
        Optional<Long> optUserId = getIdUserFromRequest(req);

        if (path.matches("/\\d+/like/\\d+$")) {
            if (optFilmId.isPresent() && optUserId.isPresent()) {
                try {
                    filmService.addLike(optFilmId.get(), optUserId.get());
                    resp.setStatus(HttpServletResponse.SC_OK);
                } catch (NotFoundException e) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(e.getMessage()));
                }
            }
        } else {
            sendBadRequest(resp);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Optional<Long> optId = getIdFilmFromRequest(req);

        if (path.matches("/\\d+$")) {
            if (optId.isPresent()) {
                try {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(filmService.getFilm(optId.get())));
                } catch (NotFoundException e) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(e.getMessage()));
                }
            } else {
                sendBadRequest(resp);
            }
        } else {
            sendBadRequest(resp);
        }
    }

    public Optional<Long> getIdFilmFromRequest(HttpServletRequest req) {
        String[] pathSplit = req.getPathInfo().split("/");

        try {
            return Optional.of(Long.parseLong(pathSplit[1]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public Optional<Long> getIdUserFromRequest(HttpServletRequest req) {
        String[] pathSplit = req.getPathInfo().split("/");

        try {
            return Optional.of(Long.parseLong(pathSplit[3]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    private void sendBadRequest(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write(gson.toJson("BAD REQUEST"));
    }
}
