package br.edu.utfpr.apiEmail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EmailApiApplication {
    public static void main(String[] arguments) {
        SpringApplication.run(EmailApiApplication.class, arguments);
    }
}
