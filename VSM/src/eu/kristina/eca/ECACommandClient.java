package eu.kristina.eca;

import de.dfki.vsm.util.log.LOGConsoleLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @author Gregor Mehlmann
 */
public final class ECACommandClient extends Thread {

    // The singelton logger instance
    private final LOGConsoleLogger mLogger
            = LOGConsoleLogger.getInstance();
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

    public ECACommandClient(
            final String host,
            final int port) {
        mHost = host;
        mPort = port;
    }

    @Override
    public final void start() {
        try {
            // Initialize socket
            mSocket = new Socket(mHost, mPort);
            // Establish streams
            mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));
            mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8"));
        } catch (final IOException exc) {
            mLogger.failure(exc.toString());
        }
        // Start thread
        super.start();
    }

    public final void init() {
        send("12345");
    }

    public final void abort() {
        // Set the flag
        mDone = true;
        // Close socket 
        if ((mSocket != null) && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (final IOException exc) {
                mLogger.failure(exc.toString());
            }
        }
    }

    @Override
    public final void run() {
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

    public final boolean send(final String string) {
        if (mWriter != null) {
            try {
                // Write message
                mWriter.write(string);
                //mWriter.newLine();
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
