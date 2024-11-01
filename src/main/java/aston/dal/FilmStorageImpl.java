package aston.dal;

import aston.exception.InternalServerException;
import aston.model.Film;
import aston.server.DataBase;

import javax.sql.PooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilmStorageImpl implements FilmStorage {
    private final PooledConnection conn;

    public FilmStorageImpl(DataBase db) {
        this.conn = db.getConnection();
    }

    @Override
    public Film create(Film film) {
        String query = "INSERT INTO films (title) VALUES (?)";

        try (PreparedStatement ps = conn
                .getConnection()
                .prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, film.getTitle());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        Long id = ps.getGeneratedKeys().getLong("id");
                        film.setId(id);
                        return film;
                    }
                    throw new InternalServerException("Не удалось сохранить данные");
                }
            } else {
                throw new InternalServerException("Не удалось сохранить данные");
            }
        } catch (SQLException e) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        String query = "INSERT INTO user_film (user_id, film_id) VALUES (?, ?)";

        try (PreparedStatement ps = conn.getConnection().prepareStatement(query)) {
            ps.setLong(1, userId);
            ps.setLong(2, filmId);

            if (ps.executeUpdate() == 0) {
                throw new InternalServerException("Не удалось сохранить данные");
            }
        } catch (SQLException e) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public Optional<Film> findOne(Long id) {
        String query = "SELECT * FROM films WHERE id = ?";

        try (PreparedStatement ps = conn.getConnection().prepareStatement(query)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Film film = new Film();
                film.setId(rs.getLong("id"));
                film.setTitle(rs.getString("title"));
                return Optional.of(film);
            }
        } catch (SQLException e) {
            throw new InternalServerException("Не удалось получить данные");
        }
        return Optional.empty();
    }

    @Override
    public List<Film> findMany(Long user_id) {
        String query = "SELECT f.* FROM films f LEFT JOIN user_film uf ON f.id = uf.film_id WHERE uf.user_id = ?";
        List<Film> films = new ArrayList<>();

        try (PreparedStatement ps = conn.getConnection().prepareStatement(query)) {
            ps.setLong(1, user_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Film film = new Film();
                film.setId(rs.getLong("id"));
                film.setTitle(rs.getString("title"));
                films.add(film);
            }
        } catch (SQLException e) {
            throw new InternalServerException("Не удалось получить данные");
        }
        return films;
    }
}
