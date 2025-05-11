package dk.sdu.sm4.config;

import dk.sdu.sm4.commonassemblystation.AssemblyStatus;
import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class AssemblyStationClientImpl implements IAssemblyStationService {


    private static String MqttBrokerURL = "tcp://localhost:1883";
    private static String statusTopic = "emulator/status";
    private static String operationTopic = "emulator/operation";
    private static String healthCheckTopic = "emulator/checkhealth";

    private MqttClient mqttClient;
    private AssemblyStatus latestStatus;

    /** Constructor - initializes MQTT connection  */
    public AssemblyStationClientImpl() {
        initializeMqttClient();
    }

    /** Initialize the MQTT client connection  */
    private void initializeMqttClient() {
        try {
            createMqttClient();
            connectMqttClient();
            setupCallbacks();
            subscribeToTopics();
        } catch (MqttException e) {
            System.err.println("Error initializing MQTT client: " + e.getMessage());
        }
    }

    /** Create a new MQTT client instance  */
    private void createMqttClient() throws MqttException {
        String clientId = MqttClient.generateClientId();
        mqttClient = new MqttClient(MqttBrokerURL, clientId, new MemoryPersistence());
    }

    /** Connect the MQTT client to the broker  */
    private void connectMqttClient() throws MqttException {
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        mqttClient.connect(connectOptions);
    }

    /** Set up callback handlers for MQTT events  */
    private void setupCallbacks() throws MqttException {
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                handleConnectionLost(cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                handleIncomingMessage(topic, message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Not needed for this implementation
            }
        });
    }

    /** Subscribe to required MQTT topics  */
    private void subscribeToTopics() throws MqttException {
        mqttClient.subscribe(statusTopic);
        System.out.println("Connected to MQTT broker and subscribed to status");
    }

    /** Handle lost connections to the MQTT broker */
    private void handleConnectionLost(Throwable cause) {
        System.out.println("Connection to MQTT broker lost: " + cause.getMessage());
    }

    /** Process incoming MQTT messages */
    private void handleIncomingMessage(String topic, MqttMessage message) {
        if (statusTopic.equals(topic)) {
            processStatusMessage(message);
        }
    }

    /** Process an assembly status message */
    private void processStatusMessage(MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            JSONObject json = new JSONObject(payload);
            updateAssemblyStatus(json);
        } catch (Exception e) {
            System.err.println("Error parsing status: " + e.getMessage());
        }
    }

    /** Update the assembly status from JSON data  */
    private void updateAssemblyStatus(JSONObject json) {
        int lastOperation = json.getInt("LastOperation");
        int currentOperation = json.getInt("CurrentOperation");
        int state = json.getInt("State");

        latestStatus = new AssemblyStatus(lastOperation, currentOperation, state);
        System.out.println("Assembly status code: " + state);
    }

    /** Publish a message to a specified topic  */
    private boolean publishMessage(String topic, String payload) {
        try {
            mqttClient.publish(topic, new MqttMessage(payload.getBytes()));
            return true;
        } catch (MqttException e) {
            System.err.println("Error publishing message to " + topic + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean startProcess(int processId) {
        String payload = "{ \"ProcessID\": " + processId + " }";
        return publishMessage(operationTopic, payload);
    }

    @Override
    public boolean isAvailable() {
        return mqttClient != null && mqttClient.isConnected();
    }

    @Override
    public boolean getHealthStatus() {
        if (!isAvailable()) {
            return false;
        }
        return publishMessage(healthCheckTopic, "");
    }

    @Override
    public AssemblyStatus getCurrentStatus() {
        return latestStatus;
    }
}