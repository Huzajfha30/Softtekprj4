package dk.sdu.sm4.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.sdu.cbse.common.warehouse.IWarehouseService;
import dk.sdu.sm4.warehouse.controller.dto.InsertItemRequest;
import dk.sdu.sm4.warehouse.controller.dto.PickOrderRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/warehouse")
@CrossOrigin(origins = "http://localhost:3000")
public class WarehouseControllerMonitor {

    private final IWarehouseService warehouseService;
    private final List<InsertItemRequest> presets = new ArrayList<>();
    public WarehouseControllerMonitor(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public WarehouseControllerMonitor() {
        ServiceLoader<IWarehouseService> loader = ServiceLoader.load(IWarehouseService.class);
        this.warehouseService = loader.findFirst().orElseThrow(() -> new IllegalStateException("No warehouses found"));
    }

    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Object>> getEnhancedInventory() {
        try {
            String xml = warehouseService.getInventory(); // ← XML fra emulator
            List<Map<String, Object>> inventory = new ArrayList<>();

            // Parse XML til JSON-struktur (bruger Jackson her for simplicitet)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(xml);
            JsonNode trays = root.get("Inventory");

            for (JsonNode tray : trays) {
                int id = tray.get("Id").asInt();
                String content = tray.get("Content").asText("");

                // Find om den er final product
                boolean isFinal = presets.stream()
                        .anyMatch(p -> p.getTrayId() == id && Boolean.TRUE.equals(p.isFinalProduct()));

                Map<String, Object> trayData = new HashMap<>();
                trayData.put("Id", id);
                trayData.put("Content", content);
                trayData.put("IsFinalProduct", isFinal);
                inventory.add(trayData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("Inventory", inventory);
            response.put("DateTime", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Error parsing inventory: " + e.getMessage()));
        }
    }


    @PostMapping("/pickItem")
    public ResponseEntity<String> pickItem(@RequestBody Map<String, Integer> request) {
        Integer trayId = request.get("trayId");

        if (trayId == null) {
            return ResponseEntity.badRequest().body("trayId is required");
        }

        try {
            warehouseService.pickItem(trayId);
            return ResponseEntity.ok("Picked item from tray " + trayId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

            req.setTrayId(trayId);
            req.setIsFinalProduct(false); // 💡 Opret som ikke-færdigt produkt
            presets.add(req);

            return ResponseEntity.ok("Inserted '" + req.getName() + "' into tray " + trayId);
        } catch (RemoteException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("Error inserting item: " + e.getMessage());
        }
    }

    @PostMapping("/resetFinalProducts")
    public ResponseEntity<String> resetFinalProducts() {
        presets.clear(); // sletter alle gemte presets i RAM
        return ResponseEntity.ok("All final product flags have been reset");
    }

    @PostMapping("/insertFinalProduct")
    public ResponseEntity<String> insertFinalProduct(@RequestBody InsertItemRequest req) {
        try {
            int trayId = req.getTrayId();
            warehouseService.insertItem(trayId, req.getName());

            req.setIsFinalProduct(true); // ✅ Dette ER et færdigt produkt
            presets.add(req);

            return ResponseEntity.ok("Final product inserted in tray " + trayId);
        } catch (RemoteException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Error inserting final product: " + e.getMessage());
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

