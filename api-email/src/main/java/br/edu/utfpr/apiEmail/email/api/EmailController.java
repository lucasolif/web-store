package br.edu.utfpr.apiEmail.email.api;

import br.edu.utfpr.apiEmail.email.api.request.EmailSendRequest;
import br.edu.utfpr.apiEmail.email.application.SendEmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    public EmailController(SendEmailService sendEmailService){
        this.sendEmailService = sendEmailService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@Valid @RequestBody EmailSendRequest request) {
        sendEmailService.sendingEmail(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
