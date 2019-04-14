package core;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javafx.application.Preloader.PreloaderNotification;


public class Chunk implements Serializable{
  int chunkNr;
  String fileId;
  byte[] data;
  int replication;
  String id;
  int currentReplication;

  public static int MAX = 64*1000;

  private static final int NR_TO_SEND = 5;

  private static final long serialVersionUID = 1L; //TODO


  public Chunk(int chknr, String fId, byte[] d, int rep){
    chunkNr = chknr;
    fileId = fId;
    data = d;
    replication = rep;
    id = chknr + fId;//TODO
    currentReplication = 0;

  }

  @Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
    Chunk chunk = (Chunk) obj;
    boolean isEqualChunk = (this.chunkNr == chunk.getChunkNr() && this.fileId.equals(chunk.getFileId())); //TODO
    return isEqualChunk;
  }
    
	public int getReplication() {
		return this.replication;
  }
    
  public int getChunkNr() {
		return this.chunkNr;
	}
	
	public String getFileId() {
		return this.fileId;
	}
	
	public String getId() {
		return this.id;
  }

  public byte[] getData() {
		return this.data;
  }

	public void setCurrentReplication(int chkCopies) {
		this.currentReplication = chkCopies; 	
  }

  public int getCurrentReplication() {
		return this.currentReplication;
  }
  
  public void backup(){ //TODO: ref

    int msgSentCnt = 0;

    int backedUp = 0;

    while (backedUp < this.replication && msgSentCnt != NR_TO_SEND){
      Peer.getMdb().startSave(this.id); //TODO: ver

      Peer.getMsgForwarder().sendPUTCHUNK(this);
      msgSentCnt += 1;


      try{
        TimeUnit.SECONDS.sleep(1);

      } catch (InterruptedException e){
        //ignore exception
        e.printStackTrace();
      }
      backedUp = Peer.getMdb().getSaves(this.id);

      //TODO: increase sleep time?

    }
    Peer.getMdb().stopSave(this.id);

  }
}