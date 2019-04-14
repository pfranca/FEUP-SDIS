package core;

import java.io.Serializable;
import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSystem implements Serializable{

    private static final long serialVersionUID = 1L; //TODO:

    long occupied;
    long available;
    ArrayList<Chunk> files;

    public FileSystem(){
        files = new ArrayList<Chunk>();
        occupied = 0;
        available = 4 * 1000000; //4MBytes

    File folder = new File("peer"+ Peer.getPeerId());
    if (!(folder.exists() && folder.isDirectory()))
        folder.mkdir();
    }

    public long getOccupied(){
        return this.occupied;
    }
    public long getAvailable(){
        return this.available;
    }
    public ArrayList<Chunk> getFiles(){
        return this.files;
    }
    public long getTotalSpace(){
        return (this.occupied + this.available);
    }

    public boolean storeChunk(Chunk c){ //TODO: name
        
        byte[] chunkBytes = c.getData();
        if((this.available - chunkBytes.length) < 0){
            return false;
        }

        FileOutputStream out;
		try {
			out = new FileOutputStream(Peer.BACKUP + c.getId());
			out.write(c.getData());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		// store in database
		files.add(c);
		
		try {
			Peer.saveDisk();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
        

    }

    //TODO: daqui pra baixo ta igual

    public boolean hasChunks() {
		return files.size() >0;
    }
    
    public boolean isStored(Chunk chunk) {
		return files.contains(chunk);
	}


	public ArrayList<Chunk> getStoredChunks() {
		return files;
    }
    public void incRepDegree(String chunkId, int rep) {
		
		
		for(int i=0; i< files.size();i++) {
			if(files.get(i).getId().equals(chunkId)) {
				files.get(i).setCurrentReplication(rep);
			}
		}
		
		try {
			Peer.saveDisk();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}