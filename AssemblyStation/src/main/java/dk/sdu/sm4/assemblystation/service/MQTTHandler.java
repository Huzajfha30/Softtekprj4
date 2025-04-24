package dk.sdu.sm4.assemblystation.service;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

@Service
public class MqttHandler {

    private final CallbackHandler callbackHandler;
    private MqttClient mqttClient;

    public MqttHandler(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    public void connect(String broker, int port) throws MqttException {
        String clientId = MqttClient.generateClientId();
        mqttClient = new MqttClient("tcp://" + broker + ":" + port, clientId);

        mqttClient.setCallback(callbackHandler);
        mqttClient.connect();
        System.out.println("Forbundet til MQTT broker på " + broker + ":" + port);
    }

    public void publish(String topic, String message) throws MqttException {
        mqttClient.publish(topic, new MqttMessage(message.getBytes()));
    }

    public void subscribe(String topic) throws MqttException {
        mqttClient.subscribe(topic);
    }

    public void disconnect() throws MqttException {
        mqttClient.disconnect();
        System.out.println("Forbindelsen til MQTT broker er lukket.");
    }
}
