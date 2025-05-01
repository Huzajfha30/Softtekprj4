package dk.sdu.sm4.interfaces;

import dk.sdu.sm4.models.InventoryStatus;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;



@WebService
public interface WarehouseService {

    @WebMethod
    String PickItem(int trayId);

    @WebMethod
    boolean InsertItem(int trayId, String name);

    @WebMethod
    InventoryStatus GetInventory();
}
