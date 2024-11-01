package aston.server;

import jakarta.servlet.Servlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;

public class Server {
    private final int port;
    private final Tomcat tomcat;
    private Context appContext;

    public Server(Tomcat tomcat, int port) {
        this.tomcat = tomcat;
        this.port = port;
        setUp();
    }

    private void setUp() {
        appContext = tomcat.addContext("", null);
        tomcat.getConnector().setPort(port);
    }

    public void addServlet(String servletName, Servlet servlet, String mappingName) {
        Wrapper newServlet = Tomcat.addServlet(appContext, servletName, servlet);
        newServlet.addMapping(mappingName);
    }

    public void start() throws LifecycleException {
        tomcat.start();
    }
}
