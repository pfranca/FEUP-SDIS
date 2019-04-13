package channels;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
//TODO

//import chunk

public class MDR extends MC {
  private Hashtable<String, ArrayList<Integer>> restore; //mudar integer para chunck;

  public MDR(String adrr, String port) throws IOException{
      super(adrr, port);
      restore = new Hashtable<String, ArrayList<Integer>>();//mudar int-chunk
  }

  public void startSave(String fileId) {
    restore.put(fileId, new ArrayList<Integer>());
  }
  
  public ArrayList<Integer> getSave(String fileId) { //mudar int-chunck
    if (restore.containsKey(fileId))
      return restore.get(fileId);
    return null;
  }
  
  public void stopSave(String fileId) {
    restore.remove(fileId);
  }
  
  public void save(String fileId, int intdf) { //mudar int intfd - Chunck chucnhk
    if (restore.containsKey(fileId))
      restore.get(fileId).add(intdf); //mudar int df - chunck
  }
  
  public boolean isSaving(String chunkId) {
    return restore.containsKey(chunkId);
	}


}