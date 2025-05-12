package dk.sdu.sm4.warehouse.controller;

import dk.sdu.cbse.IWarehouseService;
import dk.sdu.sm4.warehouse.controller.dto.InsertItemRequest;
import dk.sdu.sm4.warehouse.controller.dto.PickOrderRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    private final IWarehouseService warehouseService;
    private final List<InsertItemRequest> presets = new ArrayList<>();
    public WarehouseController(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/inventory")
    public ResponseEntity<String> getInventory() {
        try {
            String xml = warehouseService.getInventory();
            return ResponseEntity.ok(xml);
        } catch (RemoteException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("Error fetching inventory: " + e.getMessage());
        }
    }

    @PostMapping("/pickItem")
    public ResponseEntity<String> pickItem(@RequestParam int trayId) {
        try {
            warehouseService.pickItem(trayId);
            return ResponseEntity.ok("Picked one item from tray " + trayId);
        } catch (RemoteException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("Error picking item: " + e.getMessage());
        }
    }

    @PostMapping("/insertItem")
    public ResponseEntity<String> insertItem(@Valid @RequestBody InsertItemRequest req) {
        try {
            int trayId = req.getTrayId();

            if (trayId == -1) {
                trayId = warehouseService.findTrayWithSpace();
                if (trayId == -1) {
                    return ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body("Ingen tilgængelig bakke med plads");
                }
            }

            warehouseService.insertItem(trayId, req.getName());

            // 💾 Gem også som preset i RAM
            presets.add(req);

            return ResponseEntity.ok("Inserted '" + req.getName() + "' into tray " + trayId);
        } catch (RemoteException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("Error inserting item: " + e.getMessage());
        }
    }

    @GetMapping("/getPresets")
    public ResponseEntity<List<InsertItemRequest>> getPresets() {
        return ResponseEntity.ok(presets);
    }


    @PostMapping("/pickOrder")
    public ResponseEntity<String> pickOrderItems(
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            PickOrderRequest req
    ) {
        try {
            String result = warehouseService.pickOrderItems(req.getTrayIds());
            return ResponseEntity.ok(result);
        } catch (RemoteException e) {
            // map your RemoteException to a 502 Bad Gateway
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("Error picking order: " + e.getMessage());
        }
    }



    @PostMapping("/stopProcess")
    public ResponseEntity<String> stopProcess() {
        boolean ok = warehouseService.stopProcess();
        return ok
                ? ResponseEntity.ok("Stop requested")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unable to request stop");
    }

    @PostMapping("/restartProcess")
    public ResponseEntity<String> restartProcess() {
        boolean ok = warehouseService.restartProcess();
        return ok
                ? ResponseEntity.ok("Restart requested")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unable to request restart");
    }

    @PostMapping("/setStockThreshold")
    public ResponseEntity<String> setStockThreshold(@RequestBody Map<String,String> body) {
        try {
            int trayId    = Integer.parseInt(body.get("trayId"));
            int threshold = Integer.parseInt(body.get("threshold"));
            warehouseService.setStockThreshold(trayId, threshold);
            return ResponseEntity.ok("Threshold for tray "+trayId+" set to "+threshold);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Usage: {\"trayId\":<id>,\"threshold\":<number>}");
        }
    }
}


/*
    void pickItem(int trayId) throws RemoteException;
    void insertItem(int trayId, java.lang.String name) throws RemoteException;
    java.lang.String getInventory() throws RemoteException;
    public String pickOrderItems(int[] trayIds) throws RemoteException; // boolean siger enten: alle items var til stede i warehouse eller alle items var ikke til stede i warehouse
    boolean stopProcess();
    boolean restartProcess();
    void setStockThreshold(int trayId, int threshold); */

