package aston.server;

import aston.exception.InternalServerException;
import org.h2.jdbcx.JdbcDataSource;
import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    private final String url;
    private final String user;
    private final String password;
    private final TypeBase type;

    private final PGConnectionPoolDataSource dataSource;
    private final JdbcDataSource dataSourceH2;

    public DataBase(String url, String user, String password, TypeBase type) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.type = type;
        dataSource = new PGConnectionPoolDataSource();
        dataSourceH2 = new JdbcDataSource();
        setUp();
    }

    private void setUp() {
        if (TypeBase.H2 == type) {
            dataSourceH2.setUrl(url);
            dataSourceH2.setUser(user);
            dataSourceH2.setPassword(password);
        } else {
            dataSource.setUrl(url);
            dataSource.setUser(user);
            dataSource.setPassword(password);
        }
    }

    public PooledConnection getConnection() {
        try {
            if (TypeBase.H2 == type) {
                return dataSourceH2.getPooledConnection();
            } else {
                return dataSource.getPooledConnection();
            }
        } catch (SQLException e) {
            throw new InternalServerException("Не удалось получить соединение");
        }
    }


    public void initialize() {
        String query = """
                DROP TABLE IF EXISTS films cascade;
                DROP TABLE IF EXISTS users cascade;
                DROP TABLE IF EXISTS user_film cascade;
                
                CREATE TABLE IF NOT EXISTS films
                (
                    id       bigint      NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    title    varchar(255) NOT NULL
                );
                CREATE TABLE IF NOT EXISTS users
                (
                    id       bigint      NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    name    varchar(255) NOT NULL
                );
                CREATE TABLE IF NOT EXISTS user_film
                (
                    user_id       bigint      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                    film_id    bigint NOT NULL REFERENCES films (id) ON DELETE CASCADE,
                    PRIMARY KEY (user_id, film_id)
                );
                """;
        try (Statement ps = getConnection().getConnection().createStatement()) {
            ps.execute(query);
            System.out.println("База данных инициализирована");
        } catch (SQLException e) {
            throw new InternalServerException("Не удалось создать таблицы");
        }
    }

    public enum TypeBase {
        H2,
        POSTGRES
    }
}
