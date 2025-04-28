package dk.sdu.sm4.models;

import dk.sdu.sm4.enums.WarehouseState;
import java.util.Map;

public class InventoryStatus {
    private Map<String, String> inventory;
    private WarehouseState state;
    private String timeStamp;

    public InventoryStatus(Map<String, String> inventory, WarehouseState state, String timeStamp) {
        this.inventory = inventory;
        this.state = state;
        this.timeStamp = timeStamp;
    }

    public Map<String, String> getInventory() {
        return inventory;
    }

    public void setInventory(Map<String, String> inventory) {
        this.inventory = inventory;
    }

    public WarehouseState getState() {
        return state;
    }

    public void setState(WarehouseState state) {
        this.state = state;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
