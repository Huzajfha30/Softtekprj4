module Core {
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.context;
    requires spring.webmvc;
    requires jakarta.jws;
    requires spring.web;
    exports dk.sdu.sm4.models;
    exports dk.sdu.sm4.interfaces;
    exports dk.sdu.sm4.enums;
}
