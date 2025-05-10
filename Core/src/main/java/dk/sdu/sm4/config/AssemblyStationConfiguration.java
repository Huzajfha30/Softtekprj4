package dk.sdu.sm4.config;


import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssemblyStationConfiguration {

    @Bean
    public IAssemblyStationService assemblyStationService() {
        // Create implementation in the Core module
        return new AssemblyStationClientImpl();
    }

    private static class AssemblyStationClientImpl implements IAssemblyStationService {
        private MqttClient mqttClient;
        private dk.sdu.sm4.commonassemblystation.AssemblyStatus latestStatus;

        public AssemblyStationClientImpl() {
            try {
                // Initialize MQTT connection
                String clientId = MqttClient.generateClientId();
                //mqttClient = new MqttClient("tcp://mqtt:1883", clientId, new MemoryPersistence());
                mqttClient = new MqttClient("tcp://localhost:1883", clientId, new MemoryPersistence());
                MqttConnectOptions connectOptions = new MqttConnectOptions();
                connectOptions.setCleanSession(true);
                mqttClient.connect(connectOptions);

                // Set up callback for status updates
                mqttClient.setCallback(new org.eclipse.paho.client.mqttv3.MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        System.out.println("Connection to MQTT broker lost: " + cause.getMessage());
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) {
                        if ("emulator/status".equals(topic)) {
                            try {
                                String payload = new String(message.getPayload());
                                org.json.JSONObject json = new org.json.JSONObject(payload);

                                int lastOperation = json.getInt("LastOperation");
                                int currentOperation = json.getInt("CurrentOperation");
                                int state = json.getInt("State");

                                latestStatus = new dk.sdu.sm4.commonassemblystation.AssemblyStatus(
                                        lastOperation, currentOperation, state);

                                System.out.println("Assembly status updated: " + state);
                            } catch (Exception e) {
                                System.err.println("Error parsing status: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                        // Not needed for this implementation
                    }
                });

                // Subscribe to status topic
                mqttClient.subscribe("emulator/status");
                System.out.println("Connected to MQTT broker and subscribed to status");

            } catch (MqttException e) {
                System.err.println("Error initializing MQTT client: " + e.getMessage());
            }
        }

        @Override
        public boolean startProcess(int processId) {
            try {
                String payload = "{ \"ProcessID\": " + processId + " }";
                mqttClient.publish("emulator/operation", new MqttMessage(payload.getBytes()));
                return true;
            } catch (MqttException e) {
                System.err.println("Error starting process: " + e.getMessage());
                return false;
            }
        }

        @Override
        public boolean isAvailable() {
            return mqttClient != null && mqttClient.isConnected();
        }

        @Override
        public boolean getHealthStatus() {
            try {
                mqttClient.publish("emulator/checkhealth", new MqttMessage("".getBytes()));
                return mqttClient != null && mqttClient.isConnected();
            } catch (MqttException e) {
                return false;
            }
        }

        @Override
        public dk.sdu.sm4.commonassemblystation.AssemblyStatus getCurrentStatus() {
            return latestStatus;
        }
    }
}