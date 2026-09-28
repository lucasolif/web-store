package br.edu.utfpr.apicep.viaCep.application;

import br.edu.utfpr.apicep.cep.domain.EnderecoCep;

import java.util.Optional;

public interface CepProvider {
    Optional<EnderecoCep> buscarPorCep(String cep);
}
