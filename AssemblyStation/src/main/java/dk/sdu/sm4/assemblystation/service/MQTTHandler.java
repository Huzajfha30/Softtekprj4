package dk.sdu.sm4.assemblystation.service;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQTTHandler {
    // håndtere connect, publish, subscribe funktionaliteter. assemblystationService bruger den til at sende beskeder.
    // holder styr på forbindelse

    private MqttClient mqttClient; // dependency fra eclipse.paho
    private final CallbackHandler callbackHandler;

    @Autowired
    public MQTTHandler(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    public void connect(String broker, int port) throws MqttException {
        if (mqttClient != null && mqttClient.isConnected()) {
            System.out.println("Allerede forbundet til MQTT broker.");
            return;
        }

        String clientId = MqttClient.generateClientId();
        mqttClient = new MqttClient("tcp://" + broker + ":" + port, clientId);
        mqttClient.setCallback(callbackHandler);
        mqttClient.connect();

        System.out.println("Forbundet til MQTT broker på " + broker + ":" + port);
    }

    public void publish(String topic, String message) throws MqttException {
        if (mqttClient == null || !mqttClient.isConnected()) {
            throw new MqttException(new Throwable("MQTT-klient er ikke forbundet. PUBLISH METODEN"));
        }
        mqttClient.publish(topic, new MqttMessage(message.getBytes()));
    }

    public void subscribe(String topic) throws MqttException {
        if (mqttClient == null || !mqttClient.isConnected()) {
            throw new MqttException(new Throwable("MQTT-klient er ikke forbundet. SUBSCRIBE METODEN"));
        }
        mqttClient.subscribe(topic);
    }

    public void disconnect() throws MqttException { //bliver relevant med "stop knap"
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.disconnect();
            System.out.println("Forbindelsen til MQTT broker er lukket.");
        }
    }
}
