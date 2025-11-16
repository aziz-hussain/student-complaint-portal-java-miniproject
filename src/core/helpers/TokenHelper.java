package core.helpers;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TokenHelper {

    private static final String SECRET = "kaccha_pappad_pakka_pappad";

    public static String generateJwt(int userId, String email, String role) throws Exception {
        JsonObject header = new JsonObject();
        header.addProperty("alg", "HS256");
        header.addProperty("typ", "JWT");

        String headerBase64 = base64UrlEncode(header.toString().getBytes());

        JsonObject payload = new JsonObject();
        payload.addProperty("user_id", userId);
        payload.addProperty("email", email);
        payload.addProperty("role", role);
        payload.addProperty("ts", System.currentTimeMillis());

        String payloadBase64 = base64UrlEncode(payload.toString().getBytes());

        String msg = headerBase64 + "." + payloadBase64;
        String signature = hmacSha256(msg, SECRET);
        String signatureBase64 = base64UrlEncode(signature.getBytes());

        return headerBase64 + "." + payloadBase64 + "." + signatureBase64;
    }

    // ---------- DECODE TOKEN ----------
    public static JsonObject decode(String token) throws Exception {
        String[] parts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        return JsonParser.parseString(payload).getAsJsonObject();
    }

    private static String hmacSha256(String msg, String secret) throws Exception {
        Mac sha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256.init(keySpec);
        return new String(sha256.doFinal(msg.getBytes()));
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
