package br.edu.utfpr.apicep.shared.exception;

public class CepInvalidoException extends RuntimeException {

    public CepInvalidoException() {
        super("CEP inválido. Informe exatamente 8 dígitos, com ou sem hífen.");
    }
}
