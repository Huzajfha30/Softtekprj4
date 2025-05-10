package dk.sdu.sm4.common.agv;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AGVProgramRequest {

    /** Must match the simulator’s “Program name” field exactly */
    @JsonProperty("program name")
    private String programName;

    /** Must match the simulator’s “State” field exactly */
    @JsonProperty("state")
    private int state;

    public AGVProgramRequest() {}

    public AGVProgramRequest(String programName, int state) {
        this.programName = programName;
        this.state       = state;
    }
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
