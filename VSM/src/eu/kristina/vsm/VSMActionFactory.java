package eu.kristina.vsm;

/**
 *
 * @author Gregor Mehlmann
 */
public class VSMActionFactory {

    // The command id
    private static volatile long sId = 0;

    private static synchronized long newCmdId() {
        return ++sId;
    }

    public static String newBlinkAction() {
        return "{" + "\n"
                + " \"cmdId\": " + newCmdId() + "," + "\n"
                + " \"blink\": true" + "\n"
                + "}";
    }

    public static String newFaceAction(
            final float valence,
            final float arousal) {
        return "{" + "\n"
                + " \"cmdId\": " + newCmdId() + "," + "\n"
                + " \"face\": [" + valence + ", " + arousal + "]" + "\n"
                + "}";
    }

    public static String newVoiceAction() {
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
