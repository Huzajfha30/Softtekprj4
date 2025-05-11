package dk.sdu.sm4.config;
import dk.sdu.sm4.common.agv.AGVClient;
import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import dk.sdu.sm4.service.AGVClientImpl;
import dk.sdu.sm4.service.AssemblyStationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class ModuleConfig {

    @Bean
    public AGVClient agvClient() {
        return new AGVClientImpl();
    }

    @Bean
    public IAssemblyStationService assemblyStationService() {
        return new AssemblyStationServiceImpl();
    }

}