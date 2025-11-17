package api.admin;

import core.Db;
import core.helpers.ResponseHelper;
import core.helpers.TokenHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.sql.ResultSet;

public class ComplaintAllController implements HttpHandler {

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

            Db db = new Db();
            ResultSet rs = db.runQuery(
                "SELECT c.*, u.name, u.email " +
                "FROM complaints c " +
                "JOIN complaint_users u ON c.user_id = u.user_id " +
                "ORDER BY c.created_at DESC"
            );

            JsonArray arr = new JsonArray();

            while (rs.next()) {
                JsonObject o = new JsonObject();
                o.addProperty("complaint_id", rs.getInt("complaint_id"));
                o.addProperty("user_id", rs.getInt("user_id"));
                o.addProperty("name", rs.getString("name"));
                o.addProperty("email", rs.getString("email"));
                o.addProperty("category", rs.getString("category"));
                o.addProperty("description", rs.getString("description"));
                o.addProperty("is_anonymous", rs.getInt("is_anonymous"));
                o.addProperty("status", rs.getString("status"));
                o.addProperty("assigned_to", rs.getString("assigned_to"));
                o.addProperty("created_at", rs.getString("created_at"));
                o.addProperty("updated_at", rs.getString("updated_at"));
                arr.add(o);
            }

            ResponseHelper.sendJson(exchange, 200, arr.toString());

        } catch (Exception e) {
            e.printStackTrace();
            try { ResponseHelper.send(exchange, 500, "Server error"); } catch (Exception ignored) {}
        }
    }
}
