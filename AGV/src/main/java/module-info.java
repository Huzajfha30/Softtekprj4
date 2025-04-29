module AGV {
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.context;

    opens dk.sdu.sm4.agv to spring.core, spring.beans, spring.context, spring.web;
    opens dk.sdu.sm4.agv.controller to spring.web, spring.beans;
    opens dk.sdu.sm4.agv.service to spring.beans, spring.core;
    opens dk.sdu.sm4.agv.model to com.fasterxml.jackson.databind;

    exports dk.sdu.sm4.agv;
    exports dk.sdu.sm4.agv.controller;
    exports dk.sdu.sm4.agv.model;
    exports dk.sdu.sm4.agv.service;
}
