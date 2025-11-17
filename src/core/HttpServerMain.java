package core;
import com.sun.net.httpserver.HttpServer;

import api.admin.ComplaintAllController;
import api.admin.StaffAllController;
import api.auth.LoginController;
import api.health.HelloController;
import api.student.ComplaintController;
import api.student.ComplaintMyController;

import java.net.InetSocketAddress;

public class HttpServerMain {
    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/hello", new HelloController());
        server.createContext("/login", new LoginController());
        server.createContext("/complaints", new ComplaintController());
        server.createContext("/complaints/my", new ComplaintMyController());
        server.createContext("/complaints/all", new ComplaintAllController());
        server.createContext("/staff/all", new StaffAllController());





        server.setExecutor(null);
        server.start();


        System.out.println("Server running at http://localhost:8080");
    }
}
