import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    public static void main(String args[]) throws IOException{

        int portArg = 0;
        if(args.length > 0){
            try{
                portArg = Integer.parseInt(args[0]);
            } catch (NumberFormatException e){
                System.err.println("Argument " + args[0] + " must be an integer.");
                System.exit(1);
            }
        }

        // DatagramSocket socket = new DatagramSocket();
        DatagramSocket socket = new DatagramSocket(portArg);

        while(true){
            byte[] rbuf = new byte[512];
            DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
            socket.receive(packet);
            String received = new String(packet.getData());
            System.out.println("Recebi: " + received);

        }

    }

    public static void openConnection(int port) throws IOException{

       //socket = new DatagramSocket(port);
    }

}
        