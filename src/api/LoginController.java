package api;
import core.helpers.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import core.Db;
import java.io.OutputStream;
import java.sql.ResultSet;

public class LoginController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                send(exchange, 405, "Only POST allowed");
                return;
            }

            String req = new String(exchange.getRequestBody().readAllBytes());

            JsonObject json = JsonParser.parseString(req).getAsJsonObject();
            
            String email = json.get("email").getAsString();
            String password = json.get("password").getAsString();   

            Db db = new Db();
            ResultSet rs = db.runQuery(
                "SELECT * FROM complaint_users WHERE email='" + email +
                "' AND password='" + password + "'"
            );

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String token = TokenHelper.generateJwt(userId, email);
            
                JsonObject res = new JsonObject();
                res.addProperty("access_token", token);
                ResponseHelper.sendJson(exchange, 200, res.toString());      

            } else {
                ResponseHelper.send(exchange, 401, "Invalid credentials");
            }

            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(HttpExchange exchange, int code, String msg) throws Exception {
        exchange.sendResponseHeaders(code, msg.length());
        OutputStream os = exchange.getResponseBody();
        os.write(msg.getBytes());
        os.close();
    }
}
