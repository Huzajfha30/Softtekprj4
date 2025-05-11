package dk.sdu.sm4.model;

public class ProcessFlowModel { // transfer status information from service to controller - serialized to JSON.
    private String currentStep;
    private boolean running;
    private boolean error;
    private String errorMessage;
    private int progress; // track progress percentage (0-100)

    // Default constructor for Jackson
    public ProcessFlowModel() {
    }

    public boolean isRunning(boolean b) {
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


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }
}