package app;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import core.RMI;

public class TestApp {
	
	static RMI rmi;
	
    private TestApp() {}

    public static void main(String[] args) {
		
		String filePath;
    	String peerAp = args[0];
        String protocol = args[1].toUpperCase();
        
		System.out.println("Access Point : " + peerAp + "\nProtocol : " + protocol);
		//rmi
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
        	rmi = (RMI) registry.lookup(peerAp);
        } catch (Exception e) {rmiExcep(e);}

		switch (protocol) {
		case "BACKUP":
			if (args.length != 4) {invalidArgs();backup();}
			filePath = args[2];
			int replication = Integer.parseInt(args[3]);
			System.out.println("File: " + filePath);
			System.out.println("Replication: " + replication);
			try {
				rmi.backup(filePath, replication);
				sent();
			} catch (RemoteException e) {backupExcep(e);}
			break;
		case "DELETE":
			if (args.length != 3) {invalidArgs();delete();}
			filePath = args[2];
			try {
				rmi.delete(filePath);
				sent();
			} catch (RemoteException e) {deleteExcep(e);}
			break;
		case "RESTORE":
			if (args.length != 3) {invalidArgs();restore();}
			filePath = args[2];
			try {
				rmi.restore(filePath);
				sent();
			} catch (RemoteException e) {deleteExcep(e);}
			break;
		default:
			System.out.println("Protocol is not valid!\nProtocols : BACKUP RESTORE DELETE ");
		}
	}

	private static void backup() {
		System.out.println("TestApp <peerAp> BACKUP <filePath> <replicatDegr>");
	}

	private static void delete() {
		System.out.println("TestApp <peerAp> DELETE <filePath>");
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
