package core;

import java.io.Serializable;
import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSystem implements Serializable{

    private static final long serialVersionUID = 1L;
    long occupied;
    long available;
    ArrayList<Chunk> files;

    public FileSystem(){
        files = new ArrayList<Chunk>();
        occupied = 0;
        available = 4 * 1000000; //4MBytes

        File f1 = new File(Peer.BACKUP);
    if (!(f1.exists() && f1.isDirectory()))
        f1.mkdirs();

        File f3 = new File(Peer.RESTORED);
    if (!(f3.exists() && f3.isDirectory()))
        f3.mkdirs();
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

    public boolean storeChunk(Chunk c){
        
        byte[] chunkBytes = c.getData();
        if((this.available - chunkBytes.length) < 0){
            return false;
        }

        File f2= new File(Peer.BACKUP + "/" +c.getFileId());
        if (!(f2.exists() && f2.isDirectory()))
        f2.mkdirs();

        FileOutputStream out;
		try {
			out = new FileOutputStream(Peer.BACKUP +"/"+c.getFileId()+"/chk"+ c.getChunkNr());
			out.write(c.getData());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		files.add(c);
		
		try {Peer.saveFs();} catch (IOException e) {e.printStackTrace();}
		
		return true;
        

    }
    public boolean hasChunks() {
		return this.files.size() >0;
    }
    
    public boolean isStored(Chunk c) {
		return this.files.contains(c);
    }
    
    public void incReplication(String chunkId, int rep) {
		for(int i=0; i< files.size();i++) {
			if(files.get(i).getId().equals(chunkId)) {
				files.get(i).setCurrentReplication(rep);
			}
		}
		try {Peer.saveFs();} catch (IOException e) {e.printStackTrace();}
	
	}

}