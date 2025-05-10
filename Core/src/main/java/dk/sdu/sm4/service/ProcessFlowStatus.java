package dk.sdu.sm4.service;

public class ProcessFlowStatus {
    private String currentStep;
    private boolean running;
    private boolean error;
    private String errorMessage;
    private int progress; // New field to track progress percentage (0-100)

    // Default constructor for Jackson
    public ProcessFlowStatus() {
    }

    public ProcessFlowStatus(String currentStep, boolean running, boolean error, String errorMessage, int progress) {
        this.currentStep = currentStep;
        this.running = running;
        this.error = error;
        this.errorMessage = errorMessage;
        this.progress = progress;
    }

    // Existing getters and setters

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
    // Getters and setters
    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}