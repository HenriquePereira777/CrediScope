package br.com.crediscope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CrediScopeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrediScopeApplication.class, args);
    }
}
