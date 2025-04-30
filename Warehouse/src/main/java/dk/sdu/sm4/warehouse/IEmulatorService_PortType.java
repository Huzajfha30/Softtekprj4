/**
 * IEmulatorService_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dk.sdu.sm4.warehouse;

public interface IEmulatorService_PortType extends java.rmi.Remote {
    public java.lang.String pickItem(int trayId) throws java.rmi.RemoteException;
    public java.lang.String insertItem(int trayId, java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String getInventory() throws java.rmi.RemoteException;
}
