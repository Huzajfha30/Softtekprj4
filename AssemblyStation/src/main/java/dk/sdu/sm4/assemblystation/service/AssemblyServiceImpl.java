package dk.sdu.sm4.assemblystation.service;

import dk.sdu.sm4.interfaces.IAssemblyService;
import dk.sdu.sm4.models.AssemblyStatusModel;
import dk.sdu.sm4.enums.AssemblyStatus;
import org.springframework.stereotype.Service;

@Service
public class AssemblyServiceImpl implements IAssemblyService {

    private final MqttHandler mqttHandler;

    private AssemblyStatusModel latestStatus = new AssemblyStatusModel();

    public AssemblyServiceImpl(MqttHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
    }

    @Override
    public boolean startAssembly(int processId) {
        try {
            String json = "{ \"ProcessID\": " + processId + " }";
            mqttHandler.publish("emulator/operation", json);
            latestStatus.setCurrentOperation(processId);
            latestStatus.setState(AssemblyStatus.EXECUTING);
            latestStatus.setTimeStamp(java.time.LocalTime.now().toString());
            return true;
        } catch (Exception e) {
            System.out.println("Fejl ved startAssembly: " + e.getMessage());
            return false;
        }
    }

    @Override
    public AssemblyStatusModel getStatus() {
        return latestStatus;
    }

    @Override
    public boolean getHealthStatus() {
        return latestStatus.getState() != AssemblyStatus.ERROR;
    }
}
