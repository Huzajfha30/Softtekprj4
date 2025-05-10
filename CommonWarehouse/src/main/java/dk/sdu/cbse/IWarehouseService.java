package dk.sdu.cbse;

import java.lang.reflect.Array;
import java.rmi.RemoteException;

public interface IWarehouseService {
    void pickItem(int trayId) throws RemoteException;
    void insertItem(int trayId, java.lang.String name) throws RemoteException;
    java.lang.String getInventory() throws RemoteException;
    public String pickOrderItems(int[] trayIds) throws RemoteException; // boolean siger enten: alle items var til stede i warehouse eller alle items var ikke til stede i warehouse
    boolean stopProcess();
    boolean restartProcess();
    void setStockThreshold(int trayId, int threshold);
}