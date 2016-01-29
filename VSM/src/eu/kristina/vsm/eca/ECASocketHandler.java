package eu.kristina.vsm.eca;

import de.dfki.vsm.util.log.LOGDefaultLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @author Gregor Mehlmann
 */
public final class ECASocketHandler extends Thread {

    // The singelton logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The client socket
    private Socket mSocket;
    // The remote adress
    private final String mHost;
    private final int mPort;
    // The reader/writer
    private BufferedReader mReader;
    private BufferedWriter mWriter;
    // The thread flag
    private volatile boolean mDone = false;

    // Create the handler thread
    public ECASocketHandler(
            final String host,
            final int port) {
        mHost = host;
        mPort = port;
    }

    // Start the handler thread
    @Override
    public final void start() {
        try {
            // Initialize socket
            mSocket = new Socket(mHost, mPort);
            // Establish streams
            mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));
            mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8"));
            // Print some information
            mLogger.message("Starting ECA socket handler " + mSocket.getLocalSocketAddress() + " " + mSocket.getRemoteSocketAddress());
        } catch (final IOException exc) {
            mLogger.failure(exc.toString());
        }
        // Start thread
        super.start();
    }

    // Abort the handler thread
    public final void abort() {
        // Set the termination flag
        mDone = true;
        // Eventually close the socket
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (final IOException exc) {
                mLogger.failure(exc.toString());
            }
        }
        // Interrupt it if sleeping
        interrupt();
    }

    // Execute the handler thread
    @Override
    public final void run() {
        // Init the handshake
        send("12345");
        // TODO: Receive the handshake here

        //
        try {
            while (!mDone) {
                final String line = recv();
                if (line != null) {
                    mLogger.message("Reading Message'" + line + "'");
                } else {
                    abort();
                }
            }
        } catch (final Exception exc) {
            // Debug Some Information
            mLogger.failure(exc.toString());
        }
    }

    // Send a message via handler
    public final boolean send(final String string) {
        if (mWriter != null) {
            try {
                // Write message
                mWriter.write(string);
                mWriter.newLine();
                mWriter.flush();
                // Return success
                return true;
            } catch (final IOException exc) {
                mLogger.failure(exc.toString());
            }
        }
        // Return failure
        return false;
    }

    // Receive message via handler
    public final String recv() {
        if (mReader != null) {
            try {
                // Read message
                final String line = mReader.readLine();
                // Return message
                return line;
            } catch (final IOException exc) {
                mLogger.failure(exc.toString());
            }
        }
        // Return null
        return null;
    }
}
