package br.edu.utfpr.apiEmail.email.infraestructure;

import br.edu.utfpr.apiEmail.email.domain.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {
}
