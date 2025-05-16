package dk.sdu.sm4.warehouse.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

//@Schema(description = "Request payload for inserting an item into a tray")
public class InsertItemRequest {

    //@Schema(description = "ID of the tray to insert into", example = "5", required = true)
    private int trayId;

    //@Schema(description = "Name of the item to insert", example = "Widget", required = true)
    private String name;

    //@Schema(description = "Board type (optional)", example = "1")
    private String board;

    //@Schema(description = "Wheels type (optional)", example = "2")
    private String wheels;

    //@Schema(description = "Trucks type (optional)", example = "1")
    private String trucks;

    private boolean isFinalProduct;

    public boolean isFinalProduct() {
        return isFinalProduct;
    }
    public void setIsFinalProduct(boolean isFinalProduct) {
        this.isFinalProduct = isFinalProduct;
    }

    // Getters and setters
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

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getWheels() {
        return wheels;
    }

    public void setWheels(String wheels) {
        this.wheels = wheels;
    }

    public String getTrucks() {
        return trucks;
    }

    public void setTrucks(String trucks) {
        this.trucks = trucks;
    }
}
