package com.revolut.api.test;

import com.revolut.api.test.controller.UserController;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by igor on 2017-05-28.
 */
public class RestServer {
    static {
        System.setProperty("com.sun.jersey.api.json.POJOMappingFeature", "true");
    }

    public static final int SERVER_PORT = 8080;
    private final Server jettyServer;

    public static void main(String[] args) throws Exception {
        RestServer server = new RestServer();
        server.start();
    }

    public RestServer() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        jettyServer = new Server(SERVER_PORT);
        jettyServer.setHandler(context);
        jettyServer.setAttribute(
                "com.sun.jersey.config.property.packages",
                "com.revolut.api.test.model");
        jettyServer.setAttribute(
                "com.sun.jersey.api.json.POJOMappingFeature",
                "true");

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                UserController.class.getCanonicalName());

    }

    public void start() {
        try {
            jettyServer.start();
            jettyServer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jettyServer.destroy();
        }
    }

    public void stop() {
        try {
            jettyServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jettyServer.destroy();
        }
    }
}
