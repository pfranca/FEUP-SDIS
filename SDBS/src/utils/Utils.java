package utils;

import java.security.*;

import core.Peer;

import java.io.*;

import java.nio.charset.StandardCharsets;

//TODO: ref
public class Utils{
    public static String getFileID(File file) throws NoSuchAlgorithmException {
		String file_id = file.getName() + file.lastModified() + Peer.getPeerId();
		
		// sha-256
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(file_id.getBytes(StandardCharsets.UTF_8));
		
		// byte[] to hex string
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[hash.length * 2];
	    for ( int j = 0; j < hash.length; j++ ) {
	        int v = hash[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
		
		
	}
}