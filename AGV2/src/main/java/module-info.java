module AGV2 {
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.context;

    // Shared AGV interface & models
    requires CommonAGV;

    // Allow Spring (and CGLIB) to reflectively instantiate/configure beans
    opens dk.sdu.sm4.agv2 to spring.core, spring.beans, spring.context;
    // Allow Spring MVC to pick up @RestController & map endpoints
    opens dk.sdu.sm4.agv2.controller to spring.web, spring.beans;
    // Allow @Service proxies
    opens dk.sdu.sm4.agv2.service    to spring.beans, spring.core;

    // Expose only the controller API for other modules (if needed)
    exports dk.sdu.sm4.agv2.controller;
    exports dk.sdu.sm4.agv2.service;
}