package test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPTest {
	
	public static void main(String args[]) throws Exception
    {
       DatagramSocket serverSocket = new DatagramSocket(1337);
          byte[] receiveData = new byte[1024];
          byte[] sendData = new byte[1024];
          while(true)
             {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String( receivePacket.getData());
                System.out.println("RECEIVED: " + sentence);
                
             }
    } 
}
