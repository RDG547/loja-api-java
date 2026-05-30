package br.com.entrega.api;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class Response {
    private Response() {
    }

    public static void json(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] bytes = Json.stringify(body).getBytes(StandardCharsets.UTF_8);
        addDefaultHeaders(exchange.getResponseHeaders());
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    public static void empty(HttpExchange exchange, int statusCode) throws IOException {
        addDefaultHeaders(exchange.getResponseHeaders());
        exchange.sendResponseHeaders(statusCode, -1);
        exchange.close();
    }

    private static void addDefaultHeaders(Headers headers) {
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
    }
}
