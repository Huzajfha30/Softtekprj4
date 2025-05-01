package dk.sdu.sm4.assemblystation.controller;

import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// http://localhost:8080/swagger-ui/index.html#/assembly-station-controller/testAssemblyStation
@RestController
@RequestMapping("/assembly")
public class AssemblyStationController {
    private final IAssemblyStationService assemblyStationService;

    public AssemblyStationController(IAssemblyStationService assemblyStationService) {
        this.assemblyStationService = assemblyStationService;
    }

    @GetMapping("/test")
    public String testAssemblyStation() {
        try {
            System.out.println("Starter normal process...");
            boolean success = assemblyStationService.startProcess(12345);
            Thread.sleep(2000);

            System.out.println("Starter fejl process...");
            success = assemblyStationService.startProcess(9999);
            Thread.sleep(2000);

            return "Test udført.";
        } catch (Exception e) {
            return "Fejl i test: " + e.getMessage();
        }
    }

    @GetMapping("/status")
    public String getStatus() {
        boolean isAvailable = assemblyStationService.isAvailable();
        boolean isHealthy = assemblyStationService.getHealthStatus();
        return "Station status - Available: " + isAvailable + ", Healthy: " + isHealthy;
    }

    @GetMapping("/checkhealth")
    public String checkHealth() {
        boolean health = assemblyStationService.getHealthStatus();
        return "Assembly Station Health Status: " + health;
    }

}