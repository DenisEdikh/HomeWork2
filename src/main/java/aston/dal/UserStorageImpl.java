package aston.dal;

import aston.exception.InternalServerException;
import aston.model.User;
import aston.server.DataBase;

import javax.sql.PooledConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserStorageImpl implements UserStorage {
    private final PooledConnection conn;

    public UserStorageImpl(DataBase db) {
        this.conn = db.getConnection();
    }

    @Override
    public User create(User user) {
        String query = "INSERT INTO users (name) VALUES (?)";

        try (PreparedStatement ps = conn
                .getConnection()
                .prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        Long id = ps.getGeneratedKeys().getLong("id");
                        user.setId(id);
                        return user;
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
    public Optional<User> findOne(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.getConnection().prepareStatement(query)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new InternalServerException("Не удалось получить данные");
        }
        return Optional.empty();
    }

}
