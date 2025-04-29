package dk.sdu.sm4.assemblystation.controller;


import dk.sdu.sm4.assemblystation.service.MQTTHandler;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    private final MQTTHandler mqttHandler;

    @Autowired
    public MqttController(MQTTHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
    }

    @PostMapping("/connect")
    public String connect(@RequestParam(defaultValue = "localhost") String broker,
                          @RequestParam(defaultValue = "1883") int port) {
        try {
            mqttHandler.connect(broker, port);
            return "Forbundet til broker på " + broker + ":" + port;
        } catch (MqttException e) {
            return "Fejl ved forbindelse: " + e.getMessage();
        }
    }

    @PostMapping("/publish")
    public String publish(@RequestParam String topic, @RequestParam String message) {
        try {
            mqttHandler.publish(topic, message);
            return "Sendt: " + message + " til topic: " + topic;
        } catch (MqttException e) {
            return "Fejl ved sending: " + e.getMessage();
        }
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String topic) {
        try {
            mqttHandler.subscribe(topic);
            return "subscriber på topic: " + topic;
        } catch (MqttException e) {
            return "Fejl ved abonnement: " + e.getMessage();
        }
    }

    @PostMapping("/disconnect") //muligvis useless
    public String disconnect() {
        try {
            mqttHandler.disconnect();
            return "Forbindelsen lukket";
        } catch (MqttException e) {
            return "Fejl ved lukning: " + e.getMessage();
        }
    }
    @GetMapping("/test-mqtt")
    public String testMqtt() {
        try {
            mqttHandler.connect("localhost", 1883);

            System.out.println("Starter process w/ ID 12345...");
            mqttHandler.publish("emulator/operation", "{ \"ProcessID\": 12345 }");
            Thread.sleep(2000);

            System.out.println("Starter fejl process w/ ID 9999...");
            mqttHandler.publish("emulator/operation", "{ \"ProcessID\": 9999 }");
            Thread.sleep(2000);

            return "MQTT-test udført.";
        } catch (Exception e) {
            return "Fejl i MQTT-test: " + e.getMessage();
        }
    }

}
