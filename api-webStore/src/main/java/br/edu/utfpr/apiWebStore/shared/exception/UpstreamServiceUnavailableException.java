package br.edu.utfpr.apiWebStore.shared.exception;

public class UpstreamServiceUnavailableException extends RuntimeException {

    public UpstreamServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
