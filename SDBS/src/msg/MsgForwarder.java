package msg;


import java.util.Arrays;
import utils.Utils;
import core.Chunk;
import core.Peer;
//TODO:ref

public class MsgForwarder{
	Double version;
	
    public static byte CR = 0xD;
    public static byte LF = 0xA;
    public static String CRLF = "" + (char) CR + (char) LF;
	
	public MsgForwarder(double version) {
		this.version = version;
	}
	
	public byte[] createMessage(byte[] header, byte[]body) {
		
		byte[] msg = new byte[header.length + body.length];
		System.arraycopy(header, 0, msg, 0, header.length);
		System.arraycopy(body, 0, msg, header.length, body.length);
		
		return msg;
	}
	
	public void sendPUTCHUNK(Chunk chunk){
		String header = "PUTCHUNK"  
						+ " " + version 
						+ " " + Peer.getPeerId()
						+ " " + chunk.getFileId()
						+ " " + chunk.getChunkNr()
						+ " " + chunk.getReplication()
						+ " " + CRLF + CRLF;

		
		byte[] msg = createMessage(header.getBytes(),chunk.getData());
		Peer.getMdb().sendMsg(msg);
						
	}
	
	public void sendCHUNK(Chunk chunk) {
		String header = "CHUNK"  
				+ " " + version 
				+ " " + Peer.getPeerId()
				+ " " + chunk.getFileId()
				+ " " + chunk.getChunkNr()
				+ " " + chunk.getReplication()
				+ " " + CRLF + CRLF;


		byte[] msg = createMessage(header.getBytes(),chunk.getData());
		Peer.getMdr().sendMsg(msg);
				
	}
	
	public void sendSTORED(Chunk chunk) {
		String header = "STORED"
						+ " " + version 
						+ " " + Peer.getPeerId()
						+ " " + chunk.getFileId()
						+ " " + chunk.getChunkNr()
						+ " " + CRLF + CRLF;
		
		Peer.getMc().sendMsg(header.getBytes());
	}
	
	public void sendDELETE(String fileid) {
		String header = "DELETE"
						+ " " + version
						+ " " + Peer.getPeerId()
						+ " " + fileid
						+ " " + CRLF + CRLF;
		
		Peer.getMc().sendMsg(header.getBytes());
	}

	public void sendGETCHUNK(int chunkNr, String fileId) {
		String header = "GETCHUNK"
				+ " " + version 
				+ " " + Peer.getPeerId()
				+ " " + fileId
				+ " " + chunkNr
				+ " " + CRLF + CRLF;
		

		Peer.getMc().sendMsg(header.getBytes());
		
	}

	public void sendREMOVED(int chunkNr, String fileId) {
		String header = "REMOVED"
				+ " " + version 
				+ " " + Peer.getPeerId()
				+ " " + fileId
				+ " " + chunkNr
				+ " " + CRLF + CRLF;
		

		Peer.getMc().sendMsg(header.getBytes());
		
		
	}



	

	

	
}