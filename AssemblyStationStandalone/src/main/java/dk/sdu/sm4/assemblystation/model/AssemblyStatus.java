package dk.sdu.sm4.assemblystation.model;

public class AssemblyStatus {
    private int lastOperation;
    private int currentOperation;
    private int state;

    public AssemblyStatus(int lastOperation, int currentOperation, int state) {
        this.lastOperation = lastOperation;
        this.currentOperation = currentOperation;
        this.state = state;
    }

    public int getLastOperation() {
        return lastOperation;
    }

    public int getCurrentOperation() {
        return currentOperation;
    }

    public int getState() {
        return state;
    }

    @Override
    public String toString() {
        return "AssemblyStatus{" +
                "lastOperation=" + lastOperation +
                ", currentOperation=" + currentOperation +
                ", state=" + state +
                '}';
    }
}