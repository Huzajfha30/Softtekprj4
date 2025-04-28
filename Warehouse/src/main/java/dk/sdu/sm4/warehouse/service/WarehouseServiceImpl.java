package dk.sdu.sm4.warehouse.service;

import dk.sdu.sm4.enums.WarehouseState;
import dk.sdu.sm4.interfaces.WarehouseService;
import dk.sdu.sm4.models.InventoryStatus;
import jakarta.jws.WebService;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@WebService(targetNamespace = "http://warehouse.sm4.sdu.dk/")
public class WarehouseServiceImpl implements WarehouseService {

    private final Map<String, String> inventory = new HashMap<>();
    private WarehouseState state = WarehouseState.IDLE;

    public WarehouseServiceImpl() {
        inventory.put("1", "Item 1");
        inventory.put("2", "Item 2");
        inventory.put("3", "Item 3");
        inventory.put("4", "Assembly 1");
        inventory.put("5", "");
    }

    @Override
    public String PickItem(int trayId) {
        String item = inventory.getOrDefault(String.valueOf(trayId), "");
        if (item == null || item.isEmpty()) {
            state = WarehouseState.ERROR;
            return null;
        }
        inventory.put(String.valueOf(trayId), "");
        state = WarehouseState.EXECUTING;
        return item;
    }

    @Override
    public boolean InsertItem(int trayId, String name) {
        System.out.println("InsertItem kaldt med trayId = " + trayId + ", name = " + name); // <-- tilføj denne
        inventory.put(String.valueOf(trayId), name);
        state = WarehouseState.EXECUTING;
        return true;
    }


    @Override
    public InventoryStatus GetInventory() {
        return new InventoryStatus(
                inventory,
                state,
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
    }


}
