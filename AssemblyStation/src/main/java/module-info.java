module AssemblyStation {
    requires org.eclipse.paho.client.mqttv3;
    requires spring.context;
    requires spring.beans;
    requires spring.web;
    requires jakarta.annotation;

    exports dk.sdu.sm4.assemblystation.service;
    exports dk.sdu.sm4.assemblystation.controller;

    opens dk.sdu.sm4.assemblystation.service to spring.core;
    opens dk.sdu.sm4.assemblystation.controller to spring.core;
}
