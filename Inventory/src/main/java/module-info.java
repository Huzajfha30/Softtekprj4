module Inventory {
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    exports dk.sdu.sm4.inventory;

    opens dk.sdu.sm4.inventory to org.hibernate.orm.core;
}