package eu.kristina.vsm.ssi;

import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.ScenePlayer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 *
 * @author Gregor Mehlmann
 */
public class SSIEventNotifier extends Thread {

    // The max event message size
    private final int sMAX_MSG_SIZE = 65536;
    // The default logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The thread termination flag
    private boolean mDone = false;
    // The datagram connection 
    private DatagramSocket mSocket;
    // The socket address data
    private final Integer mLPort;
    private final Integer mRPort;
    private final String mLHost;
    private final String mRHost;
    private final Boolean mRFlag;
    // The both socket objects 
    private final SocketAddress mLAddr;
    private final SocketAddress mRAddr;

    // Create the handler thread
    public SSIEventNotifier(
            final ScenePlayer player,
            final String lHost,
            final Integer lPort,
            final String rHost,
            final Integer rPort,
            final Boolean rFlag) {
        // Initialize the socket data
        mLHost = lHost;
        mLPort = lPort;
        mRHost = rHost;
        mRPort = rPort;
        mRFlag = rFlag;
        // Initialize the address data
        mLAddr = new InetSocketAddress(mLHost, mLPort);
        mRAddr = new InetSocketAddress(mRHost, mRPort);
        // Print some information
        //mLogger.message("Creating SSI event notifier " + mLAddr + " " + mRAddr);
    }

    // Start the handler thread
    @Override
    public final synchronized void start() {
        try {
            // Create the server socket
            mSocket = new DatagramSocket(mLAddr);
            // Connect the server socket
            if (mRFlag) {
                mSocket.connect(mRAddr);
            }
            // Print some information
            //mLogger.message("Starting SSI event notifier " + mLAddr + " " + mRAddr);
            // Start the server thread
            super.start();
        } catch (final SocketException exc) {
            mLogger.failure(exc.toString());
        }
    }

    // Abort the handler thread
    public final synchronized void abort() {
        // Set the termination flag
        mDone = true;
        // Eventually close the socket
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.close();
        }
        // Interrupt it if sleeping
        interrupt();
        // Print some information
        //mLogger.message("Aborting SSI event notifier " + mLAddr + " " + mRAddr);
    }

    // Execute the handler thread
    @Override
    public final synchronized void run() {
        // Receive while not done 
        while (!mDone) {
            try {
                wait();
            } catch (final Exception exc) {
                // Print some information
                mLogger.warning(exc.toString());
            }
        }
        // Print some information
        //mLogger.message("Terminating SSI event notifier " + mLAddr + " " + mRAddr);
    }

    // Send a message via the server
    public final synchronized boolean sendString(final String string) {
        try {
            // Create the byte buffer
            final byte[] buffer = string.getBytes("UTF-8");
            // Create the UDP packet
            final DatagramPacket packet
                    = new DatagramPacket(buffer, buffer.length, mRAddr);
            // And send the UDP packet
            mSocket.send(packet);
            // Print some information
            //mLogger.message("SSI event notifier sending '" + string + "'");
            // Return true at success
            return true;
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
            // Return false at failure 
            return false;
        }
    }
}
