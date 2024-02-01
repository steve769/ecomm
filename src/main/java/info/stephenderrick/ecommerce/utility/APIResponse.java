package info.stephenderrick.ecommerce.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class APIResponse {
    public static ResponseEntity<Map<String, Object>> genericResponse(String status, int statusCode, Object message, HttpStatus HTTP_STATUS){
        Map<String, Object> response = new HashMap<>();

        response.put("status", status);
        response.put("statusCode", statusCode);
        response.put("message", message);

        return ResponseEntity.status(HTTP_STATUS).body(response);

    }
}
