package dk.sdu.sm4.models;

public class AGVProgramRequest {
    private String programName;
    private int state;

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
