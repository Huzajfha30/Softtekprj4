package dk.sdu.sm4.models;

import dk.sdu.sm4.enums.AGVState;
import dk.sdu.sm4.enums.AGVOperation;

public class AGVStatusModel {
    private int battery;
    private AGVOperation programName;
    private AGVState state;
    private String timeStamp;

    // Getters & Setters
    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public AGVOperation getProgramName() {
        return programName;
    }

    public void setProgramName(AGVOperation programName) {
        this.programName = programName;
    }

    public AGVState getState() {
        return state;
    }

    public void setState(AGVState state) {
        this.state = state;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
