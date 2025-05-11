package dk.sdu.sm4.service;

import dk.sdu.sm4.common.agv.AGVClient;
import dk.sdu.sm4.common.agv.AGVProgramRequest;
import dk.sdu.sm4.common.agv.AGVStatus;
import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProcessFlowService {

    // Services - interfaces
    private final AGVClient agvClient;
    private final IAssemblyStationService assemblyStationService;

    private static final String agvApiUrl = "http://localhost:8082/v1/status";
    private static final int agvIdleState = 1;
    private static final int agvWaitTime = 30000; //venter max 30 sekunder mellem hvert step

    private static final int assemblyProcessingState = 1;
    private static final int assemblyIdleState = 0;
    private static final int assemblyWaitTime = 60000; // venter max 60 sek
    private static final int assemblyProcessId = 12345;

    // State tracking
    private int progress = 0;
    private boolean isRunning = false;
    private String currentStep = "Not started";
    private boolean error = false;
    private String errorMessage = null;
    private Thread processThread;

    public ProcessFlowService(AGVClient agvClient, IAssemblyStationService assemblyStationService) {
        this.agvClient = agvClient;
        this.assemblyStationService = assemblyStationService;
    }


    /**
     * Get the current status of the process flow
     */
    public ProcessFlowStatus getProcessStatus() {
        return new ProcessFlowStatus(currentStep, isRunning, error, errorMessage, progress);
    }

    /**
     * Cancel the currently running process flow
     */
    public void cancelProcessFlow() {
        if (!isProcessRunning()) {
            return;
        }

        processThread.interrupt();
        resetState();
        currentStep = "Cancelled";
    }

    /**
     * Start the process flow execution
     */
    public void runProcessFlow() {
        initializeProcessState();

        processThread = new Thread(this::executeProcessInBackground);
        processThread.start();
    }

    /**
     * Reset the process flow and AGV state
     */
    public void resetProcessFlow() {
        stopRunningProcess();
        resetState();
        resetAGV();
    }

    // PROCESS EXECUTION METHODS

    /**
     * Main background execution method
     */
    private void executeProcessInBackground() {
        try {
            verifyAGVReadiness();
            executeProcessSteps();
            completeProcess();
        } catch (InterruptedException e) {
            handleInterruption();
        } catch (Exception e) {
            handleProcessError(e);
        } finally {
            isRunning = false;
        }
    }

    private void startAssemblyProcess() {
        assemblyStationService.startProcess(assemblyProcessId);
    }

    /**
     * Execute the individual steps of the process
     */
    /**
     * Execute the complete process flow in sequential steps
     */
    private void executeProcessSteps() throws Exception {
        // 1. Move to storage
        updateStep("Moving AGV to storage");
        agvClient.loadProgram("MoveToStorageOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(10);

        // 2. Pick warehouse item
        updateStep("Picking item from warehouse");
        agvClient.loadProgram("PickWarehouseOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(20);

        // 3. Move to assembly
        updateStep("Moving to assembly station");
        agvClient.loadProgram("MoveToAssemblyOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(30);

        // 4. Place at assembly
        updateStep("Placing item at assembly station");
        agvClient.loadProgram("PutAssemblyOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(40);

        // 5. Start assembly process
        updateStep("Starting assembly process");
        startAssemblyProcess();                 //ProcessId 12345
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(50);

        // 6 Wait for assembly to start processing
        updateStep("Waiting for assembly to start");
        waitForAssemblyState(assemblyProcessingState);
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(60);

        // 7 Wait for assembly to complete
        updateStep("Waiting for assembly to complete");
        waitForAssemblyState(assemblyIdleState); //state 0 igen
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(70);

        // 8 Pick assembled item
        updateStep("Picking assembled item");
        agvClient.loadProgram("PickAssemblyOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(80);

        // 9 Return to warehouse
        updateStep("Moving to warehouse");
        agvClient.loadProgram("MoveToStorageOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(90);

        // 10 Store completed item
        updateStep("Placing item in warehouse");
        agvClient.loadProgram("PutWarehouseOperation");
        agvClient.executeProgram();
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(100);
    }


    /**
     * Initialize process state for a new run
     */
    private void initializeProcessState() {
        isRunning = true;
        error = false;
        errorMessage = null;
        currentStep = "Starting process flow";
        progress = 0;
    }

    /**
     * Reset state to initial values
     */
    private void resetState() {
        isRunning = false;
        error = false;
        errorMessage = null;
        currentStep = "Not started";
        progress = 0;
    }

    /**
     * Update the current step description
     */
    private void updateStep(String step) {
        this.currentStep = step;
        this.progress = 0;
        checkForInterruption();
    }

    /**
     * Update the progress percentage
     */
    private void updateProgress(int newProgress) {
        this.progress = Math.max(0, Math.min(100, newProgress));
    }

    /**
     * Mark the process as complete
     */
    private void completeProcess() {
        currentStep = "Process completed";
        progress = 100;
    }

    /**
     * Handle process interruption
     */
    private void handleInterruption() {
        currentStep = "Process cancelled";
        progress = 0;
        Thread.currentThread().interrupt();
    }

    /**
     * Handle process errors
     */
    private void handleProcessError(Exception e) {
        error = true;
        errorMessage = e.getMessage();
        currentStep = "Error: " + e.getMessage();
        System.err.println("Error in process flow: " + e.getMessage());
    }

    /**
     * Check if the process is currently running
     */
    private boolean isProcessRunning() {
        return processThread != null && processThread.isAlive();
    }

    /**
     * Stop any running process
     */
    private void stopRunningProcess() {
        if (!isProcessRunning()) {
            return;
        }

        processThread.interrupt();
        try {
            processThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Check if thread was interrupted
     */
    private void checkForInterruption() {
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("Process was cancelled");
        }
    }

    /**
     * Verify AGV is ready before starting
     */
    private void verifyAGVReadiness() throws Exception {
        updateStep("Checking AGV status");
        AGVStatus status = agvClient.getStatus();
        if (status == null || status.getState() != agvIdleState) {
            throw new RuntimeException("AGV is not in idle state. Please use the reset button first.");
        }
        Thread.sleep(500);
    }

    /**
     * Wait for AGV to return to idle state
     */
    private void waitForAGVToBeIdle(long timeoutMs) throws Exception {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            checkForInterruption();

            AGVStatus status = agvClient.getStatus();
            if (status != null && status.getState() == agvIdleState) {
                return;
            }

            Thread.sleep(500);
        }

        throw new RuntimeException("AGV did not return to idle state within timeout period");
    }

    /**
     * Wait for assembly to reach the target state
     */
    private void waitForAssemblyState(int targetState) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long timeout = assemblyWaitTime;

        while (true) {
            checkForInterruption();

            var status = assemblyStationService.getCurrentStatus();

            if (status != null && status.getState() == targetState) {
                break;
            }

            updateAssemblyProgress(startTime, timeout);
            Thread.sleep(1000);
        }
    }

    /**
     * Update progress based on elapsed time during assembly
     */
    private void updateAssemblyProgress(long startTime, long timeout) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        int calculatedProgress = (int) Math.min(90, (elapsedTime * 100) / timeout);
        updateProgress(calculatedProgress);
    }

    /**
     * Reset the AGV to idle state
     */
    private void resetAGV() {
        boolean resetSuccessful = false;

        try {
            if (isAGVAlreadyIdle()) {
                return;
            }

            resetSuccessful = performAGVForceReset();
        } catch (Exception e) {
            System.err.println("Reset process failed: " + e.getMessage());
        }

        if (!resetSuccessful) {
            System.out.println("Warning: Could not reset AGV to idle state. Manual intervention may be required.");
        }
    }

    /**
     * Check if AGV is already in idle state
     */
    private boolean isAGVAlreadyIdle() {
        var status = agvClient.getStatus();
        System.out.println("Current AGV state before reset: " +
                (status != null ? status.getState() : "unknown"));

        if (status != null && status.getState() == agvIdleState) {
            System.out.println("AGV already idle, no reset needed");
            return true;
        }
        return false;
    }



    /**
     * Abort any running AGV program
     */
    private void abortAGVProgram(RestTemplate restTemplate, HttpHeaders headers) {
        AGVProgramRequest abortRequest = new AGVProgramRequest(null, 3);
        HttpEntity<AGVProgramRequest> abortEntity = new HttpEntity<>(abortRequest, headers);
        restTemplate.exchange(agvApiUrl, HttpMethod.PUT, abortEntity, AGVStatus.class);
    }

    /**
     * Load and execute return home operation
     */
    private void loadAndExecuteReturnHome(RestTemplate restTemplate, HttpHeaders headers) throws InterruptedException {
        // Load return home program
        AGVProgramRequest loadRequest = new AGVProgramRequest("ReturnHomeOperation", 1);
        HttpEntity<AGVProgramRequest> loadEntity = new HttpEntity<>(loadRequest, headers);
        restTemplate.exchange(agvApiUrl, HttpMethod.PUT, loadEntity, AGVStatus.class);
        Thread.sleep(1000);

        // Execute program
        AGVProgramRequest execRequest = new AGVProgramRequest(null, 2);
        HttpEntity<AGVProgramRequest> execEntity = new HttpEntity<>(execRequest, headers);
        restTemplate.exchange(agvApiUrl, HttpMethod.PUT, execEntity, AGVStatus.class);
    }

    /**
     * Verify AGV was successfully reset
     */
    private boolean verifyResetSuccess() {
        AGVStatus status = agvClient.getStatus();
        System.out.println("AGV state after reset attempt: " +
                (status != null ? status.getState() : "unknown"));

        return (status != null && status.getState() == agvIdleState);
    }
    /**
     * Attempt to force reset the AGV
     */
    private boolean performAGVForceReset() {
        try {
            System.out.println("Attempting to force stop AGV program...");
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            abortAGVProgram(restTemplate, headers);
            Thread.sleep(2000);

            loadAndExecuteReturnHome(restTemplate, headers);
            Thread.sleep(2000);

            return verifyResetSuccess();
        } catch (Exception e) {
            System.err.println("Failed to force reset AGV: " + e.getMessage());
            return false;
        }
    }

}