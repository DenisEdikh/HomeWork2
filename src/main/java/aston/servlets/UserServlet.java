package aston.servlets;

import aston.exception.InternalServerException;
import aston.exception.NotFoundException;
import aston.model.User;
import aston.server.DataBase;
import aston.service.UserService;
import aston.service.UserServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class UserServlet extends HttpServlet {
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    private final UserService userService;

    public UserServlet(DataBase db) {
        this.userService = new UserServiceImpl(db);
    }

    // Конструктор для тестов
    public UserServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = gson.fromJson(req.getReader(), User.class);
            User userStored = userService.createUser(user);
            if (userStored != null) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(userStored));
            } else {
                sendBadRequest(resp);
            }
        } catch (InternalServerException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Optional<Long> optId = getIdFromRequest(req);

        if (path.matches("/\\d+$")) {
            if (optId.isPresent()) {
                try {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(userService.getUser(optId.get())));
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

    private Optional<Long> getIdFromRequest(HttpServletRequest req) {
        String[] pathSplit = req.getPathInfo().split("/");

        try {
            return Optional.of(Long.parseLong(pathSplit[1]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    private void sendBadRequest(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write(gson.toJson("BAD REQUEST"));
    }

}
