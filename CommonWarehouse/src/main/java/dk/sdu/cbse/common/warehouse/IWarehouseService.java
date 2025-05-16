package dk.sdu.cbse.common.warehouse;

import java.lang.reflect.Array;
import java.rmi.RemoteException;

public interface IWarehouseService {
    int findTrayWithSpace() throws RemoteException;
    void pickItem(int trayId) throws RemoteException;
    void insertItem(int trayId, java.lang.String name) throws RemoteException;
    java.lang.String getInventory() throws RemoteException;
    public String pickOrderItems(int[] trayIds) throws RemoteException; // boolean siger enten: alle items var til stede i warehouse eller alle items var ikke til stede i warehouse
    boolean stopProcess();
    boolean restartProcess();
    void setStockThreshold(int trayId, int threshold);

    String getTrayContent(int trayId) throws RemoteException;
}