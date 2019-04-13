package channels;

import java.io.IOError;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

public class MDB extends MC {
  private Hashtable<String, HashSet<Integer>> logs; 

  public MDB(String adrr, String port) throws IOException{
      super(adrr, port);
      logs = new Hashtable<String, HashSet<Integer>>();
  }

  public void startSave(String chunkId) {
    logs.put(chunkId, new HashSet<Integer>());
  }
  
  public int getSaves(String chunkId) {
    if(logs.get(chunkId) == null) return 0;
    return logs.get(chunkId).size();
  }
  
  public void stopSave(String chunkId) {
    logs.remove(chunkId);
  }
  
  public void save(String chunkId, int peerId) {
    if (logs.containsKey(chunkId))
      logs.get(chunkId).add(peerId);
	}
}