package myInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface MyInterface extends Remote {
    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location) throws RemoteException;
    public String createSRecord(String firstName, String lastName, String courseRegistered, String status, String statusDate) throws RemoteException;
    public String getRecordCounts() throws RemoteException;
    public String editRecord(int recordID, String fieldName, String newValue) throws RemoteException;
}
