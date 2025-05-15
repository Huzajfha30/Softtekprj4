package dk.sdu.sm4.warehouse.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

//@Schema(description = "Request payload for picking an order of multiple tray items")
public class PickOrderRequest {

    //@NotNull
    //@NotEmpty
    /*@Schema(
            description = "Array of tray IDs to pick",
            example = "[1, 2, 3]",
            required = true
    )*/
    private int[] trayIds;

    public int[] getTrayIds() {
        return trayIds;
    }

    public void setTrayIds(int[] trayIds) {
        this.trayIds = trayIds;
    }
}
