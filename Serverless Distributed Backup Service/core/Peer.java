package core;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.sun.org.apache.xpath.internal.SourceTree;
import channels.*;



public class Peer implements RMI {

  	//public static final int TTL = 1;
  	//public static final int SIZE_OF_BUF = 256;
	//private static final String MSG_BEGIN = "PEER: ";

	private static double version;	
	private static int peerId;
	private static String peerAp;

  	private static MC mc;
  	private static MDB mdb;
	private static MDR mdr;
	  
	//private static Registry rmi;

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

		





		int port = Registry.REGISTRY_PORT;
		rmi = null;

		try{
			rmi = LocateRegistry.getRegistry(port);
			System.out.println("dentro reg try");
		} catch ( Exception e) {
			e.printStackTrace();
			System.out.println("merda");
		}

  		//createDir();

  	}


  	private static void createDir() {
  		new File("res/" + Integer.toString(senderId)).mkdir();
  	}



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

  

  

  	private static boolean checkArguments(String[] arguments) throws UnknownHostException {
  		if (arguments == null) {
  			System.out.println("Invalid arguments - Expected: [<SenderId> <McAddress>:<McPortNumber> <MdbAddress>:<MdbPortNumber> <MdrAddress>:<MdrPortNumber>");
  			return false;
  		}
  		if (arguments.length != 4) {
  			System.out.println("Invalid arguments - Expected: [<SenderId> <McAddress>:<McPortNumber> <MdbAddress>:<MdbPortNumber> <MdrAddress>:<MdrPortNumber>");
  			return false;
  		}


  		return true;
	}
	  
	public void backup(String fPath, int replication) throws RemoteException {
		
	}
	
	
	public void delete(String fPath) throws RemoteException {
		
	}
	
	public void restore(String fPath) throws RemoteException{
		
	}
	
	public void state() throws RemoteException{
		
	}
	
	public void raclaim(int space) throws RemoteException{
		
	}
}
