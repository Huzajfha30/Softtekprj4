module Warehouse {
    requires com.fasterxml.jackson.databind;
    requires spring.context;
    requires java.xml;
    requires java.rmi;
    requires CommonWarehouse;
    requires axis;
    requires jaxrpc.api;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires jakarta.validation;
    requires io.swagger.v3.oas.annotations;
    requires spring.webmvc;
}