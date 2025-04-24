package dk.sdu.sm4.models;

import dk.sdu.sm4.enums.AssemblyStatus;


public class AssemblyStatusModel {

    private int lastOperation;
    private int currentOperation; // ← Her manglede du semikolon
    private AssemblyStatus state;
    private String timeStamp;

    // Getters and Setters

    public int getLastOperation() {
        return lastOperation;
    }

    public void setLastOperation(int lastOperation) {
        this.lastOperation = lastOperation;
    }

    public int getCurrentOperation() {
        return currentOperation;
    }

    public void setCurrentOperation(int currentOperation) {
        this.currentOperation = currentOperation;
    }

    public AssemblyStatus getState() {
        return state;
    }

    public void setState(AssemblyStatus state) {
        this.state = state;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
