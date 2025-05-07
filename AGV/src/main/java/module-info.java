module AGV {
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.context;

    // Depend on CommonAGV
    requires CommonAGV;

    opens dk.sdu.sm4.agv to spring.core, spring.beans, spring.context;

    opens dk.sdu.sm4.agv.controller to spring.web, spring.beans;
    opens dk.sdu.sm4.agv.service    to spring.beans, spring.core;

    // Export only the controller and service APIs
    exports dk.sdu.sm4.agv.controller;
    exports dk.sdu.sm4.agv.service;
}
