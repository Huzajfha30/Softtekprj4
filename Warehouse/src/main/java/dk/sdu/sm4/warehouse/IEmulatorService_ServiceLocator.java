/**
 * IEmulatorService_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dk.sdu.sm4.warehouse;

import org.springframework.context.annotation.Bean;

public class IEmulatorService_ServiceLocator extends org.apache.axis.client.Service implements dk.sdu.sm4.warehouse.IEmulatorService_Service {

    public IEmulatorService_ServiceLocator() {
    }


    public IEmulatorService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public IEmulatorService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicHttpBinding_IEmulatorService
    private java.lang.String BasicHttpBinding_IEmulatorService_address = "http://localhost:8085/Service.asmx";

    public java.lang.String getBasicHttpBinding_IEmulatorServiceAddress() {
        return BasicHttpBinding_IEmulatorService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicHttpBinding_IEmulatorServiceWSDDServiceName = "BasicHttpBinding_IEmulatorService";

    public java.lang.String getBasicHttpBinding_IEmulatorServiceWSDDServiceName() {
        return BasicHttpBinding_IEmulatorServiceWSDDServiceName;
    }

    public void setBasicHttpBinding_IEmulatorServiceWSDDServiceName(java.lang.String name) {
        BasicHttpBinding_IEmulatorServiceWSDDServiceName = name;
    }

    public dk.sdu.sm4.warehouse.IEmulatorService_PortType getBasicHttpBinding_IEmulatorService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_IEmulatorService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_IEmulatorService(endpoint);
    }

    public dk.sdu.sm4.warehouse.IEmulatorService_PortType getBasicHttpBinding_IEmulatorService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            dk.sdu.sm4.warehouse.BasicHttpBinding_IEmulatorServiceStub _stub = new dk.sdu.sm4.warehouse.BasicHttpBinding_IEmulatorServiceStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_IEmulatorServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicHttpBinding_IEmulatorServiceEndpointAddress(java.lang.String address) {
        BasicHttpBinding_IEmulatorService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (dk.sdu.sm4.warehouse.IEmulatorService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                dk.sdu.sm4.warehouse.BasicHttpBinding_IEmulatorServiceStub _stub = new dk.sdu.sm4.warehouse.BasicHttpBinding_IEmulatorServiceStub(new java.net.URL(BasicHttpBinding_IEmulatorService_address), this);
                _stub.setPortName(getBasicHttpBinding_IEmulatorServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("BasicHttpBinding_IEmulatorService".equals(inputPortName)) {
            return getBasicHttpBinding_IEmulatorService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "IEmulatorService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "BasicHttpBinding_IEmulatorService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BasicHttpBinding_IEmulatorService".equals(portName)) {
            setBasicHttpBinding_IEmulatorServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
