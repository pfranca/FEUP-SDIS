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
	static double version;	
	static int peerId;
	static String peerAp;
	static MsgForwarder msgForwarder;
	static FileSystem fs;
	static MC mc;
	static MDB mdb;
	static MDR mdr;
	public static String ROOT = "peer";
	public static String BACKUP = "backup";
	public static String RESTORED = "restored";
	public static String FILESYSTEM = "";
	


  	public static void main(String[] args) throws IOException {
  	  //if (!checkArguments(args))	return;
			version = Double.parseDouble(args[0]);
			peerId = Integer.parseInt(args[1]);
			peerAp = args[2];
			msgForwarder = new MsgForwarder(version);
			mc = new MC(args[3], args[4]);
			mdb = new MDB(args[5], args[6]);
			mdr = new MDR(args[7], args[8]);
			new Thread(mc).start();
			new Thread(mdb).start();
			new Thread(mdr).start();

			
			ROOT += peerId + "/";
			BACKUP = "peer" + peerId + "/backup";
			RESTORED = "peer" + peerId + "/restored/";
			FILESYSTEM = "peer" + peerId +"/peer" + peerId +  ".data";
			
			System.out.println("Booting, peer" + peerId);
			System.out.println("Protocol Version : " + version);
			System.out.println("AP : " + peerAp);	
			System.out.println("Mc at  : " + args[3] + " " + args[4]);
			System.out.println("Mdb at : " + args[5] + " " + args[6]);
			System.out.println("Mdr at : " + args[7] + " " + args[8]);
			
			loadFs();
			System.out.println();
			connectRMI();
			//createDir();

		}
		
	  public static void connectRMI(){
		try {
            Peer obj = new Peer();
            RMI stub = (RMI) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(peerAp, stub);

            System.err.println("RMI conencted, ready!");
        } catch (Exception e) { System.err.println(e.toString()); e.printStackTrace();}

		}
		
		/*
  	private static void createDir() {
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

	  
	  private static void loadFs() throws IOException {
			FileInputStream stream;
			try {
				stream = new FileInputStream(FILESYSTEM);
				ObjectInputStream in = new ObjectInputStream(stream);
				fs = (FileSystem) in.readObject();
				in.close();
				
			} catch (FileNotFoundException e) {fs = new FileSystem();saveFs();
			} catch (ClassNotFoundException e) {e.printStackTrace();} 		
		}
	
	//rmi
	public static void saveFs() throws IOException {
		FileOutputStream stream = new FileOutputStream(Peer.FILESYSTEM);
		ObjectOutputStream out = new ObjectOutputStream(stream);
		out.writeObject(fs);out.close();
		
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
	
	public void state() throws RemoteException {

	}
	
}
