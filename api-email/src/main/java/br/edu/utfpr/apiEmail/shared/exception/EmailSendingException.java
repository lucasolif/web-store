package br.edu.utfpr.apiEmail.shared.exception;

public class EmailSendingException extends RuntimeException {

    private static final String CODE = "EMAIL_SEND_FAILURE";

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return CODE;
    }
}
