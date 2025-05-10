package dk.sdu.sm4.commonassemblystation;

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
}