package dk.sdu.sm4.agv.controller;

import dk.sdu.sm4.common.agv.AGVStatus;
import dk.sdu.sm4.common.agv.AGVClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agv")
@CrossOrigin(origins = "http://localhost:3000")

public class AGVController {

    private final AGVClient agvService;

    public AGVController(AGVClient agvService) {
        this.agvService = agvService;
    }

    @GetMapping("/status")
    public AGVStatus getStatus() {
        return agvService.getStatus();
    }

    @PutMapping("/load/{programName}")
    public AGVStatus loadProgram(@PathVariable String programName) {
        return agvService.loadProgram(programName);
    }

    @PutMapping("/execute")
    public AGVStatus executeProgram() {
        return agvService.executeProgram();
    }
}
