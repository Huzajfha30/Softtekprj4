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

    @Override
    public boolean validateTray(int trayId) {
        // Basic logic for now, later can check with warehouse
        System.out.println("Validating tray ID: " + trayId);
        return trayId > 0; // dummy validation
    }

    @Override
    public boolean completeAssembly(int trayId) {
        System.out.println("Assembly completed for tray ID: " + trayId);
        latestStatus.setState(AssemblyStatus.IDLE);
        latestStatus.setLastOperation(latestStatus.getCurrentOperation());
        return true;
    }

    @Override
    public void resetStation() {
        System.out.println("Resetting assembly station to IDLE");
        latestStatus = new AssemblyStatusModel();
        latestStatus.setState(AssemblyStatus.IDLE);
    }

    @Override
    public boolean simulateError(String reason) {
        System.out.println("Simulating error: " + reason);
        latestStatus.setState(AssemblyStatus.ERROR);
        latestStatus.setTimeStamp(java.time.LocalTime.now().toString());
        return true;
    }

    @Override
    public boolean scheduleAssembly(int processId, String timeIso8601) {
        System.out.println("Scheduled assembly for process: " + processId + " at " + timeIso8601);
        // You could store scheduled operations later in a queue
        return true;
    }
}
