package br.edu.utfpr.apicep.shared.exception;

public class CepNaoEncontradoException extends RuntimeException {

    public CepNaoEncontradoException(String cep) {
        super("Nenhum endereço foi encontrado para o CEP " + cep + ".");
    }
}
