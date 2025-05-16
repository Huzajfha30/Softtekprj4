package dk.sdu.sm4.service;

import dk.sdu.cbse.common.warehouse.IWarehouseService;
import dk.sdu.sm4.common.agv.AGVClient;
import dk.sdu.sm4.common.agv.AGVProgramRequest;
import dk.sdu.sm4.common.agv.AGVStatus;
import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import dk.sdu.sm4.model.ProcessFlowModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProcessFlowService {

    // interfaces
    private final AGVClient agvClient;
    private final IAssemblyStationService assemblyStationService;
    private final IWarehouseService warehouseService;

    private final ProcessFlowModel processFlow = new ProcessFlowModel();

    private static final String agvApiUrl = "http://localhost:8082/v1/status";
    private static final int agvIdleState = 1;
    private static final int agvWaitTime = 30000; //venter max 30 sekunder mellem hvert step

    private static final int assemblyProcessingState = 1;
    private static final int assemblyIdleState = 0;
    private static final int assemblyWaitTime = 60000; // venter max 60 sek
    private static final int assemblyProcessId = 12345;

    private int selectedTrayId = -1; //indikere ingen selection
    private String originalItemName;

    private Thread processThread;

    public ProcessFlowService(AGVClient agvClient, IAssemblyStationService assemblyStationService, IWarehouseService warehouseService) {
        this.agvClient = agvClient;
        this.assemblyStationService = assemblyStationService;
        this.warehouseService = warehouseService;
    }

    public void setSelectedTrayId(int trayId) {
        this.selectedTrayId = trayId;
    }


    public ProcessFlowModel getProcessStatus() {
        return processFlow; // Return the model directly
    }


    public void cancelProcessFlow() {
        if (!isProcessRunning()) {
            return;
        }

        processThread.interrupt();
        resetState();
        processFlow.setCurrentStep("Cancelled");
    }


    public void runProcessFlow() {
        initializeProcessState();

        processThread = new Thread(this::executeProcessInBackground);
        processThread.start();
    }


    public void resetProcessFlow() {
        stopRunningProcess();
        resetState();
        resetAGV();
    }

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
            processFlow.isRunning(false);
        }
    }

    private void startAssemblyProcess() {
        assemblyStationService.startProcess(assemblyProcessId);
    }

    //master method
    private void executeProcessSteps() throws Exception {
        // Log process start
        System.out.println("PROCESS START: Processing item from tray " + selectedTrayId);

        // 1. Move to storage
        System.out.println("STEP 1: Moving AGV to storage");
        updateStep("Moving AGV to storage");
        checkBatteryBeforeStep();
        System.out.println("   Loading MoveToStorageOperation program");
        agvClient.loadProgram("MoveToStorageOperation");
        agvClient.executeProgram();
        System.out.println("   Waiting for AGV to complete move to storage");
        waitForAGVToBeIdle(agvWaitTime);
        updateProgress(10);
        System.out.println("STEP 1: Complete - AGV at storage location");

        //2. PickItem "requester en item fra warehouse"
        System.out.println(" STEP 2: Requesting item from warehouse");
        updateStep("Requesting item from warehouse");
        checkBatteryBeforeStep();
        if (selectedTrayId == -1) {
            System.out.println("ERROR: No warehouse tray selected");
            throw new RuntimeException("No warehouse tray selected. Please select a tray from the warehouse presets.");
        }
        originalItemName = warehouseService.getTrayContent(selectedTrayId);
        System.out.println("   Retrieved item name: '" + originalItemName + "' from tray " + selectedTrayId);
        warehouseService.pickItem(selectedTrayId);
        System.out.println("STEP 2: Complete - Item request sent to warehouse");
        updateProgress(20);

        // 3. Pick warehouse item
        System.out.println("STEP 3: AGV picking item from warehouse");
        updateStep("Picking item from warehouse");
        checkBatteryBeforeStep();
        agvClient.loadProgram("PickWarehouseOperation");
        agvClient.executeProgram();
        System.out.println("   Waiting for AGV to pick item");
        waitForAGVToBeIdle(agvWaitTime);
        System.out.println("STEP 3: Complete - Item picked from warehouse");
        updateProgress(25);

        // 4. Move to assembly
        System.out.println("STEP 4: Moving AGV to assembly station");
        updateStep("Moving to assembly station");
        checkBatteryBeforeStep();
        agvClient.loadProgram("MoveToAssemblyOperation");
        agvClient.executeProgram();
        System.out.println("   Waiting for AGV to arrive at assembly station");
        waitForAGVToBeIdle(agvWaitTime);
        System.out.println("STEP 4: Complete - AGV at assembly station");
        updateProgress(30);

        // 5. Place at assembly
        System.out.println("STEP 5: AGV placing item at assembly station");
        updateStep("Placing item at assembly station");
        checkBatteryBeforeStep();
        agvClient.loadProgram("PutAssemblyOperation");
        agvClient.executeProgram();
        System.out.println("Waiting for AGV to place item");
        waitForAGVToBeIdle(agvWaitTime);
        System.out.println("STEP 5: Complete - Item placed at assembly station");
        updateProgress(40);

        // 6. Start assembly process
        System.out.println("STEP 6: Starting assembly process");
        updateStep("Starting assembly process");
        checkBatteryBeforeStep();
        System.out.println("   Sending start command to assembly station with processId: " + assemblyProcessId);
        startAssemblyProcess();                 //ProcessId 12345
        waitForAGVToBeIdle(agvWaitTime);
        System.out.println("STEP 6: Complete - Assembly process started");
        updateProgress(50);

        // 7 Wait for assembly to start processing
        System.out.println("STEP 7: Waiting for assembly to start processing");
        updateStep("Waiting for assembly to start");
        checkBatteryBeforeStep();
        System.out.println("   Waiting for assembly state to change to PROCESSING (" + assemblyProcessingState + ")");
        waitForAssemblyState(assemblyProcessingState);
        waitForAGVToBeIdle(agvWaitTime);
        System.out.println(" STEP 7: Complete - Assembly is now processing");
        updateProgress(60);

        // 8 Wait for assembly to complete
        System.out.println("STEP 8: Waiting for assembly to complete");
        updateStep("Waiting for assembly to complete");
        checkBatteryBeforeStep();
        System.out.println("   Waiting for assembly state to return to IDLE (" + assemblyIdleState + ")");
        waitForAssemblyState(assemblyIdleState); //state 0 igen
        waitForAGVToBeIdle(agvWaitTime);
        System.out.println("STEP 8: Complete - Assembly finished");
        updateProgress(70);

        // 9 Pick assembled item
        System.out.println("STEP 9: AGV picking assembled item");
        updateStep("Picking assembled item");
        checkBatteryBeforeStep();
        agvClient.loadProgram("PickAssemblyOperation");
        agvClient.executeProgram();
        System.out.println("   Waiting for AGV to pick assembled item");
        waitForAGVToBeIdle(agvWaitTime);
        System.out.println("STEP 9: Complete - Assembled item picked by AGV");
        updateProgress(80);

        // 10 Return to warehouse
        System.out.println("STEP 10: AGV returning to warehouse");
        updateStep("Moving to warehouse");
        checkBatteryBeforeStep();
        agvClient.loadProgram("MoveToStorageOperation");
        agvClient.executeProgram();
        System.out.println("   Waiting for AGV to return to warehouse");
        waitForAGVToBeIdle(agvWaitTime);
        System.out.println("STEP 10: Complete - AGV back at warehouse");
        updateProgress(90);

        //11. Return item to original tray with original name
        System.out.println("STEP 11: Returning assembled item to tray " + selectedTrayId);
        updateStep("Placing assembled item back in tray " + selectedTrayId);
        checkBatteryBeforeStep();
        System.out.println("   Inserting item '" + originalItemName + "' back into tray " + selectedTrayId);
        warehouseService.insertItem(selectedTrayId, originalItemName);
        agvClient.loadProgram("PutWarehouseOperation");
        agvClient.executeProgram();
        System.out.println("   Waiting for AGV to place item in warehouse");
        waitForAGVToBeIdle(agvWaitTime);
        System.out.println("STEP 11: Complete - Item returned to warehouse");
        updateProgress(100);

        System.out.println("PROCESS COMPLETE: Successfully processed item from tray " + selectedTrayId);
    }


    /**
     * Initialize process state for a new run
     */
    private void initializeProcessState() {
        processFlow.setRunning(true);
        processFlow.setError(false);
        processFlow.setErrorMessage(null);
        processFlow.setCurrentStep("Starting process flow");
        processFlow.setProgress(0);
    }


    private void resetState() {
        processFlow.setRunning(false);
        processFlow.setError(false);
        processFlow.setErrorMessage(null);
        processFlow.setCurrentStep("Not started");
        processFlow.setProgress(0);
    }


    private void updateStep(String step) {
        processFlow.setCurrentStep(step);
        checkForInterruption();
    }


    private void updateProgress(int newProgress) {
        processFlow.setProgress(Math.max(0, Math.min(100, newProgress)));
    }


    private void completeProcess() {
        processFlow.setCurrentStep("Process completed");
        processFlow.setProgress(100);
    }


    private void handleInterruption() {
        processFlow.setCurrentStep("Process cancelled");
        processFlow.setProgress(0);
        Thread.currentThread().interrupt();
    }


    private void handleProcessError(Exception e) {
        processFlow.setError(true);
        processFlow.setErrorMessage(e.getMessage());
        processFlow.setCurrentStep("Error: " + e.getMessage());
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


    private void updateAssemblyProgress(long startTime, long timeout) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        int calculatedProgress = (int) Math.min(90, (elapsedTime * 100) / timeout);
        updateProgress(calculatedProgress);
    }


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




    private void abortAGVProgram(RestTemplate restTemplate, HttpHeaders headers) {
        AGVProgramRequest abortRequest = new AGVProgramRequest(null, 3);
        HttpEntity<AGVProgramRequest> abortEntity = new HttpEntity<>(abortRequest, headers);
        restTemplate.exchange(agvApiUrl, HttpMethod.PUT, abortEntity, AGVStatus.class);
    }


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


    private boolean verifyResetSuccess() {
        AGVStatus status = agvClient.getStatus();
        System.out.println("AGV state after reset attempt: " +
                (status != null ? status.getState() : "unknown"));

        return (status != null && status.getState() == agvIdleState);
    }

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
// updated

    private void checkBatteryBeforeStep() throws Exception {
        AGVStatus status = agvClient.getStatus();

        if (status.getBattery() <= 10) {
            System.out.println("🔋 Battery low (" + status.getBattery() + "%). Sending AGV to charger...");
            processFlow.setCurrentStep("AGV battery low – charging...");

            agvClient.loadProgram("MoveToChargerOperation");
            agvClient.executeProgram();

            while (agvClient.getStatus().getBattery() <= 30) {
                Thread.sleep(5000);
            }

            System.out.println("🔌 Charging complete. Resuming process.");
        }
    }

}