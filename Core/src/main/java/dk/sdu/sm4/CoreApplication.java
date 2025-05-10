package dk.sdu.sm4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"dk.sdu.sm4", "dk.sdu.sm4.controller"})
public class CoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }
}