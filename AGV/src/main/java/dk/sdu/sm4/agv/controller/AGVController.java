package dk.sdu.sm4.agv.controller;

import dk.sdu.sm4.agv.model.AGVStatus;
import dk.sdu.sm4.agv.service.AGVService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agv")
public class AGVController {

    private final AGVService agvService;

    public AGVController(AGVService agvService) {
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
