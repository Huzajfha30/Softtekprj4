package dk.sdu.sm4.warehouse.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for inserting an item into a tray")
public class InsertItemRequest {

    @Schema(description = "ID of the tray to insert into", example = "5", required = true)
    private int trayId;

    @Schema(description = "Name of the item to insert", example = "Widget", required = true)
    private String name;

    public int getTrayId() {
        return trayId;
    }

    public void setTrayId(int trayId) {
        this.trayId = trayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
