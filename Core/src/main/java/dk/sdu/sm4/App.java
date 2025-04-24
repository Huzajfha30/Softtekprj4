package dk.sdu.sm4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "dk.sdu.sm4.assemblystation")
public class App { //http://localhost:8080/swagger-ui/index.html#/

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
