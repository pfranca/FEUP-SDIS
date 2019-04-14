package msg;

import core.*;

public class MsgForwarder{
	
	public static String CRLF = "" + (char) 0xD + (char) 0xA;
	Double version;
	
	public MsgForwarder(double version) {
		this.version = version;
	}
	
	public byte[] createMessage(byte[] header, byte[]body) {
		
		byte[] message = new byte[header.length + body.length];
		System.arraycopy(header, 0, message, 0, header.length);
		System.arraycopy(body, 0, message, header.length, body.length);
		return message;
	}
	
	public void sendPUTCHUNK(Chunk c){
		String header = "PUTCHUNK";  
		header = sendAuxChunk(c, header);
		byte[] msg = createMessage(header.getBytes(), c.getData());
		Peer.getMdb().sendMsg(msg);
	}

	public void sendCHUNK(Chunk c) {
		String header = "CHUNK";
		header = sendAuxChunk(c, header);
		byte[] msg = createMessage(header.getBytes(), c.getData());
		Peer.getMdb().sendMsg(msg);
	}

	
	public void sendSTORED(Chunk c) {
		String header = "STORED";
		header+= " " + version; 
		header+= " " + Peer.getPeerId();
		header+= " " + c.getFileId();
		header+= " " + c.getChunkNr();
		header+= " " + CRLF + CRLF;
		Peer.getMc().sendMsg(header.getBytes());
	}
	
	public void sendDELETE(String fileId) {
		String header = "DELETE";
		header+= " " + version;
		header+= " " + Peer.getPeerId();
		header+= " " + fileId;
		header+= " " + CRLF + CRLF;
		Peer.getMc().sendMsg(header.getBytes());
	}

	public void sendGETCHUNK(int chunkNr, String fileId) {
		String header = "GETCHUNK";
		header = sendAux(chunkNr, fileId, header);
		Peer.getMc().sendMsg(header.getBytes());

	}

	public void sendREMOVED(int chunkNr, String fileId) {
		String header = "REMOVED";
		header = sendAux(chunkNr, fileId, header);
		Peer.getMc().sendMsg(header.getBytes());

	}

	private String sendAux(int chunkNr, String fileId, String header) {
		header += " " + version;
		header += " " + Peer.getPeerId();
		header += " " + fileId;
		header += " " + chunkNr;
		header += " " + CRLF + CRLF;
		return header;
	}


	private String sendAuxChunk(Chunk c, String header) {
		header += " " + version;
		header += " " + Peer.getPeerId();
		header += " " + c.getFileId();
		header += " " + c.getChunkNr();
		header += " " + c.getReplication();
		header += " " + CRLF + CRLF;
		return header;
	}


	

	

	
}