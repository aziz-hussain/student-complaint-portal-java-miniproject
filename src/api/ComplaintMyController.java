package api;

import core.Db;
import core.helpers.ResponseHelper;
import core.helpers.TokenHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.sql.ResultSet;

public class ComplaintMyController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                ResponseHelper.send(exchange, 405, "Only GET allowed");
                return;
            }

            // TOKEN
            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                ResponseHelper.send(exchange, 401, "Missing token");
                return;
            }

            String token = auth.substring(7);
            JsonObject decoded = TokenHelper.decode(token);

            int userId = decoded.get("user_id").getAsInt();
            String role = decoded.get("role").getAsString();

            if (!"STUDENT".equalsIgnoreCase(role)) {
                ResponseHelper.send(exchange, 403, "Access denied");
                return;
            }
           
            Db db = new Db();
            ResultSet rs = db.runQuery(
                "SELECT * FROM complaints WHERE user_id=" + userId + " ORDER BY created_at DESC"
            );

            JsonArray arr = new JsonArray();

            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("complaint_id", rs.getInt("complaint_id"));
                obj.addProperty("category", rs.getString("category"));
                obj.addProperty("description", rs.getString("description"));
                obj.addProperty("is_anonymous", rs.getInt("is_anonymous"));
                obj.addProperty("status", rs.getString("status"));
                obj.addProperty("assigned_to", rs.getString("assigned_to"));
                obj.addProperty("created_at", rs.getString("created_at"));
                obj.addProperty("updated_at", rs.getString("updated_at"));
                arr.add(obj);
            }

            ResponseHelper.sendJson(exchange, 200, arr.toString());

        } catch (Exception e) {
            e.printStackTrace();
            try { ResponseHelper.send(exchange, 500, "Server error"); } catch (Exception ignored) {}
        }
    }
}
