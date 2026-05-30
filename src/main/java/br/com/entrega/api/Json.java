package br.com.entrega.api;

import br.com.entrega.exception.ValidationException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Json {
    private Json() {
    }

    public static Map<String, Object> parseObject(String json) {
        if (json == null || json.isBlank()) {
            return new LinkedHashMap<>();
        }

        Object parsed = parse(json);
        if (!(parsed instanceof Map<?, ?> parsedMap)) {
            throw new ValidationException("O corpo da requisicao deve ser um objeto JSON.");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : parsedMap.entrySet()) {
            if (!(entry.getKey() instanceof String key)) {
                throw new ValidationException("JSON com chave invalida.");
            }
            result.put(key, entry.getValue());
        }
        return result;
    }

    public static Object parse(String json) {
        Parser parser = new Parser(json);
        Object value = parser.parseValue();
        parser.skipWhitespace();
        if (!parser.isEnd()) {
            throw new ValidationException("JSON invalido perto da posicao " + parser.position() + ".");
        }
        return value;
    }

    public static String stringify(Object value) {
        StringBuilder builder = new StringBuilder();
        writeValue(builder, value);
        return builder.toString();
    }

    private static void writeValue(StringBuilder builder, Object value) {
        if (value == null) {
            builder.append("null");
            return;
        }

        if (value instanceof String stringValue) {
            writeString(builder, stringValue);
            return;
        }

        if (value instanceof Character characterValue) {
            writeString(builder, characterValue.toString());
            return;
        }

        if (value instanceof Number || value instanceof Boolean) {
            builder.append(value);
            return;
        }

        if (value instanceof Map<?, ?> mapValue) {
            writeMap(builder, mapValue);
            return;
        }

        if (value instanceof Iterable<?> iterableValue) {
            writeIterable(builder, iterableValue.iterator());
            return;
        }

        if (value.getClass().isArray()) {
            writeArray(builder, value);
            return;
        }

        writeString(builder, value.toString());
    }

    private static void writeMap(StringBuilder builder, Map<?, ?> mapValue) {
        builder.append('{');
        Iterator<? extends Map.Entry<?, ?>> iterator = mapValue.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> entry = iterator.next();
            writeString(builder, String.valueOf(entry.getKey()));
            builder.append(':');
            writeValue(builder, entry.getValue());
            if (iterator.hasNext()) {
                builder.append(',');
            }
        }
        builder.append('}');
    }

    private static void writeIterable(StringBuilder builder, Iterator<?> iterator) {
        builder.append('[');
        while (iterator.hasNext()) {
            writeValue(builder, iterator.next());
            if (iterator.hasNext()) {
                builder.append(',');
            }
        }
        builder.append(']');
    }

    private static void writeArray(StringBuilder builder, Object arrayValue) {
        builder.append('[');
        int length = Array.getLength(arrayValue);
        for (int index = 0; index < length; index++) {
            writeValue(builder, Array.get(arrayValue, index));
            if (index + 1 < length) {
                builder.append(',');
            }
        }
        builder.append(']');
    }

    private static void writeString(StringBuilder builder, String value) {
        builder.append('"');
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            switch (current) {
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                case '\b' -> builder.append("\\b");
                case '\f' -> builder.append("\\f");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                default -> {
                    if (current < 32) {
                        builder.append(String.format("\\u%04x", (int) current));
                    } else {
                        builder.append(current);
                    }
                }
            }
        }
        builder.append('"');
    }

    private static final class Parser {
        private final String json;
        private int index;

        private Parser(String json) {
            this.json = json == null ? "" : json;
        }

        private Object parseValue() {
            skipWhitespace();
            if (isEnd()) {
                throw new ValidationException("JSON vazio.");
            }

            char current = peek();
            return switch (current) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't' -> parseLiteral("true", Boolean.TRUE);
                case 'f' -> parseLiteral("false", Boolean.FALSE);
                case 'n' -> parseLiteral("null", null);
                default -> {
                    if (current == '-' || Character.isDigit(current)) {
                        yield parseNumber();
                    }
                    throw new ValidationException("JSON invalido perto da posicao " + index + ".");
                }
            };
        }

        private Map<String, Object> parseObject() {
            expect('{');
            Map<String, Object> object = new LinkedHashMap<>();
            skipWhitespace();
            if (consumeIf('}')) {
                return object;
            }

            while (true) {
                skipWhitespace();
                if (peek() != '"') {
                    throw new ValidationException("Objeto JSON deve possuir chaves em string.");
                }
                String key = parseString();
                skipWhitespace();
                expect(':');
                object.put(key, parseValue());
                skipWhitespace();
                if (consumeIf('}')) {
                    return object;
                }
                expect(',');
            }
        }

        private List<Object> parseArray() {
            expect('[');
            List<Object> array = new ArrayList<>();
            skipWhitespace();
            if (consumeIf(']')) {
                return array;
            }

            while (true) {
                array.add(parseValue());
                skipWhitespace();
                if (consumeIf(']')) {
                    return array;
                }
                expect(',');
            }
        }

        private String parseString() {
            expect('"');
            StringBuilder builder = new StringBuilder();

            while (!isEnd()) {
                char current = json.charAt(index++);
                if (current == '"') {
                    return builder.toString();
                }

                if (current != '\\') {
                    builder.append(current);
                    continue;
                }

                if (isEnd()) {
                    throw new ValidationException("Escape JSON incompleto.");
                }

                char escaped = json.charAt(index++);
                switch (escaped) {
                    case '"' -> builder.append('"');
                    case '\\' -> builder.append('\\');
                    case '/' -> builder.append('/');
                    case 'b' -> builder.append('\b');
                    case 'f' -> builder.append('\f');
                    case 'n' -> builder.append('\n');
                    case 'r' -> builder.append('\r');
                    case 't' -> builder.append('\t');
                    case 'u' -> builder.append(parseUnicode());
                    default -> throw new ValidationException("Escape JSON invalido: \\" + escaped + ".");
                }
            }

            throw new ValidationException("String JSON nao finalizada.");
        }

        private char parseUnicode() {
            if (index + 4 > json.length()) {
                throw new ValidationException("Unicode JSON incompleto.");
            }

            String hex = json.substring(index, index + 4);
            index += 4;
            try {
                return (char) Integer.parseInt(hex, 16);
            } catch (NumberFormatException exception) {
                throw new ValidationException("Unicode JSON invalido.");
            }
        }

        private Object parseNumber() {
            int start = index;
            consumeIf('-');
            consumeDigits();

            boolean decimal = false;
            if (consumeIf('.')) {
                decimal = true;
                consumeDigits();
            }

            if (consumeIf('e') || consumeIf('E')) {
                decimal = true;
                if (!consumeIf('+')) {
                    consumeIf('-');
                }
                consumeDigits();
            }

            String numberText = json.substring(start, index);
            try {
                if (decimal) {
                    return Double.parseDouble(numberText);
                }
                return Long.parseLong(numberText);
            } catch (NumberFormatException exception) {
                throw new ValidationException("Numero JSON invalido.");
            }
        }

        private void consumeDigits() {
            int start = index;
            while (!isEnd() && Character.isDigit(peek())) {
                index++;
            }
            if (start == index) {
                throw new ValidationException("Numero JSON invalido.");
            }
        }

        private Object parseLiteral(String literal, Object value) {
            if (!json.startsWith(literal, index)) {
                throw new ValidationException("Literal JSON invalido.");
            }
            index += literal.length();
            return value;
        }

        private void expect(char expected) {
            skipWhitespace();
            if (isEnd() || json.charAt(index) != expected) {
                throw new ValidationException("Esperado '" + expected + "' perto da posicao " + index + ".");
            }
            index++;
        }

        private boolean consumeIf(char expected) {
            if (!isEnd() && json.charAt(index) == expected) {
                index++;
                return true;
            }
            return false;
        }

        private char peek() {
            if (isEnd()) {
                throw new ValidationException("JSON finalizado inesperadamente.");
            }
            return json.charAt(index);
        }

        private void skipWhitespace() {
            while (!isEnd() && Character.isWhitespace(json.charAt(index))) {
                index++;
            }
        }

        private boolean isEnd() {
            return index >= json.length();
        }

        private int position() {
            return index;
        }
    }
}
