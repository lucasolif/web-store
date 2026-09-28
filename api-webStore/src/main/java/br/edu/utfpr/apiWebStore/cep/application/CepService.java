package br.edu.utfpr.apiWebStore.cep.application;

import br.edu.utfpr.apiWebStore.cep.api.response.AddressResponse;
import br.edu.utfpr.apiWebStore.cep.integration.CepClient;
import org.springframework.stereotype.Service;

@Service
public class CepService {

    private final CepClient cepClient;

    public CepService(CepClient cepClient){
        this.cepClient = cepClient;
    }

    public AddressResponse findAddressByCep(String cep) {
        return cepClient.findAddressByCep(cep);
    }
}
