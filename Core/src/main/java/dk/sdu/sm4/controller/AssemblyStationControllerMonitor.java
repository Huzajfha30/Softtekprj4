package dk.sdu.sm4.controller;

import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/assembly")
@CrossOrigin(origins = "http://localhost:3000")
public class AssemblyStationControllerMonitor {

    private final IAssemblyStationService assemblyStationService;

    @Autowired
    public AssemblyStationControllerMonitor(IAssemblyStationService assemblyStationService) {
        this.assemblyStationService = assemblyStationService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        try {
            var status = assemblyStationService.getCurrentStatus();
            assemblyStationService.getHealthStatus();

            if (status == null) {
                return ResponseEntity.ok(Map.of(
                        "state", -1,
                        "stateDescription", "Not connected",
                        "currentOperation", -1,
                        "lastOperation", -1
                ));
            }

            String stateDescription = getStateDescription(status.getState());

            Map<String, Object> enrichedStatus = Map.of(
                    "state", status.getState(),
                    "stateDescription", stateDescription,
                    "currentOperation", status.getCurrentOperation(),
                    "lastOperation", status.getLastOperation(),
                    "inUse", status.getState() != 0 // 0 = idle, anything else means it's busy
            );

            return ResponseEntity.ok(enrichedStatus);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> getHealth() {
        try {
            boolean isHealthy = assemblyStationService.getHealthStatus();
            boolean isConnected = assemblyStationService.isAvailable();

            // Get current status to check if station is idle
            var status = assemblyStationService.getCurrentStatus();
            boolean isIdle = (status != null && status.getState() == 0);

            var healthStatus = Map.of(
                    "healthy", isHealthy,
                    "connected", isConnected,
                    "available", isIdle,  // Only available if in idle state
                    "state", status != null ? status.getState() : -1,
                    "stateDescription", status != null ? getStateDescription(status.getState()) : "Unknown"
            );

            return ResponseEntity.ok(healthStatus);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // Helper method to convert numeric states to readable descriptions
    private String getStateDescription(int state) {
        return switch(state) {
            case 0 -> "Idle";
            case 1 -> "Processing";
            case 2 -> "Error";
            default -> "Unknown State";
        };
    }
}