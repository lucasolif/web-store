package br.edu.utfpr.apiDelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DeliveryApiApplication {
    public static void main(String[] arguments) {
        SpringApplication.run(DeliveryApiApplication.class, arguments);
    }

}
