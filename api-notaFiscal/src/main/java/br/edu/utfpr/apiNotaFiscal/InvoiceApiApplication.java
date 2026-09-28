package br.edu.utfpr.apiNotaFiscal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class InvoiceApiApplication {
    public static void main(String[] arguments) {
        SpringApplication.run(InvoiceApiApplication.class, arguments);
    }
}
