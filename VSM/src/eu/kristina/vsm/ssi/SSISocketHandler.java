package eu.kristina.vsm.ssi;

import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.VSMKristinaPlayer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Gregor Mehlmann
 */
public final class SSISocketHandler extends Thread {

    // The singelton logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
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
    private final VSMKristinaPlayer mPlayer;

    // Create the handler thread
    public SSISocketHandler(
            final VSMKristinaPlayer player,
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
            mLogger.message("Starting SSI socket handler " + mLAddr + " " + mRAddr);
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
            // Receive a new message
            final String message = recvString();
            // Check message content
            if (message != null) {

                // Parse the SSI message
                parse(message);
                // Print some information
                //mLogger.message("SSI socket handler receiving:\n" + message + "");
            }
        }
    }

    // Parse an SSI event object
    private void parse(final String xml) {
        // Print some information
        //mLogger.message("Parsing message " + xml + "");
        try {
            // Parse the received XML string
            final ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(stream);
            // Get the XML tree root element
            final Element element = document.getDocumentElement();
            // Check if we have SSI events 
            if (element.getTagName().equals("events")) {
                // Get the list of SSI events
                final NodeList eventList = element.getElementsByTagName("event");
                // Check if the list is empty
                if (eventList.getLength() > 0) {
                    // Process the indvidual SSI events
                    for (int i = 0; i < eventList.getLength(); i++) {
                        final Element event = ((Element) eventList.item(i));

                        // Get the event attributes
                        final String mode = event.getAttribute("sender");
                        final String name = event.getAttribute("event");
                        final String type = event.getAttribute("type");
                        final String state = event.getAttribute("state");
                        final Integer glue = Integer.parseInt(event.getAttribute("glue"));
                        final Integer from = Integer.parseInt(event.getAttribute("from"));
                        final Integer dur = Integer.parseInt(event.getAttribute("dur"));
                        final Double prob = Double.parseDouble(event.getAttribute("prob"));

                        // Process the event features
                        if (mode.equalsIgnoreCase("audio")) {
                            if (name.equalsIgnoreCase("voice") || name.equalsIgnoreCase("vad")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    // User stopped speaking
                                    mLogger.message("User stopped speaking");
                                    // Try to set a variable
                                    if (mPlayer != null) {
                                        mPlayer.set("UserRoleActivity", "Speaking", false);
                                    }
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // User started speaking
                                    mLogger.message("User started speaking");
                                    // Try to set a variable
                                    if (mPlayer != null) {
                                        mPlayer.set("UserRoleActivity", "Speaking", true);
                                    }
                                } else {
                                    // Cannot process this
                                }
                            } else if (name.equalsIgnoreCase("speech")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("string")) {
                                        // Just get the content
                                        final String text = event.getTextContent();
                                        // User said something
                                        mLogger.message("User just said '" + text + "'");
                                        // Try to set a variable
                                        if (mPlayer != null) {
                                            mPlayer.set("UserDialogMove", text);
                                        }
                                    } else {
                                        // Cannot process this    
                                    }
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // Cannot process this
                                } else {
                                    // Cannot process this
                                }
                            } else {
                                // Cannot process this
                            }
                        } else if (mode.equalsIgnoreCase("fsender") || mode.equalsIgnoreCase("fusion")) {
                            if (name.equalsIgnoreCase("fevent") || name.equalsIgnoreCase("fusion")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("ntuple")) {
                                        // Get the list of tuples
                                        final NodeList tuples = element.getElementsByTagName("tuple");
                                        for (int j = 0; j < tuples.getLength(); j++) {
                                            // Get the nex tuple element
                                            final Element tuple = ((Element) tuples.item(j));
                                            // Get the tuple's attributes
                                            final String key = tuple.getAttribute("string");
                                            final Double val = Double.parseDouble(tuple.getAttribute("value"));
                                            // User said something
                                            //mLogger.message("Fusion result '" + key + "' is '" + val + "'");
                                            // Try to set a variable
                                            if (mPlayer != null) {
                                                // Append this to the record
                                                // String.format(Locale.US, "%.6f", val)
                                                mPlayer.set("UserAffectState", key, val.floatValue());
                                            }
                                        }
                                    } else {
                                        // Cannot process this    
                                    }
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // Cannot process this
                                } else {
                                    // Cannot process this
                                }
                            } else {
                                // Cannot process this
                            }
                        } else if (mode.equalsIgnoreCase("upf")) {
                            if (name.equalsIgnoreCase("la")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("string")) {
                                        // Just get the content
                                        final String text = event.getTextContent();
                                         // Print some information
                                        mLogger.message("Language analysis result is\n" + text + "");
                                    } else {
                                        // Cannot process this    
                                    }
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // Cannot process this
                                } else {
                                    // Cannot process this
                                }                               
                            } else {
                                // Cannot process this
                            }
                        } else {
                            // Cannot process this
                        }

                    }
                }
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
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
            final byte[] buffer = new byte[16384];
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
