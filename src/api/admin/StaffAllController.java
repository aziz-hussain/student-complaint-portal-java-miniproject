package api.admin;

import core.Db;
import core.helpers.ResponseHelper;
import core.helpers.TokenHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.sql.ResultSet;

public class StaffAllController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                ResponseHelper.send(exchange, 405, "Only GET allowed");
                return;
            }

            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                ResponseHelper.send(exchange, 401, "Missing token");
                return;
            }

            String token = auth.substring(7);
            JsonObject decoded = TokenHelper.decode(token);

            String role = decoded.get("role").getAsString();

            if (!"ADMIN".equalsIgnoreCase(role)) {
                ResponseHelper.send(exchange, 403, "Access denied");
                return;
            }

            // fetch staff
            Db db = new Db();
            ResultSet rs = db.runQuery(
                "SELECT user_id, name, email FROM complaint_users WHERE role='STAFF'"
            );

            JsonArray arr = new JsonArray();

            while (rs.next()) {
                JsonObject o = new JsonObject();
                o.addProperty("staff_id", rs.getInt("user_id"));
                o.addProperty("name", rs.getString("name"));
                o.addProperty("email", rs.getString("email"));
                arr.add(o);
            }

            ResponseHelper.sendJson(exchange, 200, arr.toString());

        } catch (Exception e) {
            e.printStackTrace();
            try { ResponseHelper.send(exchange, 500, "Server error"); } catch (Exception ignored) {}
        }
    }
}
