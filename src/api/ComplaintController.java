package api;

import core.Db;
import core.helpers.ResponseHelper;
import core.helpers.TokenHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ComplaintController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                ResponseHelper.send(exchange, 405, "Only POST allowed");
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

            // allow only STUDENT
            if (!"STUDENT".equalsIgnoreCase(role)) {
                ResponseHelper.send(exchange, 403, "Access denied: only students can create complaints");
                return;
            }

            // BODY
            String body = new String(exchange.getRequestBody().readAllBytes());
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            String category = json.get("category").getAsString();
            String description = json.get("description").getAsString();
            int isAnon = json.get("is_anonymous").getAsInt();

            // DB INSERT
            Db db = new Db();
            int rows = db.runUpdate(
                "INSERT INTO complaints (user_id, category, description, is_anonymous, status, created_at, updated_at) " +
                "VALUES (" + userId + ", '" + category + "', '" + description + "', " + isAnon + ", 'Pending', NOW(), NOW())"
            );

            if (rows > 0) {
                ResponseHelper.send(exchange, 200, "Complaint created");
            } else {
                ResponseHelper.send(exchange, 500, "Failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                ResponseHelper.send(exchange, 500, "Server error");
            } catch (Exception ignored) {}
        }
    }
}
