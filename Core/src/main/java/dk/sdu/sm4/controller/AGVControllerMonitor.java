package dk.sdu.sm4.controller;

import dk.sdu.sm4.common.agv.AGVClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agv")
@CrossOrigin(origins = "http://localhost:3000")
public class AGVControllerMonitor {
    private final AGVClient agvClient;

    @Autowired
    public AGVControllerMonitor(AGVClient agvClient) {
        this.agvClient = agvClient;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        try {
            // Return the current status from your AGV client
            return ResponseEntity.ok(agvClient.getStatus());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}