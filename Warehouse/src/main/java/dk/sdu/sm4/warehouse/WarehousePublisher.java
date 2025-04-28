package dk.sdu.sm4.warehouse;

import dk.sdu.sm4.warehouse.service.WarehouseServiceImpl;


import jakarta.xml.ws.Endpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class WarehousePublisher {

    @Bean
    public Endpoint publishWarehouseService() {
        // Publicerer Warehouse SOAP service på http://localhost:8081/Service.asmx
        return Endpoint.publish("http://localhost:9090/Service.asmx", new WarehouseServiceImpl());
    }
}
