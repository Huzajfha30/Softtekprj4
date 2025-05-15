package dk.sdu.sm4.warehouse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.sdu.cbse.common.warehouse.IWarehouseService;
import dk.sdu.sm4.warehouse.IEmulatorService_PortType;
import dk.sdu.sm4.warehouse.IEmulatorService_ServiceLocator;
//import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

public class WarehouseService implements IWarehouseService {

    private final IEmulatorService_ServiceLocator serviceLocator;

    private final IEmulatorService_PortType port;

    private volatile boolean stopRequested = false;
    public WarehouseService(IEmulatorService_ServiceLocator serviceLocator, IEmulatorService_PortType port) throws ServiceException {
        this.serviceLocator = serviceLocator;
        this.port = port;
    }


    private int parseTrayIdWithSpace(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode inventory = root.get("Inventory");

            for (JsonNode tray : inventory) {
                int id = tray.get("Id").asInt();
                String content = tray.get("Content").asText();
                if (content == null || content.isEmpty()) {
                    return id;
                }
            }

            return -1; // ingen ledige trays
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    @Override
    public int findTrayWithSpace() throws RemoteException {
        String xml = getInventory(); // brug eksisterende metode
        // TODO: Parse xml og find en ledig tray
        // Fx: returner 3 hvis tray 3 er tom
        return parseTrayIdWithSpace(xml); // du skriver selv denne metode
    }

    @Override
    public void pickItem(int trayId) throws RemoteException {
        port.pickItem(trayId);
    }

    @Override
    public void insertItem(int trayId, java.lang.String name) throws RemoteException {
        port.insertItem(trayId, name);
    }

    @Override
    public java.lang.String getInventory() throws RemoteException {
        return port.getInventory();
    }

    @Override
    public String pickOrderItems(int[] trayIds) throws RemoteException {

        for (int trayId : trayIds) {
            port.pickItem(trayId);
        }
        return "Items picked";
    }

    @Override
    public boolean stopProcess() {
        stopRequested = true;
        return true;
    }

    // TODO
    @Override
    public boolean restartProcess() {
        return false;
    }

    // TODO
    @Override
    public void setStockThreshold(int trayId, int threshold) {

    }
}
