package core.helpers;

import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;

public class ResponseHelper {

    public static void send(HttpExchange exchange, int code, String msg) throws Exception {
        exchange.sendResponseHeaders(code, msg.length());
        OutputStream os = exchange.getResponseBody();
        os.write(msg.getBytes());
        os.close();
    }

    public static void sendJson(HttpExchange exchange, int code, String json) throws Exception {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, json.length());
        OutputStream os = exchange.getResponseBody();
        os.write(json.getBytes());
        os.close();
    }
}
