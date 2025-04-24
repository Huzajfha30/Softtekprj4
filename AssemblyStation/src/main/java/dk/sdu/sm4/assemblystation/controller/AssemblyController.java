package dk.sdu.sm4.assemblystation.controller;

import dk.sdu.sm4.enums.AssemblyStatus;
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

    // 🔧 Start en assembly proces
    @PostMapping("/start")
    public String start(@RequestParam int processId) {
        boolean success = service.startAssembly(processId);
        return success ? "Assembly process started." : "Failed to start process.";
    }

    // 📊 Hent nuværende status
    @GetMapping("/status")
    public AssemblyStatusModel getStatus() {
        return service.getStatus();
    }

    // ❤️ Hent health-status (true/false)
    @GetMapping("/health")
    public boolean getHealth() {
        return service.getHealthStatus();
    }
}
