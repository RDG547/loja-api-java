package br.com.entrega.exception;

public class MethodNotAllowedException extends ApiException {
    public MethodNotAllowedException(String message) {
        super(405, message);
    }
}
