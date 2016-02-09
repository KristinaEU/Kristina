package eu.kristina.vsm.gti;

/**
 * @author Gregor Mehlmann
 */
public final class GTIActionFactory {

    // The static command id
    private static volatile long sId = 0;

    // Get a new command id
    private static synchronized long newCmdId() {
        return ++sId;
    }

    // Get a blink command
    public static String blink() {
        return "{" + "\n"
                + " \"cmdId\": " + newCmdId() + "," + "\n"
                + " \"blink\": true" + "\n"
                + "}";
    }

    // Get a facial command
    public static String face(
            final float valence,
            final float arousal) {
        return "{" + "\n"
                + " \"cmdId\": " + newCmdId() + "," + "\n"
                + " \"face\": [" + valence + ", " + arousal + "]" + "\n"
                + "}";
    }

    public static String __speech() {
        return "{" + "\n"
                + " \"cmdId\": " + newCmdId() + "," + "\n"
                + " \"audioURL\": \"http://www.webglstudio.org/gerard/visemes/es003_2.wav\"" + "," + "\n"
                + " \"sequence\":"
                + " [[0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.5],"
                + "  [1.0, 0.5, 0.0, 0.5, 1.0, 0.5, 1.0],"
                + "  [1.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]" + "\n"
                + "}";
    }

}
