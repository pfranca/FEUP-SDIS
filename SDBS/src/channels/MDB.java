package channels;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

public class MDB extends MC {
  private Hashtable<String, HashSet<Integer>> hash; 

  public MDB(String adrr, String gate) throws IOException{
      super(adrr, gate);
      hash = new Hashtable<String, HashSet<Integer>>();
  }


  public void save(String chunkId, int peerId) {
    if (hash.containsKey(chunkId))
    hash.get(chunkId).add(peerId);
	}

  public void startSave(String chunkId) {
    hash.put(chunkId, new HashSet<Integer>());
  }
  
  public int getSaves(String chunkId) {
    if(hash.get(chunkId) == null) return 0;
    return hash.get(chunkId).size();
  }
  
  public void stopSave(String chunkId) {
    hash.remove(chunkId);
  }
}
