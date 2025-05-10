package dk.sdu.sm4.commonassemblystation;

public interface IAssemblyStationService {
    boolean startProcess(int processId); //fx id: 12345 som virker eller 9999 for error testing

    boolean getHealthStatus();

    boolean isAvailable();

    AssemblyStatus getCurrentStatus();
}
