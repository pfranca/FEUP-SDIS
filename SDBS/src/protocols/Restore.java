package protocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import core.*;
import utils.Utils;

public class Restore implements Runnable {

	private File f;
	public Restore(String fPath) {
		this.f = new File(fPath);
	}

	@Override
	public void run() {
		String fId="";
		
		try {
			fId = Utils.getFileId(f);
		} catch (NoSuchAlgorithmException e) {e.printStackTrace();}

		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		Peer.getMdr().startSave(fId);
		
		int i=0;
		boolean end = false;					 
		do {	
			Peer.getMsgForwarder().sendGETCHUNK(i,fId);
			try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException exc) {exc.printStackTrace();}

			//System.out.println("PEERMDr");
			//System.out.println(Peer.getMdr());
			//System.out.println("MBD.GETSAVES");
			//System.out.println(Peer.getMdr().getSave(fId));
			
			Chunk chunk = Peer.getMdr().getSave(fId).get(i);
			if(chunk == null) {return;}
			
			if(chunk.getData().length != Chunk.MAX){end = true;}
			chunks.add(chunk);
			i+=1;			
		}while(end == false);
		
		Peer.getMdr().stopSave(fId);
		
		byte [] fData = new byte[0];
		byte[] aux = new byte[0];
		
		for(int j =0; j <chunks.size(); j++) {
			
			byte [] chunkData = chunks.get(j).getData();
			aux = new byte[fData.length + chunkData.length];
			System.arraycopy(fData, 0, aux, 0, fData.length);
			System.arraycopy(chunkData, 0, aux, fData.length, chunkData.length);
			fData = aux;
		}
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(Peer.RESTORED +f.getName());
			out.write(fData);
			out.close();
		} catch (FileNotFoundException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();}	
	}
}