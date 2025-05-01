package dk.sdu.sm4.presets;

import java.util.Set;
import dk.sdu.sm4.inventory.Inventory;

import jakarta.persistence.*;

@Entity
public class Preset {

    @Id
    private Long id;

    @Column
    private String type;

    @Column
    private String name;


    @ManyToMany
    @JoinTable(
            name = "presets_inventory_items",
            joinColumns = @JoinColumn(name = "preset_id"),
            inverseJoinColumns = @JoinColumn(name = "inventory_item_id")
    )

    private Set<Inventory> requiredItems;



    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
