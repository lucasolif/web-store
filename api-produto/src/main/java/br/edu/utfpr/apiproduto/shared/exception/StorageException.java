package br.edu.utfpr.apiproduto.shared.exception;

public class StorageException extends RuntimeException {

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
