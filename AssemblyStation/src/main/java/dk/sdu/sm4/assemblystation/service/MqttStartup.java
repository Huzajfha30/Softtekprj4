package dk.sdu.sm4.assemblystation.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class MqttStartup {

    private final MQTTHandler mqttHandler;

    public MqttStartup(MQTTHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
    }

    @PostConstruct
    public void init() {
        try {
            mqttHandler.connect("localhost", 1883); // docker settings
            mqttHandler.subscribe("emulator/status"); //vigtig for at se heartbeat ved execution
        } catch (Exception e) {
            System.out.println("Fejl ved forbindelse til MQTT: " + e.getMessage());
        }
    }
}
