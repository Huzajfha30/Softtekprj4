package dk.sdu.sm4.warehouse.service;

import dk.sdu.cbse.IWarehouseService;
import dk.sdu.sm4.warehouse.IEmulatorService_PortType;
import dk.sdu.sm4.warehouse.IEmulatorService_ServiceLocator;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

@Service
public class WarehouseService implements IWarehouseService {

    private final IEmulatorService_ServiceLocator serviceLocator;

    private final IEmulatorService_PortType port;

    private volatile boolean stopRequested = false;
    public WarehouseService(IEmulatorService_ServiceLocator serviceLocator, IEmulatorService_PortType port) throws ServiceException {
        this.serviceLocator = serviceLocator;
        this.port = port;
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
