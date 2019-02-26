import java.util.*;
import java.io.*;
import java.net.*;

public class Server {

    static DatagramSocket socket;
    static int portArg = 0;
    static Map<String,  String> db = new HashMap<String, String>();
    static InetAddress address;
    static String resString;
    static byte[] resPacket = new byte[1024];

    public static void main(String args[]) throws IOException{

        //int portArg = 0;
        if(args.length > 0){
            try{
                portArg = Integer.parseInt(args[0]);
            } catch (NumberFormatException e){
                System.err.println("Argument " + args[0] + " must be an integer.\n");
                System.exit(1);
            }
        }


        openConnection();

        db.put("111", "aaa");
        db.put("222", "bbb");
        db.put("333", "ccc");
        db.put("444", "ddd");

        while(true){
            
            byte[] rbuf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
            socket.receive(packet);
            address = packet.getAddress();
            portArg = packet.getPort();
            String receivedPacket = new String(packet.getData(), 0, packet.getLength());
            String[] receivedString = receivedPacket.split(" ");
            if(receivedString[0].equals("register")){
                registerRes(receivedString[1], receivedString[2]);
            }
            if(receivedString[0].equals("lookup")){
                lookupRes(receivedString[1]);
            }
        }
    }

    public static void openConnection() throws IOException{
        socket = new DatagramSocket(portArg);
        System.out.println("Server connected to socket on port " + portArg);
       
    }

    public static boolean lookupRes(String plate) throws IOException{   
        String ownerOfPlate = db.get(plate);
        if(ownerOfPlate == null){
            System.out.print("SERVER: lookup request, entry not found\n");
            resString = "There is NO entry for plate: " + plate + " in our database!";
            resPacket = resString.getBytes();
            DatagramPacket packet = new DatagramPacket(resPacket, resPacket.length ,address, portArg);
            socket.send(packet);
            return true;

        } else {
            resString = plate + ":" + ownerOfPlate;
            System.out.print("SERVER: lookup request. Found: \"" + resString + "\" \n");
            resPacket = resString.getBytes();
            DatagramPacket packet = new DatagramPacket(resPacket, resPacket.length, address, portArg);
            socket.send(packet);
            return true;
        } 
    }

    public static boolean registerRes(String plate, String owner) throws IOException{
        String putReturn = db.put(plate, owner);
        if(putReturn == null){      
            resString = "Entry \"" + plate + ":" + owner + "\" was added to the database!\n";
            System.out.println("SERVER: register request. " + resString);
            resPacket = resString.getBytes();
            DatagramPacket packet = new DatagramPacket(resPacket, resPacket.length ,address, portArg);
            socket.send(packet);
            return true;
        } else {
            resString = "Entry for  " + plate + "  was overwriten in our db, previous owner was "  + putReturn + " and is now " + owner + ". \n";
            System.out.println("SERVER: register request. " + resString);
            resPacket = resString.getBytes();
            DatagramPacket packet = new DatagramPacket(resPacket, resPacket.length,address, portArg);
            socket.send(packet);
            return true;
        }
    }
}
        