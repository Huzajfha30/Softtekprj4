package dk.sdu.sm4.config;
import dk.sdu.sm4.common.agv.AGVClient;
import dk.sdu.sm4.commonassemblystation.IAssemblyStationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import dk.sdu.sm4.service.AssemblyStationServiceImpl;
//import dk.sdu.sm4.service.AGVClientImpl;

import java.util.ServiceLoader;


@Configuration
public class ModuleConfig {


    @Bean
    public AGVClient agvClient() {
        return ServiceLoader.load(AGVClient.class)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No AGV client interface implementation found"));
    }

    @Bean
    public IAssemblyStationService assemblyStationService() {
        return ServiceLoader.load(IAssemblyStationService.class)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No assembly station service interface implementation found"));
    }



}