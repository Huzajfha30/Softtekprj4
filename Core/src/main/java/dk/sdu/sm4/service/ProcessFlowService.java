package dk.sdu.sm4.service;

import dk.sdu.sm4.common.agv.AGVClient;
import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import org.springframework.stereotype.Service;

@Service
public class ProcessFlowService {

    private final AGVClient agvClient;
    private IAssemblyStationService assemblyStationService;
    private int progress = 0;

    // Status tracking fields
    private boolean isRunning = false;
    private String currentStep = "Not started";
    private boolean error = false;
    private String errorMessage = null;
    private Thread processThread;

    public ProcessFlowService(AGVClient agvClient, IAssemblyStationService assemblyStationService) {
        this.agvClient = agvClient;
        this.assemblyStationService = assemblyStationService;
    }

    public ProcessFlowStatus getProcessStatus() {
        return new ProcessFlowStatus(currentStep, isRunning, error, errorMessage, progress);
    }

    public void cancelProcessFlow() {
        if (processThread != null && processThread.isAlive()) {
            processThread.interrupt();
            isRunning = false;
            currentStep = "Cancelled";
            progress = 0;
        }
    }

    public void runProcessFlow() {
        // Reset status
        isRunning = true;
        error = false;
        errorMessage = null;
        currentStep = "Starting process flow";
        progress = 0;

        // Run in background thread
        processThread = new Thread(() -> {
            try {
                // Remove the automatic reset at the beginning of the process
                // since you have a separate reset button

                // Check if AGV is ready
                updateStep("Checking AGV status");
                var status = agvClient.getStatus();
                if (status == null || status.getState() != 1) {
                    throw new RuntimeException("AGV is not in idle state. Please use the reset button first.");
                }
                Thread.sleep(500); // Short pause to ensure AGV is ready

                executeProcessSteps();
                currentStep = "Process completed";
                progress = 100;
            } catch (InterruptedException e) {
                currentStep = "Process cancelled";
                progress = 0;
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                error = true;
                errorMessage = e.getMessage();
                currentStep = "Error: " + e.getMessage();
                System.err.println("Error in process flow: " + e.getMessage());
            } finally {
                isRunning = false;
            }
        });

        processThread.start();
    }
    // Add this helper method to wait for AGV to return to idle state
    private void waitForAGVToBeIdle(long timeoutMs) throws Exception {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Process was cancelled");
            }

            var status = agvClient.getStatus();
            if (status != null && status.getState() == 1) { // 1 = idle
                return; // AGV is idle, we can proceed
            }

