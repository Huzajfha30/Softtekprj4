package dk.sdu.sm4.assemblystation.service;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

@Component
public class CallbackHandler implements MqttCallback { //håndtere alle indkommende mqtt beskeder
    //logger besekder fra emulator/status topic. uden denne vil ikke kunne se status/heartbeat messages fra emulatoren

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Forbindelsen mistet: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Modtog besked fra '" + topic + "': " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Kan bruges til at logge/tracke bekræftede leveringer
    }
}
