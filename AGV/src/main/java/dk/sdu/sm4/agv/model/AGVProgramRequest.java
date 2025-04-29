package dk.sdu.sm4.agv.model;

public class AGVProgramRequest {
    private String programName;
    private int state;

    public AGVProgramRequest() {}

    public AGVProgramRequest(String programName, int state) {
        this.programName = programName;
        this.state = state;
    }

    // Getters and Setters
    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public int getState() { return state; }
    public void setState(int state) { this.state = state; }
}