            // Sleep before polling again to avoid flooding the AGV API
            Thread.sleep(500);
        }

        // If we get here, we timed out
        throw new RuntimeException("AGV did not return to idle state within timeout period");
    }

    // The actual process flow implementation
    private void executeProcessSteps() throws Exception {
        // 1.
        updateStep("Moving AGV to storage");
        agvClient.loadProgram("MoveToStorageOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(30000); // Wait up to 30 seconds for AGV to finish
        updateProgress(10);

        // 2.
        updateStep("Picking item from warehouse");
        agvClient.loadProgram("PickWarehouseOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(30000);
        updateProgress(20);
        // 3.
        updateStep("Moving to assembly station");
        agvClient.loadProgram("MoveToAssemblyOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(30000);
        updateProgress(30);

        // 4.
        updateStep("Placing item at assembly station");
        agvClient.loadProgram("PutAssemblyOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(30000);
        updateProgress(40);

        // 5. Start assembly process using service interface
        updateStep("Starting assembly process");
        startAssemblyProcess();
        waitForAGVToBeIdle(30000);
        updateProgress(50);

        // 6. Wait for assembly states
        updateStep("Waiting for assembly to start");
        waitForAssemblyState(1);
        waitForAGVToBeIdle(30000);
        updateProgress(60);

        updateStep("Waiting for assembly to complete");
        waitForAssemblyState(0);
        waitForAGVToBeIdle(30000);
        updateProgress(70);

        // 7.
        updateStep("Picking assembled item");
        agvClient.loadProgram("PickAssemblyOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(30000);
        updateProgress(80);

        // 8.
        updateStep("Moving to warehouse");
        agvClient.loadProgram("MoveToStorageOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(30000);
        updateProgress(90);

        // 9.
        updateStep("Placing item in warehouse");
        agvClient.loadProgram("PutWarehouseOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(30000);
        updateProgress(100);
    }

    // Start assembly process using service interface
    private void startAssemblyProcess() {
        assemblyStationService.startProcess(12345);
    }

    // Helper method to update current step
    private void updateStep(String step) {
        this.currentStep = step;
        // Reset progress for new step
        this.progress = 0;
        // Check for thread interruption
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("Process was cancelled");
        }
    }

    // Helper method to update progress
    private void updateProgress(int newProgress) {
        this.progress = Math.max(0, Math.min(100, newProgress));
    }

    // Wait for assembly to reach specific state
    private void waitForAssemblyState(int targetState) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long timeout = 60000; // 60 seconds timeout

        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Process was cancelled");
            }

            // Get the latest status from the service interface
            var status = assemblyStationService.getCurrentStatus();

            if (status != null && status.getState() == targetState) {
                break;
            }

            // Calculate elapsed time and update progress
            long elapsedTime = System.currentTimeMillis() - startTime;
            int calculatedProgress = (int) Math.min(90, (elapsedTime * 100) / timeout);
            updateProgress(calculatedProgress);

            Thread.sleep(1000); // Wait 1 second before polling again
        }
    }

    public void resetProcessFlow() {
        // Cancel any running process first
        if (processThread != null && processThread.isAlive()) {
            processThread.interrupt();
            try {
                processThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Reset all status fields to initial values
        isRunning = false;
        error = false;
        errorMessage = null;
        currentStep = "Not started";
        progress = 0;

        // Try to reset the AGV with a more aggressive approach
        boolean resetSuccessful = false;

        try {
            // First check current state
            var status = agvClient.getStatus();
            System.out.println("Current AGV state before reset: " +
                    (status != null ? status.getState() : "unknown"));

            // If already idle, we're done
            if (status != null && status.getState() == 1) {
                System.out.println("AGV already idle, no reset needed");
                return;
            }

            // Try direct call to the AGV API to force reset
            // This is a more direct approach than the regular client methods
            try {
                System.out.println("Attempting to force stop AGV program...");
                // Use RestTemplate directly for more control
                var restTemplate = new org.springframework.web.client.RestTemplate();
                var headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

                // First try to abort any running program
                var abortRequest = new dk.sdu.sm4.common.agv.AGVProgramRequest(null, 3); // Assuming 3 is abort code
                var abortEntity = new org.springframework.http.HttpEntity<>(abortRequest, headers);
                restTemplate.exchange("http://localhost:8082/v1/status",
                        org.springframework.http.HttpMethod.PUT,
                        abortEntity,
                        dk.sdu.sm4.common.agv.AGVStatus.class);

                Thread.sleep(2000);

                // Then try to load and execute return home
                var loadRequest = new dk.sdu.sm4.common.agv.AGVProgramRequest("ReturnHomeOperation", 1);
                var loadEntity = new org.springframework.http.HttpEntity<>(loadRequest, headers);
                restTemplate.exchange("http://localhost:8082/v1/status",
                        org.springframework.http.HttpMethod.PUT,
                        loadEntity,
                        dk.sdu.sm4.common.agv.AGVStatus.class);

                Thread.sleep(1000);

                var execRequest = new dk.sdu.sm4.common.agv.AGVProgramRequest(null, 2);
                var execEntity = new org.springframework.http.HttpEntity<>(execRequest, headers);
                restTemplate.exchange("http://localhost:8082/v1/status",
                        org.springframework.http.HttpMethod.PUT,
                        execEntity,
                        dk.sdu.sm4.common.agv.AGVStatus.class);

                // Check if we're reset
                Thread.sleep(2000);
                status = agvClient.getStatus();
                System.out.println("AGV state after reset attempt: " +
                        (status != null ? status.getState() : "unknown"));

                resetSuccessful = (status != null && status.getState() == 1);
            } catch (Exception e) {
                System.err.println("Failed to force reset AGV: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Reset process failed: " + e.getMessage());
        }

        if (!resetSuccessful) {
            System.out.println("Warning: Could not reset AGV to idle state. Manual intervention may be required.");
        }
    }
}
