package dk.sdu.sm4.interfaces;

import dk.sdu.sm4.models.AssemblyStatusModel;

public interface IAssemblyService {

    boolean startAssembly(int processId);
    AssemblyStatusModel getStatus();
    boolean getHealthStatus();

    boolean validateTray(int trayId);
    boolean completeAssembly(int trayId);
    void resetStation();
    boolean simulateError(String reason);
    boolean scheduleAssembly(int processId, String timeIso8601);
}
