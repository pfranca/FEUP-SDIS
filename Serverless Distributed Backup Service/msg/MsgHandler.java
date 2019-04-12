package msg;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Random;

//import common.Utils;
//import peer.Chunk;
import core.Peer;

public class MsgHandler implements Runnable{

	DatagramPacket dataPacket;
	
	String[] msgHeader;
	
	public MsgHandler(DatagramPacket dp){
		this.dataPacket = dp;
	}
	
	@Override
	public void run() {
		
		msgHeader = Utils.parseHeader(dataPacket);
		
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
		
		
		ArrayList<Chunk> chunks = Peer.getDisk().getStoredChunks();
	
			if(chunks.contains(chunk)) {
			
				int i=0;	
				while(true) {
					
					if(chunks.get(i).getID().equals(chunk.getID())) { //TODO ID-Id
						chunk= chunks.get(i);
	
						
						chunk.setActualRepDegree(chunk.getActualRepDegree()-1);
						
						
						if(chunk.getActualRepDegree() < chunk.getRepDegree()) {
							
							// wait a random delay
							Random rand = new Random(); //TODO: por utils
							int  n = rand.nextInt(400) + 1;
							
							Peer.getMdb().startSave(chunk.getID());
														
							try {
								Thread.sleep(n);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							
							
							int save = Peer.getMdb().getSaves(chunk.getID());
							
							System.out.println("SAVES " + save); //TODO: ref
							
							Peer.getMdb().stopSave(chunk.getID());
							
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
		byte[] chunkData = Utils.parseBody(dataPacket); //TODO: chunkdata can be chunckbody??
		
		if(Peer.getMdr().isSaving(fileId)){
			Chunk chunk = new Chunk(chunkNr,fileId, chunkData, replication);
			Peer.getMdr().save(fileId, chunk);
		}
			
	}

	private void handleGETCHUNK() {
		System.out.println("GETCHUNK RECEIVED");
		
		String file_id = header[3];
		int chunk_no = Integer.parseInt(header[4]);
	
		
		File file = new File(Peer.CHUNKS +  chunk_no + "_"+ file_id);
		
		Peer.getMDR().startSave(file_id);
		
		if(file.exists() && file.isFile()) {
			try {
				byte[] chunk_data = Utils.loadFileBytes(file);
				
				Chunk chunk =new Chunk(chunk_no, file_id,chunk_data,0);
				
				// wait a random delay
				Random rand = new Random();
				int  n = rand.nextInt(400) + 1;
				
				
				try {
					Thread.sleep(n);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				
				ArrayList<Chunk> chunks =Peer.getMDR().getSave(file_id);
				
				
				if(chunks != null)
				if(!chunks.contains(new Chunk(chunk_no, file_id, new byte[0], 0)))
						Peer.getMsgForwarder().sendCHUNK(chunk);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		Peer.getMDR().stopSave(file_id);
		
		
	}

	private void handleDELETE() {
		System.out.println("DELETE RECEIVED");
		
		String file_id = header[3];

		Peer.getDisk().deleteChunks(file_id);
	   
		
	}

	private void handleSTRORED() {
		System.out.println("STORED RECEIVED");
		
		int peer_id = Integer.parseInt(header[2]);
		String file_id=header[3];
		int chunk_no = Integer.parseInt(header[4]);
		
		String chunk_id = chunk_no + "_" +file_id; 
		
		Peer.getMC().save(chunk_id,peer_id );
		
		if(Peer.getDisk().isStored(new Chunk(chunk_no,file_id, new byte[0],0))) 
			Peer.getDisk().incRepDegree(chunk_id,Peer.getMC().getSaves(chunk_id)+1);
		
		
	}

	private void hdlPUTCHUNK() {
		System.out.println("PUTCHUNK RECEIVED");
		
		// chunk info from header
		String fileId=msgHeader[3];
		int chunkNr = Integer.parseInt(msgHeader[4]);
		int replication = Integer.parseInt(msgHeader[5]);
		
		// chunk data from body
		byte[] chunkData =Utils.parseBody(dataPacket);
		
		
		// create chunk 
		Chunk chunk = new Chunk(chunkNr,fileId,chunkData,replication );
		Peer.getMdb().save(chunk.getID(), 0);
		
		// stored chunk if not stored already
		if(!Peer.getDisk().isStored(chunk)) {
			Peer.getDisk().storeChunk(chunk);
		}
		
		// start saving STORED messages
		Peer.getMc().startSave(chunk.getID());
		
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
	
	public class chunkFilter implements FilenameFilter {
		
	       private String fileId;
		
	       public chunkFilter(String file_id) {
	         this.fileId = fileId;             
	       }
		       
	       public boolean accept(File dir, String name) {
	         return (name.endsWith(fileId));
	       }
	    }
	

}