package dk.sdu.sm4.agv.services;

import dk.sdu.sm4.agv.client.AgvSimulatorClient;
import dk.sdu.sm4.enums.AGVOperation;
import dk.sdu.sm4.models.AGVStatusModel;
import org.springframework.stereotype.Service;

@Service
public class AgvServiceImpl {

    private final AgvSimulatorClient simulatorClient;

    public AgvServiceImpl(AgvSimulatorClient simulatorClient) {
        this.simulatorClient = simulatorClient;
    }

    public boolean performOperation(AGVOperation operation) {
        System.out.println("🔧 Udfører operation: " + operation.name());

        boolean loaded = simulatorClient.loadOperation(operation.name());
        if (!loaded) {
            System.out.println("❌ Kunne ikke loade program: " + operation.name());
            return false;
        }

        boolean executed = simulatorClient.executeLoadedProgram();
        if (!executed) {
            System.out.println("❌ Kunne ikke eksekvere programmet.");
            return false;
        }

        System.out.println("✅ Program eksekveret: " + operation.name());
        return true;
    }

    public AGVStatusModel getStatus() {
        return simulatorClient.getStatus();
    }
}
