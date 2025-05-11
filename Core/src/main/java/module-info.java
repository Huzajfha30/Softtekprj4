module Core {
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.context;
    requires spring.webmvc;
    requires jakarta.jws;
    requires spring.web;
    requires org.eclipse.paho.client.mqttv3;

    requires CommonAGV;
    requires CommonAssemblyStation;
    requires spring.beans;
    requires org.json;

    exports dk.sdu.sm4.service;

    opens dk.sdu.sm4 to spring.core;
    opens dk.sdu.sm4.service to spring.beans, spring.core;
    opens dk.sdu.sm4.controller to spring.beans, spring.web;
    opens dk.sdu.sm4.config to spring.beans, spring.core;
    exports dk.sdu.sm4.model;
    opens dk.sdu.sm4.model to spring.beans, spring.core;
}