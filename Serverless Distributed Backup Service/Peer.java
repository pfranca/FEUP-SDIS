import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;



public class Peer implements RMI {

  	public static final int TTL = 1;
  	public static final int SIZE_OF_BUF = 256;
  	private static final String MSG_BEGIN = "PEER: ";

  	private static int senderId;

  	private static InetAddress mcAddress;
  	private static InetAddress mdbAddress;
  	private static InetAddress mdrAddress;

  	private static int interfacePortNumber;
  	private static int mcPortNumber;
  	private static int mdbPortNumber;
  	private static int mdrPortNumber;


  	public static void main(String[] args) throws IOException {
  		if (!checkArguments(args))
  			return;


  		System.out.println(MSG_BEGIN + "Peers Id: " + getSenderId());
  		interfacePortNumber = getSenderId();

  		createDir();

  	}


  	private static void createDir() {
  		new File("res/" + Integer.toString(senderId)).mkdir();
  	}



  	public static int getSenderId() {
  		return senderId;
  	}


  	public static void setSenderId(int senderId) {
  		Peer.senderId = senderId;
  	}


  	public static InetAddress getMcAddress() {
  		return mcAddress;
  	}

  	/**
  	 * Sets the McAddress
  	 * @param mcAddress
  	 */
  	public static void setMcAddress(InetAddress mcAddress) {
  		Peer.mcAddress = mcAddress;
  	}

  	public static int getMcPortNumber() {
  		return mcPortNumber;
  	}


  	public static void setMcPortNumber(int mcPortNumber) {
  		Peer.mcPortNumber = mcPortNumber;
  	}


  	public static InetAddress getMdbAddress() {
  		return mdbAddress;
  	}


  	public static void setMdbAddress(InetAddress mdbAddress) {
  		Peer.mdbAddress = mdbAddress;
  	}


  	public static int getMdbPortNumber() {
  		return mdbPortNumber;
  	}


  	public static void setMdbPortNumber(int mdbPortNumber) {
  		Peer.mdbPortNumber = mdbPortNumber;
  	}


  	public static int getMdrPortNumber() {
  		return mdrPortNumber;
  	}


  	public static InetAddress getMdrAddress() {
  		return mdrAddress;
  	}

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
