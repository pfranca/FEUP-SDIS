package utils;

import java.security.*;
import core.Peer;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Utils{

	//https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java

	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String getFileId(File file) throws NoSuchAlgorithmException {
		String file_id = file.getName() + file.lastModified() + Peer.getPeerId();
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(file_id.getBytes(StandardCharsets.UTF_8));
		
		char[] hexChars = new char[hash.length * 2];
	    for ( int j = 0; j < hash.length; j++ ) {
	        int v = hash[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}