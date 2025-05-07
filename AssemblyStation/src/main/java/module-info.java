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
    requires org.json;
    requires com.fasterxml.jackson.databind;
    requires spring.webmvc;


    exports dk.sdu.sm4.assemblystation.controller;
    exports dk.sdu.sm4.assemblystation.service;
}