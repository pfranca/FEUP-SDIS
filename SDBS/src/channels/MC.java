package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import core.Chunk;
import msg.MsgHandler;
import java.util.HashSet;
import java.util.Hashtable;


public class MC implements Runnable{
    public MulticastSocket multicastSocket;
    public InetAddress multicastAddress;
    public int multicastPort;
    private Hashtable<String, HashSet<Integer>> logs; 

    public MC(String address, String port) throws IOException{

        this.multicastAddress = InetAddress.getByName(address);
        this.multicastPort = Integer.parseInt(port);

        multicastSocket = new MulticastSocket(multicastPort);
        multicastSocket.setTimeToLive(1);
        multicastSocket.joinGroup(multicastAddress);

        logs = new Hashtable<String, HashSet<Integer>>();
    }

    public void run(){
        boolean end = false;

        byte[] buffer = new byte[1024 + Chunk.MAX];//mudar tamanho

        while(end == false){
            try{
                DatagramPacket dataPacket = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(dataPacket);
                new Thread(new MsgHandler(dataPacket)).start();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        multicastSocket.close();
    }

    public synchronized void sendMsg(byte[] msg){
        DatagramPacket dataPacket = new DatagramPacket(msg , msg.length, multicastAddress, multicastPort);
        try{
            multicastSocket.send(dataPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}