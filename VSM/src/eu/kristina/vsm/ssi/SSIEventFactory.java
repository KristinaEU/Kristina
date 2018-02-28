package eu.kristina.vsm.ssi;

/**
 *
 * @author Gregor Mehlmann
 */
public final class SSIEventFactory {

    public static final String createEvent(
            final String content) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<events>"
                + "<event"
                + " sender=\"" + "VSM" + "\""
                + " event=\"" + "STATUS" + "\""
                + " type=\"" + "STRING" + "\""
                + " state=\"" + "COMPLETED" + "\""
                + " glue=\"" + "0" + "\""
                + " from=\"" + "0" + "\""
                + " dur=\"" + "0" + "\""
                + " prob=\"" + "1.0" + "\">"
                + "<![CDATA[" + content + "]]>"
                + "</event>"
                + "</events>";
    }
}
