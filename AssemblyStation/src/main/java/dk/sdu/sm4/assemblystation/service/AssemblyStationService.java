package dk.sdu.sm4.assemblystation.service;


import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssemblyStationService implements IAssemblyStationService {
    private final MQTTHandler mqttHandler;

    public AssemblyStationService(MQTTHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
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

}