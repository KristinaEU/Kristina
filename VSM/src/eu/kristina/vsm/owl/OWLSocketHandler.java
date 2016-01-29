package eu.kristina.vsm.owl;

import de.dfki.vsm.util.log.LOGConsoleLogger;
import eu.kristina.vsm.KristinaScenePlayer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * @author Gregor Mehlmann
 */
public final class OWLSocketHandler extends Thread {

    // The singelton logger instance
    private final LOGConsoleLogger mLogger
            = LOGConsoleLogger.getInstance();
    // The thread termination flag
    private boolean mDone = false;
    // The datagram connection 
    private DatagramSocket mSocket;
    // The socket adress data
    private final Integer mLPort;
    private final Integer mRPort;
    private final String mLHost;
    private final String mRHost;
    private final Boolean mRFlag;
    // The both socket objects 
    private final SocketAddress mLAddr;
    private final SocketAddress mRAddr;
    // The scene player object
    private final KristinaScenePlayer mPlayer;

    // Create the handler thread
    public OWLSocketHandler(
            final KristinaScenePlayer player,
            final String lHost, final Integer lPort,
            final String rHost, final Integer rPort,
            final Boolean rFlag) {
        // Initialize proxy reference
        mPlayer = player;
        // Initialize the socket data
        mLHost = lHost;
        mLPort = lPort;
        mRHost = rHost;
        mRPort = rPort;
        mRFlag = rFlag;
        // Initialize the address data
        mLAddr = new InetSocketAddress(mLHost, mLPort);
        mRAddr = new InetSocketAddress(mRHost, mRPort);
    }

    // Start the handler thread
    @Override
    public final void start() {
        try {
            // Create the server socket
            mSocket = new DatagramSocket(mLAddr);
            // Connect the server socket
            if (mRFlag) {
                mSocket.connect(mRAddr);
            }
            // Print some information
            mLogger.message("Starting OWL socket handler " + mLAddr + " " + mRAddr);
            // Start the server thread
            super.start();
        } catch (final SocketException exc) {
            mLogger.failure(exc.toString());
        }
    }

    // Abort the handler thread
    public final void abort() {
        // Set the termination flag
        mDone = true;
        // Eventually close the socket
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.close();
        }
        // Interrupt it if sleeping
        interrupt();
    }

    // Execute the handler thread
    @Override
    public final void run() {
        // Receive while not done ...
        while (!mDone) {
            mLogger.message("Awaiting OWL events ...");
            // Receive a new message
            final String message = recvString();
            // Check message content
            if (message != null) {
                // Print some information
                mLogger.message("OWL socket handler receiving '" + message + "'");
            }
        }
    }

    // Send a message via the server
    public final boolean sendString(final String string) {
        try {
            // Create the byte buffer
            final byte[] buffer = string.getBytes("UTF-8");
            // Create the UDP packet
            final DatagramPacket packet
                    = new DatagramPacket(buffer, buffer.length);
            // And send the UDP packet
            mSocket.send(packet);
            // Return true at success
            return true;
        } catch (final IOException exc) {
            // Print some information
            mLogger.failure(exc.toString());
            // Return false at failure 
            return false;
        }
    }

    // Receive a sized byte array
    private byte[] recvBytes() {
        try {
            // Construct a byte array
            final byte[] buffer = new byte[4096];
            // Construct an UDP packet
            final DatagramPacket packet
                    = new DatagramPacket(buffer, buffer.length);
            // Receive the UDP packet
            mSocket.receive(packet);
            // Return the buffer now
            return Arrays.copyOf(buffer, packet.getLength());
        } catch (final IOException exc) {
            // Print some information
            mLogger.failure(exc.toString());
            // Return null at failure 
            return null;
        }
    }

    // Receive a string from socket
    private String recvString() {
        try {
            // Receive a byte buffer
            final byte[] buffer = recvBytes();
            // Check the buffer content
            if (buffer != null) {
                // Construct a message
                final String message
                        = new String(buffer, 0, buffer.length, "UTF-8");
                // And return message
                return message;
            }
        } catch (final UnsupportedEncodingException exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return null at failure 
        return null;
    }
}
