package core;

import java.io.Serializable;
import java.util.ArrayList;

public class FileSystem implements Serializable{

    long occupied;
    long available;
    ArrayList<Chunk> files;

    public FileSystem(){
        files = new ArrayList<Chunk>();
        occupied = 0;
        available = 4 * 1000000; //4MBytes
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

    public boolean backup(Chunk c){
        
        byte[] chunkBytes = c.getData();
        if((this.available - chunkBytes.length) < 0){
            //TODO: mensagem
            return false;
        }
        

    }
}