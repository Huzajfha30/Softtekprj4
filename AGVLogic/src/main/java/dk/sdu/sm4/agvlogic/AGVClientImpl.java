package dk.sdu.sm4.agvlogic;

import dk.sdu.sm4.common.agv.AGVClient;
import dk.sdu.sm4.common.agv.AGVProgramRequest;
import dk.sdu.sm4.common.agv.AGVStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class AGVClientImpl implements AGVClient {
    private RestTemplate restTemplate = new RestTemplate();
    private String AGV_URL = "http://localhost:8082/v1/status";

    @Override
    public AGVStatus getStatus() {
        return restTemplate.getForObject(AGV_URL, AGVStatus.class);
    }

    @Override
    public AGVStatus loadProgram(String programName) {
        try {
            AGVProgramRequest request = new AGVProgramRequest(programName, 1);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AGVProgramRequest> entity = new HttpEntity<>(request, headers);
            return restTemplate.exchange(AGV_URL, HttpMethod.PUT, entity, AGVStatus.class).getBody();
        } catch (org.springframework.web.client.HttpClientErrorException.BadRequest e) {
            throw new RuntimeException("AGV is busy. Please wait for current operation to complete or reset the AGV.", e);
        }
    }

    @Override
    public AGVStatus executeProgram() {
        AGVProgramRequest request = new AGVProgramRequest(null, 2);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AGVProgramRequest> entity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(AGV_URL, HttpMethod.PUT, entity, AGVStatus.class).getBody();
    }

    @Override
    public AGVStatus resetAGV() {
        try {
            // Get current status first
            AGVStatus currentStatus = getStatus();
            System.out.println("Current AGV state: " + (currentStatus != null ? currentStatus.getState() : "unknown"));

            if (currentStatus != null && currentStatus.getState() == 1) {
                return currentStatus; // Already in idle state
            }

            // Send empty execution command to attempt reset
            AGVProgramRequest execRequest = new AGVProgramRequest(null, 2);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AGVProgramRequest> execEntity = new HttpEntity<>(execRequest, headers);
            return restTemplate.exchange(AGV_URL, HttpMethod.PUT, execEntity, AGVStatus.class).getBody();
        } catch (Exception e) {
            System.err.println("Failed to reset AGV: " + e.getMessage());
            throw new RuntimeException("AGV could not be reset - might be in an unrecoverable state", e);
        }
    }
}