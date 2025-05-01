package dk.sdu.sm4.assemblystation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App { // hej: http://localhost:8080/swagger-ui/index.html#/mqtt-controller/testMqtt
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
