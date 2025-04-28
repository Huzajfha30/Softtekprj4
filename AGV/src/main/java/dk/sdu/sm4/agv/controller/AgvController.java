package dk.sdu.sm4.agv.controller;

import dk.sdu.sm4.agv.services.AgvServiceImpl;
import dk.sdu.sm4.enums.AGVOperation;
import dk.sdu.sm4.models.AGVStatusModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agv")
public class AgvController {

    private final AgvServiceImpl agvService;

    public AgvController(AgvServiceImpl agvService) {
        this.agvService = agvService;
    }

    @PostMapping("/run")
    public String runOperation(@RequestParam AGVOperation op) {
        boolean success = agvService.performOperation(op);
        return success
                ? "✅ Operation kørt: " + op
                : "❌ Fejl ved udførelse af operation: " + op;
    }

    @GetMapping("/status")
    public AGVStatusModel getStatus() {
        return agvService.getStatus();
    }
}
