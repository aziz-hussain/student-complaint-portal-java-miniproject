package core.helpers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import com.google.gson.JsonObject;

public class TokenHelper {

    private static final String SECRET = "my_super_secret_key_123";

    public static String generateJwt(int userId, String email) throws Exception {

        // -------- HEADER --------
        JsonObject header = new JsonObject();
        header.addProperty("alg", "HS256");
        header.addProperty("typ", "JWT");

        String headerJson = header.toString();
        String headerBase64 = base64UrlEncode(headerJson.getBytes());

        // -------- PAYLOAD --------
        JsonObject payload = new JsonObject();
        payload.addProperty("user_id", userId);
        payload.addProperty("email", email);
        payload.addProperty("ts", System.currentTimeMillis());

        String payloadJson = payload.toString();
        String payloadBase64 = base64UrlEncode(payloadJson.getBytes());

        // -------- SIGNATURE --------
        String msg = headerBase64 + "." + payloadBase64;
        String signature = hmacSha256(msg, SECRET);
        String signatureBase64 = base64UrlEncode(signature.getBytes());

        // -------- FINAL JWT --------
        return headerBase64 + "." + payloadBase64 + "." + signatureBase64;
    }

    private static String hmacSha256(String msg, String secret) throws Exception {
        Mac sha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256.init(keySpec);
        byte[] hash = sha256.doFinal(msg.getBytes());
        return new String(hash);
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
