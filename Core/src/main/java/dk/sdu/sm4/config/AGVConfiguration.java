package dk.sdu.sm4.config;

import dk.sdu.sm4.common.agv.AGVClient;
import dk.sdu.sm4.common.agv.AGVProgramRequest;
import dk.sdu.sm4.common.agv.AGVStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AGVConfiguration {
    @Bean
    public AGVClient agvClient() {
        return new AGVClientImpl();
    }

    private static class AGVClientImpl implements AGVClient {
        private final RestTemplate restTemplate = new RestTemplate();

        //private final String AGV_URL = "http://st4-agv/v1/status";
        private final String AGV_URL = "http://localhost:8082/v1/status";

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
                // More descriptive error
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

                // If already idle (state 1), just return current status
                if (currentStatus != null && currentStatus.getState() == 1) {
                    return currentStatus; // Already idle, nothing to do
                }

                // Only try to send empty execution if AGV is not in idle state
                if (currentStatus != null && currentStatus.getState() != 1) {
                    // Try to directly send execute request with no program
                    AGVProgramRequest execRequest = new AGVProgramRequest(null, 2);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<AGVProgramRequest> execEntity = new HttpEntity<>(execRequest, headers);

                    try {
                        restTemplate.exchange(AGV_URL, HttpMethod.PUT, execEntity, AGVStatus.class);
                        System.out.println("Sent empty execution command to attempt reset");
                    } catch (Exception e) {
                        // Ignore errors here - just trying to clear state
                    }

                    // Wait briefly after first attempt
                    Thread.sleep(500);

                    // Check status again
                    currentStatus = getStatus();
                    System.out.println("After empty execute, AGV state: " +
                            (currentStatus != null ? currentStatus.getState() : "unknown"));
                }

                // If we're in state 1 (idle) now, just return
                if (currentStatus != null && currentStatus.getState() == 1) {
                    return currentStatus; // Now idle
                }

                // Try to load a home operation
                AGVProgramRequest loadRequest = new AGVProgramRequest("ReturnHomeOperation", 1);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<AGVProgramRequest> loadEntity = new HttpEntity<>(loadRequest, headers);
                AGVStatus loadStatus = restTemplate.exchange(AGV_URL, HttpMethod.PUT, loadEntity, AGVStatus.class).getBody();
                System.out.println("Loaded ReturnHomeOperation");

                // Then execute it
                AGVProgramRequest execRequest = new AGVProgramRequest(null, 2);
                HttpEntity<AGVProgramRequest> execEntity = new HttpEntity<>(execRequest, headers);
                return restTemplate.exchange(AGV_URL, HttpMethod.PUT, execEntity, AGVStatus.class).getBody();
            } catch (Exception e) {
                System.err.println("Failed to reset AGV: " + e.getMessage());
                throw new RuntimeException("AGV could not be reset - might be in an unrecoverable state", e);
            }
        }
    }
}

