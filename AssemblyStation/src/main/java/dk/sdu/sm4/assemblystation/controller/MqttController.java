package dk.sdu.sm4.assemblystation.controller;


import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    private final MqttHandler mqttHandler;

    @Autowired
    public MqttController(MqttHandler mqttHandler) {
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
            return "Abonneret på topic: " + topic;
        } catch (MqttException e) {
            return "Fejl ved abonnement: " + e.getMessage();
        }
    }

    @PostMapping("/disconnect")
    public String disconnect() {
        try {
            mqttHandler.disconnect();
            return "Forbindelsen lukket";
        } catch (MqttException e) {
            return "Fejl ved lukning: " + e.getMessage();
        }
    }
}
