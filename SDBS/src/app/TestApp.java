package app;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import core.RMI;

public class TestApp {
	
	static RMI i_rmi;
	
    private TestApp() {}

    public static void main(String[] args) {
		
		
    	String peerAp = args[0];
        String subProt = args[1].toUpperCase();
        
        System.out.println("Access Point : " + peerAp);
        System.out.println("Subprotocol : " + subProt);

        
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            i_rmi = (RMI) registry.lookup(peerAp);

        } catch (Exception e) {
        	rmiExcep(e);
		}

		String filePath;

		switch (subProt) {
		case "BACKUP":
			if (args.length != 4) {
				invalidArgs();
				backup();
			}

			filePath = args[2];
			int replicatDegr = Integer.parseInt(args[3]);

			if (replicatDegr > 9) {
				System.out.println("ATENTION : Replication degree is not valid!!");
				System.out.println("ATENTION : int < 10");
			}

			System.out.println("file path: " + filePath);
			System.out.println("replication degree: " + replicatDegr);

			try {
				i_rmi.backup(filePath, replicatDegr);
				sent();
			} catch (RemoteException e) {
				backupExcep(e);
			}
			break;

		case "DELETE":
			if (args.length != 3) {
				invalidArgs();
				delete();
			}

			filePath = args[2];

			try {
				i_rmi.delete(filePath);
				sent();
			} catch (RemoteException e) {
				deleteExcep(e);
			}
			break;

		case "RESTORE":
			if (args.length != 3) {
				invalidArgs();
				restore();
			}

			filePath = args[2];

			try {
				i_rmi.restore(filePath);
				sent();
			} catch (RemoteException e) {
				deleteExcep(e);
			}
			break;


		case "STATE":
			try {
				i_rmi.state();
				sent();
			} catch (RemoteException e) {
				deleteExcep(e);
			}
			break;

		default:
			System.out.println("ATENTION : Subprotocol is not valid!!");
			System.out.println("Subprotocols : BACKUP RESTORE DELETE RECLAIM STATE");

		}
	}



	//AUXLIAR FUNCTIONS PROTOCOLS 

	private static void backup() {
		System.out.println("TestApp <peerAp> BACKUP <filePath> <replicatDegr>");
	}

	private static void delete() {
		System.out.println("TestApp <peerAp> DELETE <filePath>");
	}

	private static void reclaim() {
		System.out.println("TestApp <peerAp> RECLAIM <diskSpace>");
	}

	private static void restore() {
		System.out.println("TestApp <peerAp> RESTORE <filePath>");
	}

	private static void sent() {
		System.out.println("\nSent");
	}

	//AUXILIAR FUNCTIONS TO ERROR TREATMENT

	private static void invalidArgs() {
		System.out.println("ATENTION : Invalid arguements !!!");
	}

	private static void backupExcep(RemoteException e) {
		System.err.println("BACKUP Exception: " + e.toString());
		e.printStackTrace();
	}

	private static void rmiExcep(Exception e) {
		System.err.println("RMI Exception: " + e.toString());
		e.printStackTrace();
	}

	private static void deleteExcep(RemoteException e) {
		System.err.println("DELETE Exception: " + e.toString());
		 e.printStackTrace();
	}
}
