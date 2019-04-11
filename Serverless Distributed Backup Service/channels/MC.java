package channels;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MC implements Runnable{
    public MulticastSocket multicastSocket;
    public InetAddress multicastAddress;
    public int multicastPort;

    public MC(String address, String port) throws IOException{

        this.multicastAddress = InetAddress.getByName(address);
        this.multicastPort = Integer.parseInt(port);

        multicastSocket = new MulticastSocket(multicastPort);
        multicastSocket.setTimeToLive(1);
        multicastSocket.joinGroup(multicastAddress);
    }

    public void run(){

    }

}