package msg;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import core.*;

public class MsgHandler implements Runnable{

	String[] msgHeader;
	DatagramPacket dataPacket;
	
	public MsgHandler(DatagramPacket dp){
		dataPacket = dp;
	}
	
	public void run() {
		
		msgHeader = parseHeader(dataPacket);
		int peerId = Integer.parseInt(msgHeader[2]);
		
		if(peerId == Peer.getPeerId()) return;
		
		String op = msgHeader[0];
		switch(op){
		case "PUTCHUNK":
			hdlPUTCHUNK();break;
		case "STORED":
			hdlSTRORED();break;
		case "GETCHUNK":
			hdlGETCHUNK();break;
		case "CHUNK":
			hdlCHUNK();break;
		case "REMOVED":
			hdlREMOVED();break;
		case "DELETE":
			hdlDELETE();break;
		default: break;
			
		}
		
	}

	private void hdlREMOVED() {
		System.out.print("Received REMOVED");
		String fileId = msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);
		
		Chunk chk = new Chunk(chunkNr, fileId, new byte[0], 0);
		
		ArrayList<Chunk> cks = Peer.getFileSystem().getFiles();
	
			if(cks.contains(chk)) {
				int i=0;	
				while(true) {
					if(cks.get(i).getId().equals(chk.getId())) {

						chk = cks.get(i);
						chk.setCurrentReplication(chk.getCurrentReplication()-1);
							
						if(chk.getCurrentReplication() < chk.getReplication()) {	
							Random rand = new Random(); 
							int  r = rand.nextInt(400) + 1;
							
							Peer.getMdb().startSave(chk.getId());							
							try {
								Thread.sleep(r);
							} catch (InterruptedException e) {e.printStackTrace();}
						
							int isSaved = Peer.getMdb().getSaves(chk.getId());	
							System.out.println("SAVES " + isSaved); 
							Peer.getMdb().stopSave(chk.getId());
							if(isSaved == 0 ) {chk.backup();}	
						}return;
					}i++;
				}
			}			
	}

	private void hdlCHUNK() {
		System.out.println("Receiving CHUNK"); 
		String fileId = msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);
		int replication = Integer.parseInt(msgHeader[5]);
		byte[] chunkData = parseBody(dataPacket); 
		
		if(Peer.getMdr().isSaving(fileId)){
			Chunk chk = new Chunk(chunkNr,fileId, chunkData, replication);
			Peer.getMdr().save(fileId, chk);}	
	}

	private void hdlGETCHUNK() {
		System.out.println("Receiving GETCHUNK");
		String fileId = msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);

		System.out.println("/chk"+ chunkNr);
		
		File file = new File(Peer.BACKUP + "/" +  fileId + "/chk"+ chunkNr);
		Peer.getMdr().startSave(fileId);
		
		if(file.exists() && file.isFile()) {
			try {
				byte[] chunkData = loadFileBytes(file);
				Chunk chk = new Chunk(chunkNr, fileId,chunkData,0);
				
				Random rand = new Random();
				int  r = rand.nextInt(400) + 1;
				try {
					Thread.sleep(r);
				} catch (InterruptedException e) {e.printStackTrace();} 
				
				ArrayList<Chunk> chks =Peer.getMdr().getSave(fileId);
				
				if(chks != null)
				if(!chks.contains(new Chunk(chunkNr, fileId, new byte[0], 0)))
						Peer.getMsgForwarder().sendCHUNK(chk);
				
			} catch (IOException e) {e.printStackTrace();}	
		}
		Peer.getMdr().stopSave(fileId);	
	}

	private void hdlDELETE() {
		System.out.println("Receiving DELETE");
		
		String fileId = msgHeader[3];
		Peer.getFileSystem().deleteChunks(fileId);
	   
		
	}

	private void hdlSTRORED() {
		System.out.println("Reiciving STORED");
		
		int peerId = Integer.parseInt(msgHeader[2]);
		String fileId=msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);
		
		String chunkId = fileId + "_" +chunkNr; 
		
		Peer.getMdb().save(chunkId,peerId );
		if(Peer.getFileSystem().isStored(new Chunk(chunkNr,fileId, new byte[0],0))) {Peer.getFileSystem().incReplication(chunkId,Peer.getMdb().getSaves(chunkId)+1);}	
	}

	private void hdlPUTCHUNK() {
		System.out.println("Recieving PUTCHUNK");
		
		String fileId=msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);
		int replication = Integer.parseInt(msgHeader[5]);
		byte[] chunkData =parseBody(dataPacket);
		
		 
		Chunk chk = new Chunk(chunkNr,fileId,chunkData,replication );
		Peer.getMdb().save(chk.getId(), 0);
		
		if(!Peer.getFileSystem().isStored(chk)) {Peer.getFileSystem().storeChunk(chk);}

		Peer.getMdb().startSave(chk.getId());

		Random rand = new Random();
		int  r = rand.nextInt(400) + 1;
		try {
			Thread.sleep(r);
		} catch (InterruptedException e) {e.printStackTrace();} 
	
		Peer.getMsgForwarder().sendSTORED(chk);
	}

	public static byte[] loadFileBytes(File file) throws IOException  {
		FileInputStream fInStream = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fInStream.read(data);
		fInStream.close();
		return data;
	}

	public static String[] parseHeader(DatagramPacket packet) {
		
		ByteArrayInputStream s = new ByteArrayInputStream(packet.getData());
		BufferedReader r = new BufferedReader(new InputStreamReader(s));
		String header = "";

		try {
			header = r.readLine();
		} catch (IOException e) {e.printStackTrace();}
			
		return header.split(" ");
	}
	   
	public static byte[] parseBody(DatagramPacket packet) {
		   
			
		ByteArrayInputStream s = new ByteArrayInputStream(packet.getData());
		BufferedReader r = new BufferedReader(new InputStreamReader(s));
		String header="";
		
		try {
			header += r.readLine();
		} catch (IOException e) { e.printStackTrace();}
		
		int bodyIndex = header.length()+2*MsgForwarder.CRLF.length();
		byte[] body = Arrays.copyOfRange(packet.getData(),bodyIndex ,packet.getLength());

		return body;
	}
}