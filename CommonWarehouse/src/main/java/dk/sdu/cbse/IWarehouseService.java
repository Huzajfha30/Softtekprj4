package dk.sdu.cbse;

import java.lang.reflect.Array;

public interface IWarehouseService {
    void pickItem(int trayId);
    void insertItem(int trayId, java.lang.String name);
    java.lang.String getInventory();
    boolean pickOrderItems(int[] trayIds); // boolean siger enten: alle items var til stede i warehouse eller alle items var ikke til stede i warehouse
    boolean stopProcess();
    boolean restartProcess();
    void setStockThreshold(int trayId, int threshold);
}