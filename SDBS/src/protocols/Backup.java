package protocols;

import java.io.*;
import utils.Utils;
import core.Chunk;
import java.util.*;
import java.security.*;

public class Backup implements Runnable{
    File f;
    int replication;

    public Backup(String fPath, int replication){
        this.replication = replication;
        f = new File(fPath);

    }

    public static byte[] loadFileBytes(File file) throws IOException  {
		FileInputStream fInStream = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fInStream.read(data);
		fInStream.close();
		return data;
	}

    public void run(){
        try{
        byte[] fileData = loadFileBytes(f);
        String fileId = Utils.getFileId(f);
        int nrOfChunks= fileData.length / (Chunk.MAX) + 1;

        for(int i =0; i < nrOfChunks; i++) {
            byte[] data;
            
            if(i == nrOfChunks-1) {
                if(fileData.length % Chunk.MAX == 0) {
                    data = new byte[0];
                }else {
                    data = Arrays.copyOfRange(fileData, i*Chunk.MAX, i*Chunk.MAX + (fileData.length % Chunk.MAX));
                }
            }else {
                data = Arrays.copyOfRange(fileData, i*Chunk.MAX, (i+1)*Chunk.MAX);
            }
    
            Chunk chunk=new Chunk(i,fileId,data,replication);
    
            chunk.backup();      
        }
        } catch (IOException e) {e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {e.printStackTrace();}
    }
}