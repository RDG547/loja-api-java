package br.com.entrega.exception;

public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(400, message);
    }
}
