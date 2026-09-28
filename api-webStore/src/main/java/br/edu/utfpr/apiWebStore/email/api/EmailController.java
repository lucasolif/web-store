package br.edu.utfpr.apiWebStore.email.api;

import br.edu.utfpr.apiWebStore.email.api.request.EmailSendRequest;
import br.edu.utfpr.apiWebStore.email.application.SendEmailService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emails")
@Validated
public class EmailController {

    private final SendEmailService sendEmailService;

    public EmailController(SendEmailService sendEmailService) {
        this.sendEmailService = sendEmailService;
    }

    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendEmail(
            @Valid @RequestBody EmailSendRequest request
    ) {
        sendEmailService.send(request);
        return ResponseEntity.noContent().build();
    }
}
