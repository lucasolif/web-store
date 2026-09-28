package br.edu.utfpr.apicep.cep.api;

import br.edu.utfpr.apicep.cep.api.response.EnderecoCepResponse;
import br.edu.utfpr.apicep.cep.application.FindCepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cep")
public class CepController {

    private final FindCepService findCepService;

    public CepController(FindCepService findCepService) {
        this.findCepService = findCepService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<EnderecoCepResponse> search(@PathVariable String cep) {
        var endereco = findCepService.execute(cep);
        return ResponseEntity.ok(EnderecoCepResponse.from(endereco));
    }
}
