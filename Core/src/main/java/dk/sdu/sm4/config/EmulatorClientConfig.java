package dk.sdu.sm4.config;

import dk.sdu.sm4.warehouse.IEmulatorService_PortType;
import dk.sdu.sm4.warehouse.IEmulatorService_ServiceLocator;
import org.apache.axis.AxisFault;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.rpc.ServiceException;

@Configuration
public class EmulatorClientConfig {

    @Bean
    public IEmulatorService_ServiceLocator emulatorServiceLocator() {
        return new IEmulatorService_ServiceLocator();
    }

    @Bean
    public IEmulatorService_PortType emulatorServicePort(IEmulatorService_ServiceLocator locator)
            throws AxisFault, ServiceException {
        IEmulatorService_PortType port = locator.getBasicHttpBinding_IEmulatorService();
        return port;
    }

}