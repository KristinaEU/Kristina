package eu.kristina.vsm.gti;

/**
 * @author Gregor Mehlmann
 */
public final class GTIActionFactory {

    // The static command id
    private static volatile long sId = 0;

    // Get a new command id
    private static synchronized long id() {
        return ++sId;
    }

    // Get a blink command
    public static String blink(
            final float duration
    ) {
        return "{" + "\n"
                + " \"cmdId\": " + id() + "," + "\n"
                + "  \"blink\": true" + ", \"blinkDuration\":" + duration + "\n"
                + "}";
    }

    // Get a facial command
    public static String face(
            final float valence,
            final float arousal,
            final float duration) {
        return "{" + "\n"
                + " \"cmdId\": " + id() + "," + "\n"
                + " \"face\": [" + valence + ", " + arousal + ", " + duration + "]" + "\n"
                + "}";
    }

    // Get a head command
    public static String head(
            final String direction,
            final float duration) {
        return "{" + "\n"
                + " \"cmdId\": " + id() + "," + "\n"
                + "  \"headGaze\": {\"direction\":\"" + direction + "\", \"duration\":" + duration + "}" + "\n"
                + "}";
    }

    // Get an eyes command
    public static String eyes(
            final String direction,
            final float duration) {
        return "{" + "\n"
                + " \"cmdId\": " + id() + "," + "\n"
                + "  \"eyeGaze\": {\"direction\":\"" + direction + "\", \"duration\":" + duration + "}" + "\n"
                + "}";
    }

    // Get an anim command
    public static String anim(
            final String name,
            final String mode,
            final float speed) {
        return "{" + "\n"
                + " \"cmdId\": " + id() + "," + "\n"
                + "  \"animation\": {\"gesture\":\"" + name + "\", \"mode\":\"" + mode + "\", \"speed\":" + speed + "}" + "\n"
                + "}";
    }

    // Get a speech command
    public static String speech() {
        return "{" + "\n"
                + " \"cmdId\": " + id() + "," + "\n"
                + "  \"audioURL\": \"http://www.webglstudio.org/gerard/visemes/es003_2.wav\"" + "," + "\n"
                + "  \"sequence\":"
                + "  [[0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.5],"
                + "   [1.0, 0.5, 0.0, 0.5, 1.0, 0.5, 1.0],"
                + "   [1.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]" + "\n"
                + "}";
    }
}
