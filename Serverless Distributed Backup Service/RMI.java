import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI extends Remote {

  void backup(String fPath, int replication) throws RemoteException;

	void delete(String fPath) throws RemoteException;

	void restore(String fPath) throws RemoteException;

	void state()throws RemoteException;

	void raclaim(int space)throws RemoteException;

}
