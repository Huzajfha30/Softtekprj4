package dk.sdu.sm4.controller;

import dk.sdu.sm4.service.ProcessFlowService;
import dk.sdu.sm4.model.ProcessFlowModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/processflow")
@CrossOrigin(origins = "http://localhost:3000")
public class ProcessFlowController {

    private ProcessFlowService processFlowService;

    @Autowired
    public ProcessFlowController(ProcessFlowService processFlowService) {
        this.processFlowService = processFlowService;
    }


    @PostMapping("/start")
    public ResponseEntity<String> startProcess(@RequestBody Map<String, Integer> payload) {
        try {
            Integer trayId = payload.get("trayId");
            if (trayId != null) {
                processFlowService.setSelectedTrayId(trayId);
            }
            processFlowService.runProcessFlow();
            return ResponseEntity.ok("Process started successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error starting process: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ProcessFlowModel> getProcessStatus() {
        try {
            ProcessFlowModel status = processFlowService.getProcessStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelProcess() {
        try {
            processFlowService.cancelProcessFlow();
            return ResponseEntity.ok("Process cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error cancelling process: " + e.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetProcess() {
        try {
            processFlowService.resetProcessFlow();
            return ResponseEntity.ok("Process reset successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error resetting process: " + e.getMessage());
        }
    }
}