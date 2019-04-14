package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import core.Chunk;
import msg.MsgHandler;


public class MC implements Runnable{
    public MulticastSocket multicastSocket;
    public InetAddress multicastAddress;
    public int multicastGate;
    public static final int TTL = 1;

    public MC(String adr, String gate) throws IOException{

        this.multicastAddress = InetAddress.getByName(adr);
        this.multicastGate = Integer.parseInt(gate);

        multicastSocket = new MulticastSocket(multicastGate);
        multicastSocket.setTimeToLive(TTL);
        multicastSocket.joinGroup(multicastAddress);

        
    }

    public synchronized void sendMsg(byte[] message){
        DatagramPacket dataPacket = new DatagramPacket(message , message.length, multicastAddress, multicastGate);
        try{
            multicastSocket.send(dataPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        boolean end = false;

        int bufferSize = 1024;

        byte[] buffer = new byte[bufferSize + Chunk.MAX];

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

    

}