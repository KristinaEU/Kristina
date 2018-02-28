package eu.kristina.vsm.ssi;

import de.dfki.vsm.util.log.LOGDefaultLogger;
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
public final class SSIEventListener extends Thread {

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
    // The event handler object
    private final SSIEventHandler mHandler;

    // Create the handler thread
    public SSIEventListener(
            final SSIEventHandler handler,
            final String lHost,
            final Integer lPort,
            final String rHost,
            final Integer rPort,
            final Boolean rFlag) {
        // Initialize handler object
        mHandler = handler;
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
        mLogger.message("Creating SSI event listener " + mLAddr + " " + mRAddr);
    }

    // Start the handler thread
    @Override
    public final void start() {
        try {
            // Create the server socket
            mSocket = new DatagramSocket(mLAddr);

            // Connect the server socket
            if (mRFlag) {
                //
                //mSocket.bind(mRAddr);
                //
                mSocket.connect(mRAddr);
            }
            // Print some information
            mLogger.message("Starting SSI event listener " + mLAddr + " " + mRAddr);
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
        // Print some information
        mLogger.message("Aborting SSI event listener " + mLAddr + " " + mRAddr);
    }

    // Execute the handler thread
    @Override
    public final void run() {
        // Receive while not done 
        while (!mDone) {
            // Print some information
            //mLogger.message("Awaiting SSI event listener input ...");
            // Receive new SSI event
            final String event = recvString();
            // Print some information
            //mLogger.message("Received SSI event listener input '" + event + "'");
            // Check message content
            if (event != null) {
                // Handle the SSI event
                mHandler.handle(event);
            }
        }
        // Print some information
        mLogger.message("Aborting SSI event listener " + mLAddr + " " + mRAddr);
    }

    // Receive a sized byte array
    private byte[] recvBytes() {
        try {
            // Construct a byte array
            final byte[] buffer = new byte[sMAX_MSG_SIZE];
            // Construct an UDP packet
            final DatagramPacket packet
                    = new DatagramPacket(buffer, buffer.length);
            //
            //mLogger.message("Awaiting datagram packet");
            // Receive the UDP packet
            mSocket.receive(packet);
            // Print some information
            //mLogger.message("Receiving datagram packet '" + packet.toString() + "'");
            // Return the buffer now
            return Arrays.copyOf(buffer, packet.getLength());
        } catch (final IOException exc) {
            // Print some information
            mLogger.warning(exc.toString());
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
