/*package dk.sdu.sm4.warehouse.controller;

import dk.sdu.sm4.interfaces.WarehouseService;
import dk.sdu.sm4.models.InventoryStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/pick/{trayId}")
    public ResponseEntity<String> pickItem(@PathVariable String trayId) {
        String item = warehouseService.PickItem(trayId);
        return (item == null)
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found")
                : ResponseEntity.ok(item);
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertItem(
            @RequestParam String trayId,
            @RequestParam String name) {
        warehouseService.InsertItem(trayId, name);
        return ResponseEntity.ok("Item inserted.");
    }

    @GetMapping("/inventory")
    public ResponseEntity<InventoryStatus> getInventory() {
        return ResponseEntity.ok(warehouseService.GetInventory());
    }
}*/
