package dk.sdu.sm4.assemblystation.controller;



import dk.sdu.sm4.interfaces.IAssemblyService;
import dk.sdu.sm4.models.AssemblyStatusModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assembly")
public class AssemblyController {

    private final IAssemblyService service;

    public AssemblyController(IAssemblyService service) {
        this.service = service;
    }

    // ⭐ Start en assembly-proces
    @PostMapping("/start")
    public String start(@RequestParam int processId) {
        return service.startAssembly(processId)
                ? "Assembly started for processId " + processId
                : "Failed to start assembly.";
    }

    // 📊 Hent status
    @GetMapping("/status")
    public AssemblyStatusModel getStatus() {
        return service.getStatus();
    }

    // ❤️ Health-check
    @GetMapping("/health")
    public boolean getHealth() {
        return service.getHealthStatus();
    }

    // ✅ Valider bakke
    @PostMapping("/validate")
    public boolean validateTray(@RequestParam int trayId) {
        return service.validateTray(trayId);
    }

    // 🏁 Afslut assembly
    @PostMapping("/complete")
    public boolean complete(@RequestParam int trayId) {
        return service.completeAssembly(trayId);
    }

    // 🔄 Nulstil station
    @PostMapping("/reset")
    public String reset() {
        service.resetStation();
        return "Assembly station has been reset.";
    }

    // ❌ Simulér fejl
    @PostMapping("/simulate-error")
    public String simulateError(@RequestParam String reason) {
        service.simulateError(reason);
        return "Error simulated: " + reason;
    }

    // 📅 Planlæg assembly
    @PostMapping("/schedule")
    public String schedule(@RequestParam int processId,
                           @RequestParam String time) {
        boolean success = service.scheduleAssembly(processId, time);
        return success
                ? "Assembly scheduled for " + time
                : "Failed to schedule assembly.";
    }
}
