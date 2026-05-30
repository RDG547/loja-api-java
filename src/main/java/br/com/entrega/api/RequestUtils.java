package br.com.entrega.api;

import br.com.entrega.exception.NotFoundException;
import br.com.entrega.exception.ValidationException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RequestUtils {
    private RequestUtils() {
    }

    public static Map<String, Object> bodyAsObject(HttpExchange exchange) throws IOException {
        try (InputStream body = exchange.getRequestBody()) {
            String content = new String(body.readAllBytes(), StandardCharsets.UTF_8);
            return Json.parseObject(content);
        }
    }

    public static Long extractId(String path, String basePath) {
        String suffix = path.substring(basePath.length());
        if (suffix.isBlank() || "/".equals(suffix)) {
            return null;
        }

        if (!suffix.startsWith("/")) {
            throw new NotFoundException("Rota nao encontrada.");
        }

        String rawId = suffix.substring(1);
        if (rawId.contains("/") || rawId.isBlank()) {
            throw new NotFoundException("Rota nao encontrada.");
        }

        try {
            long id = Long.parseLong(rawId);
            if (id < 1) {
                throw new ValidationException("O id deve ser maior que zero.");
            }
            return id;
        } catch (NumberFormatException exception) {
            throw new ValidationException("O id deve ser numerico.");
        }
    }

    public static Map<String, String> queryParams(HttpExchange exchange) {
        Map<String, String> params = new LinkedHashMap<>();
        String query = exchange.getRequestURI().getRawQuery();
        if (query == null || query.isBlank()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            if (pair.isBlank()) {
                continue;
            }

            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";
            params.put(key, value);
        }
        return params;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
