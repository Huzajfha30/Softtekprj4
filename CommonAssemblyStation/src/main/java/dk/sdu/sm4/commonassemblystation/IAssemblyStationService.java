package dk.sdu.sm4.commonassemblystation;

public interface IAssemblyStationService {
    boolean startProcess(int processId);

    boolean getHealthStatus();

    boolean isAvailable();
}
