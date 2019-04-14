package core;

import channels.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import msg.MsgForwarder;
import protocols.*;
import java.util.ArrayList;





public class Peer implements RMI {

  	//public static final int TTL = 1;
  	//public static final int SIZE_OF_BUF = 256;
	//private static final String MSG_BEGIN = "PEER: ";

	static double version;	
	static int peerId;
	static String peerAp;

	static MC mc;
	static MDB mdb;
	static MDR mdr;
	//----
	static MsgForwarder msgForwarder;
	//----  
	//private static Registry rmi;
	public static String ROOT = "peer";
	public static String BACKUP = "backup";
	public static String RESTORED = "restored";
	public static String FILESYSTEM = "";
	
	static FileSystem fs;
  	//private static int interfacePortNumber;
  	//private static int mcPortNumber;
  	//private static int mdbPortNumber;
  	//private static int mdrPortNumber;


  	public static void main(String[] args) throws IOException {
  		//if (!checkArguments(args))
  		//	return;


  		//System.out.println(MSG_BEGIN + "Peers Id: " + getSenderId());
		 // interfacePortNumber = getSenderId();
		  
		version = Double.parseDouble(args[0]);
		peerId = Integer.parseInt(args[1]);
		peerAp = args[2];

		mc = new MC(args[3], args[4]);
		mdb = new MDB(args[5], args[6]);
		mdr = new MDR(args[7], args[8]);

		new Thread(mc).start();
		new Thread(mdb).start();
		new Thread(mdr).start();

		
		//----
		msgForwarder = new MsgForwarder(version);
		//----
		ROOT += peerId + "/";
		BACKUP = "peer" + peerId + "/backup";
		RESTORED = "peer" + peerId + "/restored/";
		FILESYSTEM = "peer" + peerId +"/peer" + peerId +  ".data";
		
		loadFs();

		// print main info 
        System.out.println("version : " + version);
        System.out.println("server_id : " + peerId);
        System.out.println("access point : " + peerAp);
        
        System.out.println();
        
        System.out.println("MC  : " + args[3] + " " + args[4]);
        System.out.println("MDB : " + args[5] + " " + args[6]);
        System.out.println("MDR : " + args[7] + " " + args[8]);
        
        System.out.println();

		connectRMI();
		


  		//createDir();

	  }

	  //TODO: meu

	  public static void connectRMI(){
		try {
            Peer obj = new Peer();
            RMI stub = (RMI) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(peerAp, stub);

            System.err.println("Peer ready");
        } catch (Exception e) {
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
		}

	  }
	  //TODO: END MEU
	  
	  private static void loadFs() throws IOException {

		FileInputStream stream;
		try {
		
			stream = new FileInputStream(FILESYSTEM);
			ObjectInputStream in = new ObjectInputStream(stream);
			fs = (FileSystem) in.readObject();
			in.close();
			
		} catch (FileNotFoundException e) {
			fs = new FileSystem();
			saveFs();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public static void saveFs() throws IOException {
		FileOutputStream stream = new FileOutputStream(Peer.FILESYSTEM);

		ObjectOutputStream out = new ObjectOutputStream(stream);

		out.writeObject(fs);

		out.close();
		
	}
/*
  	private static void createDir() {
  		new File("res/" + Integer.toString(senderId)).mkdir();
  	}
*/


  	public static int getPeerId() {
  		return peerId;
  	}


  	public static void setPeerId(int peerId) {
  		Peer.peerId = peerId;
	  }
	  
	public static MC getMc() {
		return mc;
  	}

	public static MDB getMdb() {
  		return mdb;
	}
	  
	public static MDR getMdr() {
		return mdr;
	}

	public static FileSystem getFileSystem(){
		return fs;
	}

	public static MsgForwarder getMsgForwarder(){
		return msgForwarder;
	}

  

  
	  
	public void backup(String fPath, int replication) throws RemoteException {
		Backup init = new Backup(fPath, replication);
		new Thread(init).start();
	}
	
	
	public void delete(String fPath) throws RemoteException {
		Delete init = new Delete(fPath);
		new Thread(init).start();
	}
	
	public void restore(String fPath) throws RemoteException{
		Restore init = new Restore(fPath);
		new Thread(init).start();
	}
	
	@Override
	public void state() throws RemoteException {
		System.out.println("\nPEER STATE");
		System.out.println("Disk memory capacity: " + fs.getAvailable()/1000+ " KBytes");
		System.out.println("Disk memory used: " + fs.getOccupied()/1000+ " KBytes");
		
		if(fs.hasChunks()) {
			ArrayList<Chunk> chunks = fs.getFiles();
			
			System.out.println("\nCHUNKS");
			for(int i=0; i < chunks.size(); i++) {
				System.out.println("ID: "+ chunks.get(i).getId());
				System.out.println("SIZE: "+ chunks.get(i).getData().length);
				System.out.println("REP DEGREE: " + chunks.get(i).getCurrentReplication()); 
			}
			
			System.out.println("");
		}
		
	}
	
	//public void raclaim(int space) throws RemoteException{
		
	//}
}
