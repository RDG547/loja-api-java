package br.com.entrega.exception;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(409, message);
    }
}
