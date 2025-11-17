package api.admin;

import core.Db;
import core.helpers.ResponseHelper;
import core.helpers.TokenHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.sql.ResultSet;
// *  /complaints/{id}/assign
public class ComplaintAssignController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!exchange.getRequestMethod().equalsIgnoreCase("PUT")) {
                ResponseHelper.send(exchange, 405, "Only PUT allowed");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            int complaintId = Integer.parseInt(parts[3]);  

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

            String body = new String(exchange.getRequestBody().readAllBytes());
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            int staffId = json.get("staff_id").getAsInt();

            Db db = new Db();

            ResultSet rsStaff = db.runQuery(
                "SELECT user_id FROM complaint_users WHERE user_id=" + staffId + " AND role='STAFF'"
            );

            if (!rsStaff.next()) {
                ResponseHelper.send(exchange, 400, "Invalid staff ID");
                return;
            }

            int rows = db.runUpdate(
                "UPDATE complaints SET assigned_to=" + staffId + ", status='Assigned', updated_at=NOW() " +
                "WHERE complaint_id=" + complaintId
            );

            if (rows > 0) {
                ResponseHelper.send(exchange, 200, "Assigned");
            } else {
                ResponseHelper.send(exchange, 404, "Complaint not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            try { ResponseHelper.send(exchange, 500, "Server error"); } catch (Exception ignored) {}
        }
    }
}
