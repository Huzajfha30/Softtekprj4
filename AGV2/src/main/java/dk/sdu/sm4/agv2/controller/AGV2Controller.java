package dk.sdu.sm4.agv2.controller;

import dk.sdu.sm4.common.agv.AGVStatus;
import dk.sdu.sm4.common.agv.AGVClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agv2")
@CrossOrigin(origins = "http://localhost:3000")

public class AGV2Controller {

    private final AGVClient agv2Service;

    public AGV2Controller(AGVClient agv2Service) {
        this.agv2Service = agv2Service;
    }

    @GetMapping("/status")
    public AGVStatus getStatus() {
        return agv2Service.getStatus();
    }

    @PutMapping("/load/{programName}")
    public AGVStatus loadProgram(@PathVariable String programName) {
        return agv2Service.loadProgram(programName);
    }

    @PutMapping("/execute")
    public AGVStatus executeProgram() {
        return agv2Service.executeProgram();
    }
}
