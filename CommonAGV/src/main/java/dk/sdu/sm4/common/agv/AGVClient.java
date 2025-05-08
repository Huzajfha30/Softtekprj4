package dk.sdu.sm4.common.agv;

public interface AGVClient {
    AGVStatus getStatus();
    AGVStatus loadProgram(String programName);
    AGVStatus executeProgram();
}
