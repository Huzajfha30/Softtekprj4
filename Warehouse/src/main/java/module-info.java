open module Warehouse {
    requires com.fasterxml.jackson.databind;
    requires java.xml;
    requires java.rmi;
    requires java.naming;
    requires CommonWarehouse;
    requires jakarta.validation;
    requires jaxrpc.api;
    requires io.swagger.v3.oas.annotations;
    requires axis;

    exports dk.sdu.sm4.warehouse;
    exports dk.sdu.sm4.warehouse.controller.dto;
    exports dk.sdu.sm4.warehouse.service;
}