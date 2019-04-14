package msg;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

//import common.Utils;
import core.Chunk;
import core.Peer;

public class MsgHandler implements Runnable{

	DatagramPacket dataPacket;
	
	String[] msgHeader;
	
	public MsgHandler(DatagramPacket dp){
		dataPacket = dp;
	}
	
	@Override
	public void run() {
		
		msgHeader = parseHeader(dataPacket);
		
		int peerId = Integer.parseInt(msgHeader[2]);
		
		
		// if message comes from self ignore it 
		if(peerId == Peer.getPeerId()) return;
		
		String op = msgHeader[0];
		
		switch(op){
		case "PUTCHUNK":
			hdlPUTCHUNK();
			break;
		case "STORED":
			hdlSTRORED();
			break;
		case "DELETE":
			hdlDELETE();
			break;
		case "GETCHUNK":
			hdlGETCHUNK();
			break;
		case "CHUNK":
			hdlCHUNK();
			break;
		case "REMOVED":
			hdlREMOVED();
		default:
			break;
			
		}
		
	}

	private void hdlREMOVED() {
		System.out.println("REMOVED RECEIVED"); //TODO: ref
		String fileId = msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);
		
		Chunk chunk = new Chunk(chunkNr, fileId, new byte[0], 0);
		
		
		ArrayList<Chunk> chunks = Peer.getFileSystem().getFiles();
	
			if(chunks.contains(chunk)) {
			
				int i=0;	
				while(true) {
					
					if(chunks.get(i).getId().equals(chunk.getId())) { //TODO ID-Id
						chunk= chunks.get(i);
	
						
						chunk.setCurrentReplication(chunk.getCurrentReplication()-1);
						
						
						if(chunk.getCurrentReplication() < chunk.getReplication()) {
							
							// wait a random delay
							Random rand = new Random(); //TODO: por utils
							int  n = rand.nextInt(400) + 1;
							
							Peer.getMdb().startSave(chunk.getId());
														
							try {
								Thread.sleep(n);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							
							
							int save = Peer.getMdb().getSaves(chunk.getId());
							
							System.out.println("SAVES " + save); //TODO: ref
							
							Peer.getMdb().stopSave(chunk.getId());
							
							if(save == 0 ) //TODO: was (save == 0)
								chunk.backup();
								
						}
						return;
					}
					i++;
				}
			}			
		
	}

	private void hdlCHUNK() {
		System.out.println("CHUNK RECEIVED"); //TODO ref
		
		// parsing message
		String fileId = msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);
		int replication = Integer.parseInt(msgHeader[5]);
		byte[] chunkData = parseBody(dataPacket); 
		
		if(Peer.getMdr().isSaving(fileId)){
			Chunk chunk = new Chunk(chunkNr,fileId, chunkData, replication);
			Peer.getMdr().save(fileId, chunk);
		}
			
	}

	private void hdlGETCHUNK() {
		System.out.println("GETCHUNK RECEIVED");
		
		String fileId = msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);
	
		System.out.println("/chk"+ chunkNr);
		
		File file = new File(Peer.BACKUP + "/" +  fileId + "/chk"+ chunkNr);
		
		Peer.getMdr().startSave(fileId);
		
		if(file.exists() && file.isFile()) {
			try {
				byte[] chunkData = loadFileBytes(file);
				
				Chunk chunk = new Chunk(chunkNr, fileId,chunkData,0);
				
				// wait a random delay
				Random rand = new Random();
				int  n = rand.nextInt(400) + 1;
				
				
				try {
					Thread.sleep(n);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				
				ArrayList<Chunk> chunks =Peer.getMdr().getSave(fileId);
				
				
				if(chunks != null)
				if(!chunks.contains(new Chunk(chunkNr, fileId, new byte[0], 0)))
						Peer.getMsgForwarder().sendCHUNK(chunk);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		Peer.getMdr().stopSave(fileId);
		
		
	}

	private void hdlDELETE() {
		System.out.println("DELETE RECEIVED");
		
		String file_id = msgHeader[3];

		Peer.getFileSystem().deleteChunks(file_id);
	   
		
	}

	private void hdlSTRORED() {
		System.out.println("STORED RECEIVED");
		
		int peerId = Integer.parseInt(msgHeader[2]);
		String fileId=msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);
		
		String chunkId = fileId + "_" +chunkNr; 
		
		Peer.getMdb().save(chunkId,peerId );
		
		if(Peer.getFileSystem().isStored(new Chunk(chunkNr,fileId, new byte[0],0))) 
			Peer.getFileSystem().incReplication(chunkId,Peer.getMdb().getSaves(chunkId)+1);
		
		
	}

	private void hdlPUTCHUNK() {
		System.out.println("PUTCHUNK RECEIVED");
		
		// chunk info from header
		String fileId=msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);
		int replication = Integer.parseInt(msgHeader[5]);
		
		// chunk data from body
		byte[] chunkData =parseBody(dataPacket);
		
		
		// create chunk 
		Chunk chunk = new Chunk(chunkNr,fileId,chunkData,replication );
		Peer.getMdb().save(chunk.getId(), 0);
		
		// stored chunk if not stored already
		if(!Peer.getFileSystem().isStored(chunk)) {
			Peer.getFileSystem().storeChunk(chunk);
		}
		
		// start saving STORED messages
		Peer.getMdb().startSave(chunk.getId());
		
		// wait a random delay
		Random rand = new Random();
		int  n = rand.nextInt(400) + 1;
		
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
		// send STORED message
		Peer.getMsgForwarder().sendSTORED(chunk);
	}

		public static byte[] loadFileBytes(File file) throws IOException  {
			FileInputStream file_is = new FileInputStream(file);
	
			byte[] data = new byte[(int) file.length()];
	
			file_is.read(data);
			file_is.close();
	
			return data;
		}

		public static String[] parseHeader(DatagramPacket packet) {
		 
			ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData());
		   BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
   
		   String header = "";
		   try {
			   header = reader.readLine();
		   } catch (IOException e) {
			   e.printStackTrace();
		   }
   
		   return header.split(" ");
   
	}
	   
	   public static byte[] parseBody(DatagramPacket packet) {
		   
			
		   ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData());
		   BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
   
		   String header="";
		   
		   
		   try {
			   header += reader.readLine();
		   } catch (IOException e) {
			   e.printStackTrace();
		   }
		   
		   int body_idx = header.length()+2*MsgForwarder.CRLF.length();
		   
		   byte[] body = Arrays.copyOfRange(packet.getData(),body_idx ,
				   packet.getLength());
   
		   
		   return body;
	}
		
		
	
	public class chunkFilter implements FilenameFilter {
		
	       private String fileId;
		
	       public chunkFilter(String fileId) {
	         this.fileId = fileId;             
	       }
		       
	       public boolean accept(File dir, String name) {
	         return (name.endsWith(fileId));
	       }
	    }
	

}