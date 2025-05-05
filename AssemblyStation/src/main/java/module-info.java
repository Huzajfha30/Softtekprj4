module AssemblyStation {
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires spring.beans;
    requires spring.context;
    requires jakarta.annotation;
    requires org.eclipse.paho.client.mqttv3;
    requires io.swagger.v3.oas.annotations; //for swagger ui
    requires io.swagger.v3.oas.models;


    requires CommonAssemblyStation;


    exports dk.sdu.sm4.assemblystation.controller;
    exports dk.sdu.sm4.assemblystation.service;
}