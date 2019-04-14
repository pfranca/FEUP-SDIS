package protocols;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import utils.Utils;
import core.Peer;

public class Delete implements Runnable {
	private File f;

	public Delete(String fPath) {
		f = new File(fPath); 
	}

	@Override
	public void run() {
		String fileId="";
		
		try {
			fileId = Utils.getFileID(f);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		Peer.getMsgForwarder().sendDELETE(fileId);
		
	}

}