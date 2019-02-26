import java.util.*;
import java.io.*;
import java.net.*;

public class Client {

    static InetAddress host;
    static int port;
    static String operation;
    static String plate;
    static String owner;

    public static void main(String args[]) throws IOException{

        //int portArg = 0;
        if(args.length < 3){
            System.out.println("Usage: Client <host_name> <port_number> <oper> <opnd>*");
        }else{
            host = InetAddress.getByName(args[0]);
            port = Integer.parseInt(args[1]);
            operation = args[2];
            plate = args[3];
            if(operation.equals("register")){owner = args[4];}
        }

        String requestString = operation + " " + plate;
        if(operation.equals("register")) requestString += " "+ owner;
        System.out.println("REQUEST: " + requestString);

        //send request
        DatagramSocket socket = new DatagramSocket();
        byte[] request = requestString.getBytes();
        DatagramPacket packet = new DatagramPacket(request, request.length, host, port);
        socket.send(packet);

        //get response
        byte[] response = new byte[1024];
        DatagramPacket packet2 = new DatagramPacket(response, response.length);
        socket.receive(packet2);
  

        //display response
        String received = new String(packet2.getData(), 0, packet2.getLength());
        System.out.println("REPLY: " + received);
    }

}
        