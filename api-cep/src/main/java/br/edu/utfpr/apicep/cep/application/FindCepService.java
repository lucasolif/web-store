package br.edu.utfpr.apicep.cep.application;

import br.edu.utfpr.apicep.viaCep.application.CepProvider;
import br.edu.utfpr.apicep.cep.domain.EnderecoCep;
import br.edu.utfpr.apicep.shared.exception.CepInvalidoException;
import br.edu.utfpr.apicep.shared.exception.CepNaoEncontradoException;
import org.springframework.stereotype.Service;

@Service
public class FindCepService {

    private static final String FORMATO_CEP = "\\d{5}-?\\d{3}";
    private final CepProvider cepProvider;

    public FindCepService(CepProvider cepProvider) {
        this.cepProvider = cepProvider;
    }

    public EnderecoCep execute(String cepInformado) {
        var cepNormalizado = normalizar(cepInformado);
        return cepProvider.buscarPorCep(cepNormalizado).orElseThrow(() -> new CepNaoEncontradoException(cepNormalizado));
    }

    private String normalizar(String cepInformado) {
        if (cepInformado == null || !cepInformado.strip().matches(FORMATO_CEP)) {
            throw new CepInvalidoException();
        }

        return cepInformado.strip().replace("-", "");
    }
}
