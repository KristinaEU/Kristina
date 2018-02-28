package owlSpeak.kristina;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import owlSpeak.engine.ServletEngine;
import owlSpeak.servlet.OwlSpeakServlet;

public class VSMconnector {
	
	private static DatagramSocket mSocket ;
	private static int portNumber = 4444;
	private static String hostName = "127.0.0.1";
	private static int portNumberR = 4445;
	
	static String whereAmI = "http://beaver.e-technik.uni-ulm.de:8080/owlSpeak";
	static final String user = "user";
	static ServletEngine owlEngine;
	static KristinaDocument_alt systemMove;
	
	public static void main(String[] args){
		try { 
			System.setProperty("owlSpeak.settings.file", "C:/OwlSpeak/settings.xml");
			owlEngine = new ServletEngine();
			
			OwlSpeakServlet.reset(owlEngine, whereAmI, user);
			
			SocketAddress mLAddr = new InetSocketAddress(hostName, portNumber);
            // Create The UDP Socket
            mSocket = new DatagramSocket(mLAddr);
            // Connect The UDP Socket

                // Create The Addresses
                SocketAddress mRAddr = new InetSocketAddress(hostName, portNumberR);
                // Connect The UDP Socket
             mSocket.connect(mRAddr);	
			
			final byte[] buffer = new byte[4096];
	        final DatagramPacket packet
	                = new DatagramPacket(buffer, buffer.length);
			
			while (mSocket != null && !mSocket.isClosed()) {
				recv(packet);
				
				if(systemMove.isExitMove())
					break;
			}
		} catch(Exception e){
			System.err.println(e.getMessage());
		}
			
	}
	
	private static void send(String message){
		message = message+"\nnew Grammar";
		if (mSocket != null) {
            // Try To Receive New Data
            try {
                // Create The Datagram Buffer
                final byte[] buffer = message.getBytes("UTF-8");
                // Create The Datagram Packet
                final DatagramPacket packet
                        = new DatagramPacket(buffer, buffer.length);
                // Send The Datagram Packet
                mSocket.send(packet);
            } catch (final Exception exc) {
                // Debug Some Information
                System.err.println(exc.getMessage());
            }
        }
	}
	
	public static void recv(DatagramPacket packet) throws IOException{
		mSocket.receive(packet);
		
		final String message = new String(
                packet.getData(), 0,
                packet.getLength(), "UTF-8");
		
		if(message.equals("ready")){
			systemMove = (KristinaDocument_alt) (OwlSpeakServlet.processRequest(whereAmI, null, user, "0", owlEngine));
		}
		else{
			systemMove = (KristinaDocument_alt) (OwlSpeakServlet.processWork(whereAmI, systemMove.getAgenda(), message, "1", "Hallo", user, owlEngine)); 
		}
		send(systemMove.getSystemMove());
	}
	
}
