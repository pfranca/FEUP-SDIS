package channels;

import java.io.IOException;
import core.Chunk;
import java.util.ArrayList;
import java.util.Hashtable;


public class MDR extends MC {
  private Hashtable<String, ArrayList<Chunk>> restore; 

  public MDR(String adrr, String gate) throws IOException{
      super(adrr, gate);
      restore = new Hashtable<String, ArrayList<Chunk>>();
  }

  public void save(String fileId, Chunk c) { 
    if (restore.containsKey(fileId))
      restore.get(fileId).add(c); 
  }

  public void startSave(String fileId) {
    restore.put(fileId, new ArrayList<Chunk>());
  }
  
  public ArrayList<Chunk> getSave(String fileId) { 
    if (restore.containsKey(fileId))
      return restore.get(fileId);
    return null;
  }
  
  public void stopSave(String fileId) {
    restore.remove(fileId);
  }
  
  public boolean isSaving(String chunkId) {
    return restore.containsKey(chunkId);
	}


}