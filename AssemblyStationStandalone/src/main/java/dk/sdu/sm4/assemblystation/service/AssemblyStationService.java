package dk.sdu.sm4.assemblystation.service;


import dk.sdu.sm4.assemblystation.model.AssemblyStatus;
import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

@Service
public class AssemblyStationService implements IAssemblyStationService {
    private final MQTTHandler mqttHandler;
    private final CallbackHandler callbackHandler;

    public AssemblyStationService(MQTTHandler mqttHandler, CallbackHandler callbackHandler) {
        this.mqttHandler = mqttHandler;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean startProcess(int processId) {
        try {
            String payload = "{ \"ProcessID\": " + processId + " }";
            mqttHandler.publish("emulator/operation", payload);
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        return mqttHandler != null && mqttHandler.isConnected();
    }

    @Override
    public boolean getHealthStatus() {
        try {
            mqttHandler.publish("emulator/checkhealth", "");
            return mqttHandler != null && mqttHandler.isConnected();
        } catch (MqttException e) {
            return false;
        }
    }

    @Override
    public dk.sdu.sm4.commonassemblystation.AssemblyStatus getCurrentStatus() {
        // Get status from the CallbackHandler
        dk.sdu.sm4.assemblystation.model.AssemblyStatus localStatus = callbackHandler.getLatestStatus();

        if (localStatus == null) {
            return null;
        }

        // Convert to the interface's AssemblyStatus class
        return new dk.sdu.sm4.commonassemblystation.AssemblyStatus(
                localStatus.getLastOperation(),
                localStatus.getCurrentOperation(),
                localStatus.getState()
        );

    }
}