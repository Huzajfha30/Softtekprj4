package dk.sdu.sm4.agv.service;

import dk.sdu.sm4.agv.model.AGVProgramRequest;
import dk.sdu.sm4.agv.model.AGVStatus;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AGVService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String AGV_URL = "http://localhost:8082/v1/status";

    public AGVStatus getStatus() {
        ResponseEntity<AGVStatus> response = restTemplate.getForEntity(AGV_URL, AGVStatus.class);
        return response.getBody();
    }

    public AGVStatus loadProgram(String programName) {
        AGVProgramRequest request = new AGVProgramRequest(programName, 1);
        HttpEntity<AGVProgramRequest> entity = new HttpEntity<>(request);
        ResponseEntity<AGVStatus> response = restTemplate.exchange(AGV_URL, HttpMethod.PUT, entity, AGVStatus.class);
        return response.getBody();
    }

    public AGVStatus executeProgram() {
        AGVProgramRequest request = new AGVProgramRequest(null, 2);
        HttpEntity<AGVProgramRequest> entity = new HttpEntity<>(request);
        ResponseEntity<AGVStatus> response = restTemplate.exchange(AGV_URL, HttpMethod.PUT, entity, AGVStatus.class);
        return response.getBody();
    }
}
