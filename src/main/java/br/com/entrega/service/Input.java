package br.com.entrega.service;

import br.com.entrega.exception.ValidationException;

import java.util.Map;

final class Input {
    private Input() {
    }

    static String requiredString(Map<String, Object> data, String field) {
        Object value = data.get(field);
        if (!(value instanceof String text) || text.isBlank()) {
            throw new ValidationException("Campo obrigatorio ausente ou invalido: " + field + ".");
        }
        return text.trim();
    }

    static String optionalString(Map<String, Object> data, String field, String currentValue) {
        if (!data.containsKey(field)) {
            return currentValue;
        }
        Object value = data.get(field);
        if (!(value instanceof String text) || text.isBlank()) {
            throw new ValidationException("Campo invalido: " + field + ".");
        }
        return text.trim();
    }

    static double requiredDouble(Map<String, Object> data, String field) {
        if (!data.containsKey(field)) {
            throw new ValidationException("Campo obrigatorio ausente: " + field + ".");
        }
        return asDouble(data.get(field), field);
    }

    static double optionalDouble(Map<String, Object> data, String field, double currentValue) {
        if (!data.containsKey(field)) {
            return currentValue;
        }
        return asDouble(data.get(field), field);
    }

    static int requiredInt(Map<String, Object> data, String field) {
        if (!data.containsKey(field)) {
            throw new ValidationException("Campo obrigatorio ausente: " + field + ".");
        }
        return asInt(data.get(field), field);
    }

    static int optionalInt(Map<String, Object> data, String field, int currentValue) {
        if (!data.containsKey(field)) {
            return currentValue;
        }
        return asInt(data.get(field), field);
    }

    static boolean optionalBoolean(Map<String, Object> data, String field, boolean currentValue) {
        if (!data.containsKey(field)) {
            return currentValue;
        }

        Object value = data.get(field);
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        throw new ValidationException("Campo booleano invalido: " + field + ".");
    }

    private static double asDouble(Object value, String field) {
        double result;
        if (value instanceof Number number) {
            result = number.doubleValue();
        } else if (value instanceof String text) {
            try {
                result = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                throw new ValidationException("Campo numerico invalido: " + field + ".");
            }
        } else {
            throw new ValidationException("Campo numerico invalido: " + field + ".");
        }

        if (!Double.isFinite(result)) {
            throw new ValidationException("Campo numerico invalido: " + field + ".");
        }
        return result;
    }

    private static int asInt(Object value, String field) {
        if (value instanceof Number number) {
            double doubleValue = number.doubleValue();
            int intValue = number.intValue();
            if (doubleValue != intValue) {
                throw new ValidationException("Campo inteiro invalido: " + field + ".");
            }
            return intValue;
        }

        if (value instanceof String text) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException exception) {
                throw new ValidationException("Campo inteiro invalido: " + field + ".");
            }
        }

        throw new ValidationException("Campo inteiro invalido: " + field + ".");
    }
}
