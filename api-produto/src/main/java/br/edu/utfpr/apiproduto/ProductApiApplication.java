package br.edu.utfpr.apiproduto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ProductApiApplication {
    public static void main(String[] arguments) {
        SpringApplication.run(ProductApiApplication.class, arguments);
    }
}
