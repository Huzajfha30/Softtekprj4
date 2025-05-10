package dk.sdu.sm4.common.agv;

public class AGVStatus {
    private int battery;
    private String programName;
    private int state;
    private String timeStamp;

    // Constructor
    public AGVStatus() {}

    // Getters and Setters
    public int getBattery() { return battery; }
    public void setBattery(int battery) { this.battery = battery; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public int getState() { return state; }
    public void setState(int state) { this.state = state; }

    public String getTimeStamp() { return timeStamp; }
    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }


}
