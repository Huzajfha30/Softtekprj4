module Presets {
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires Inventory;

    exports dk.sdu.sm4.presets;
    opens dk.sdu.sm4.presets to org.hibernate.orm.core;


}