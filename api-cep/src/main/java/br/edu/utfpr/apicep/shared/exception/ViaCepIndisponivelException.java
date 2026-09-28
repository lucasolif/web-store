package br.edu.utfpr.apicep.shared.exception;

public class ViaCepIndisponivelException extends RuntimeException {

    private static final String MESSAGE = "O serviço ViaCEP está temporariamente indisponível.";

    public ViaCepIndisponivelException() {
        super(MESSAGE);
    }

    public ViaCepIndisponivelException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
