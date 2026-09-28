package br.edu.utfpr.apiWebStore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WebStoreApiApplication {
    public static void main(String[] arguments) {
        SpringApplication.run(WebStoreApiApplication.class, arguments);
    }
}
