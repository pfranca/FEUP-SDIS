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
		String header = "PUTCHUNK";  
				header = sendAuxCunk(chunk, header);
	}

	public void sendCHUNK(Chunk chunk) {
		String header = "CHUNK";
			header = sendAuxCunk(chunk, header);
	}

	
	public void sendSTORED(Chunk chunk) {
		String header = "STORED";
						header+= " " + version; 
						header+= " " + Peer.getPeerId();
						header+= " " + chunk.getFileId();
						header+= " " + chunk.getChunkNr();
						header+= " " + CRLF + CRLF;
		
		Peer.getMc().sendMsg(header.getBytes());
	}
	
	public void sendDELETE(String fileid) {
		String header = "DELETE";
						header+= " " + version;
						header+= " " + Peer.getPeerId();
						header+= " " + fileid;
						header+= " " + CRLF + CRLF;
		
		Peer.getMc().sendMsg(header.getBytes());
	}

	public void sendGETCHUNK(int chunkNr, String fileId) {
		String header = "GETCHUNK";
			header = sendAux(chunkNr, fileId, header);
			
	}

	public void sendREMOVED(int chunkNr, String fileId) {
		String header = "REMOVED";
			header = sendAux(chunkNr, fileId, header);

	}


	//AUXILIAR SEND FUNCTIONS 

	private String sendAux(int chunkNr, String fileId, String header) {
		header += " " + version;
		header += " " + Peer.getPeerId();
		header += " " + fileId;
		header += " " + chunkNr;
		header += " " + CRLF + CRLF;

		Peer.getMc().sendMsg(header.getBytes());
	}


	private String sendAuxCunk(Chunk chunk, String header) {
		header += " " + version;
		header += " " + Peer.getPeerId();
		header += " " + chunk.getFileId();
		header += " " + chunk.getChunkNr();
		header += " " + chunk.getReplication();
		header += " " + CRLF + CRLF;

		byte[] msg = createMessage(header.getBytes(), chunk.getData());
		Peer.getMdb().sendMsg(msg);
	}


	

	

	
}