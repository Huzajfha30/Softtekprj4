package dk.sdu.sm4.agv2.service;

import dk.sdu.sm4.common.agv.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AGV2Service implements AGVClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String AGV_URL = "http://localhost:8083/v1/status";

    @Override
    public AGVStatus getStatus() {
        return restTemplate.getForObject(AGV_URL, AGVStatus.class);
    }

    @Override
    public AGVStatus loadProgram(String programName) {
        AGVProgramRequest request = new AGVProgramRequest(programName, 1);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AGVProgramRequest> entity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(AGV_URL, HttpMethod.PUT, entity, AGVStatus.class)
                .getBody();
    }

    @Override
    public AGVStatus executeProgram() {
        AGVProgramRequest request = new AGVProgramRequest(null, 2);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AGVProgramRequest> entity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(AGV_URL, HttpMethod.PUT, entity, AGVStatus.class)
                .getBody();
    }
}
