package core;
import com.sun.net.httpserver.HttpServer;
import api.HelloController;
import api.LoginController;
import java.net.InetSocketAddress;

public class HttpServerMain {
    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/hello", new HelloController());
        server.createContext("/login", new LoginController());

        server.setExecutor(null);
        server.start();


        System.out.println("Server running at http://localhost:8080");
    }
}
