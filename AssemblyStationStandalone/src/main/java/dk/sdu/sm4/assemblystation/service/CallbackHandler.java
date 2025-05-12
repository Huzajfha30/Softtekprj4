package dk.sdu.sm4.assemblystation.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;
import org.json.JSONObject;
import dk.sdu.sm4.assemblystation.model.AssemblyStatus;



@Component
public class CallbackHandler implements MqttCallback {

    //Gemmer den nyeste status for assemblyStation, som REST-controlleren kan hente
    private static AssemblyStatus latestStatus;

    //Gør status tilgængelig for fx MqttController
    public AssemblyStatus getLatestStatus() {
        return latestStatus;
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Forbindelsen mistet: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        System.out.println("Modtog besked fra '" + topic + "': " + payload);

        if (topic.equals("emulator/status")) {
            try {
                JSONObject json = new JSONObject(payload);

                int lastOperation = json.getInt("LastOperation");
                int currentOperation = json.getInt("CurrentOperation");
                int state = json.getInt("State");

                latestStatus = new AssemblyStatus(lastOperation, currentOperation, state);
                System.out.println("AssemblyStatus opdateret: " + latestStatus);
            } catch (Exception e) {
                System.err.println("Fejl ved parsing af status: " + e.getMessage());
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Kan bruges til at logge/tracke bekræftede leveringer
    }
}