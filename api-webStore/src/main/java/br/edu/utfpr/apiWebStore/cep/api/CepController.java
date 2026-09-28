package br.edu.utfpr.apiWebStore.cep.api;

import br.edu.utfpr.apiWebStore.cep.api.response.AddressResponse;
import br.edu.utfpr.apiWebStore.cep.application.CepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cep")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService){
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<AddressResponse> search(@PathVariable String cep) {
        AddressResponse address = cepService.findAddressByCep(cep);

        return ResponseEntity.ok(address);
    }
}
