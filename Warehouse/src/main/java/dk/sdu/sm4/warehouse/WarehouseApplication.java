/* package dk.sdu.sm4.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class WarehouseApplication {
    public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);
        try {
            IEmulatorService_ServiceLocator serviceLocator = new IEmulatorService_ServiceLocator();

            IEmulatorService_PortType port = serviceLocator.getBasicHttpBinding_IEmulatorService();

            String result = port.getInventory();

            System.out.println("Service response: " + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/