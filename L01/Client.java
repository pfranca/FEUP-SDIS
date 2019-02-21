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
        if(args.length < 4){
            System.out.println("Usage: Client <host_name> <port_number> <oper> <opnd>*");
        }else{
            host = InetAddress.getByName(args[0]);
            port = Integer.parseInt(args[1]);
            operation = args[2];
            plate = args[3];
            if(operation.equals("register")){/*plate = args[3];*/ owner = args[4];}
            //if(operation.equals("lookup")) plate = args[3];
        }

        /*System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Op: " + operation);
        System.out.println("Plate: " + plate);
        System.out.println("Owner: " + owner);*/

        String requestString = operation + " " + plate;
        if(operation.equals("register")) requestString += " "+ owner;

        System.out.println("request " + requestString);


        //send request
        DatagramSocket socket = new DatagramSocket();
        byte[] request = requestString.getBytes();
        DatagramPacket packet = new DatagramPacket(request, request.length, host, port);
        socket.send(packet);
/*

        //get response
        byte[] rbuf = new byte[sbuf.length];
        packet = new DatagramPacket(rbuf, rbuf.length);
        socket.receive(packet);

        //display response
        String received = new String(packet.getData());
        System.out.println("Recebi: " + received);

*/
    }


}
        