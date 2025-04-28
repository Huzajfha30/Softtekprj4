package dk.sdu.sm4.agv.client;

import dk.sdu.sm4.models.AGVProgramRequest;
import dk.sdu.sm4.models.AGVStatusModel;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AgvSimulatorClient {

    private final String BASE_URL = "http://localhost:8082/v1/status";
    private final RestTemplate restTemplate = new RestTemplate();

    // 🔹 1. Load operation (State = 1)
    public boolean loadOperation(String programName) {
        AGVProgramRequest request = new AGVProgramRequest();
        request.setProgramName(programName);
        request.setState(1);

        return sendPut(request);
    }

    // 🔹 2. Execute loaded operation (State = 2)
    public boolean executeLoadedProgram() {
        AGVProgramRequest request = new AGVProgramRequest();
        request.setState(2); // Trigger execution

        return sendPut(request);
    }

    private boolean sendPut(AGVProgramRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<AGVProgramRequest> entity = new HttpEntity<>(request, headers);
            restTemplate.exchange(BASE_URL, HttpMethod.PUT, entity, String.class);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Fejl ved PUT til simulator: " + e.getMessage());
            return false;
        }
    }

    // 🔍 3. Hent status fra simulator (GET)
    public AGVStatusModel getStatus() {
        try {
            return restTemplate.getForObject(BASE_URL, AGVStatusModel.class);
        } catch (Exception e) {
            System.out.println("❌ Fejl ved GET status: " + e.getMessage());
            return null;
        }
    }
}
