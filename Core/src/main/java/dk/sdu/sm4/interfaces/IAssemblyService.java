package dk.sdu.sm4.interfaces;

import dk.sdu.sm4.models.AssemblyStatusModel;

public interface IAssemblyService {
    boolean startAssembly(int processId);
    AssemblyStatusModel getStatus();
    boolean getHealthStatus();
}
