package aston;

import aston.server.Server;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    private Tomcat tomcat = new Tomcat();
    private Server server = new Server(tomcat, 8081);

    @Test
    public void serverSetup() {
        assertEquals(8081, tomcat.getConnector().getPort());
    }
}