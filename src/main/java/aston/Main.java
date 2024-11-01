package aston;

import aston.server.DataBase;
import aston.server.Server;
import aston.servlets.FilmServlet;
import aston.servlets.UserServlet;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class Main {
    public static void main(String[] args) throws LifecycleException {
        DataBase db = new DataBase(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "12345",
                DataBase.TypeBase.POSTGRES
        );
        db.initialize();

        Server server = new Server(new Tomcat(), 8080);
        server.addServlet("userServlet", new UserServlet(db), "/users/*");
        server.addServlet("filmServlet", new FilmServlet(db), "/films/*");
        server.start();
    }
}
