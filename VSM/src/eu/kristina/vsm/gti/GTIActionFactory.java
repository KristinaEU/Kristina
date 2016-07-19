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
    public static String blink(final float duration) {
        return "{" + "\n"
                + "  \"cmdId\": " + id() + "," + "\n"
                + "  \"blink\": true" + "," + "\n"
                + "  \"blinkDuration\":" + duration + "\n"
                + "}";
    }

    // Get a face command
    public static String face(
            final float valence,
            final float arousal,
            final float duration) {
        return "{" + "\n"
                + "  \"cmdId\": " + id() + "," + "\n"
                + "  \"faceShift\": {" + "\n"
                + "    \"start\": " + 0 + "," + "\n"
                + "    \"end\": " + duration + "," + "\n"
                + "    \"valaro\": [" + valence + ", " + arousal + "]" + "\n"
                + "  }" + "\n"
                + "}";
    }

    // Get an eyes command
    public static String eyes(
            final float speed,
            final float angle,
            final String direction) {
        return "{" + "\n"
                + "  \"cmdId\": " + id() + "," + "\n"
                + "  \"gazeShift\": {" + "\n"
                + "    \"start\": " + 0 + "," + "\n"
                + "    \"end\": " + speed + "," + "\n"
                + "    \"target\": \"CAMERA\"" + "," + "\n"
                + "    \"influence\": \"EYES\"" + "," + "\n"
                + "    \"offsetAngle\": " + angle + "," + "\n"
                + "    \"offsetDirection\": \"" + direction + "\"\n"
                + "  }" + "\n"
                + "}";
    }

    // Get a gaze command
    public static String gaze(
            final float speed,
            final float angle,
            final String direction) {
        return "{" + "\n"
                + "  \"cmdId\": " + id() + "," + "\n"
                + "  \"gazeShift\": {" + "\n"
                + "    \"start\": " + 0 + "," + "\n"
                + "    \"end\": " + speed + "," + "\n"
                + "    \"target\": \"CAMERA\"" + "," + "\n"
                + "    \"influence\": \"HEAD\"" + "," + "\n"
                + "    \"offsetAngle\": " + angle + "," + "\n"
                + "    \"offsetDirection\": \"" + direction + "\"\n"
                + "  }" + "\n"
                + "}";
    }

    // Get a head command
    public static String head(
            final float speed,
            final float angle,
            final String direction) {
        return "{" + "\n"
                + "  \"cmdId\": " + id() + "," + "\n"
                + "  \"headDirectionShift\": {" + "\n"
                + "    \"start\": " + 0 + "," + "\n"
                + "    \"end\": " + speed + "," + "\n"
                + "    \"target\": \"CAMERA\"" + "," + "\n"
                + "    \"offsetAngle\": " + angle + "," + "\n"
                + "    \"offsetDirection\": \"" + direction + "\"\n"
                + "  }" + "\n"
                + "}";
    }

    // Get a nod command
    public static String nod(
            final float amount,
            final int repetition) {
        return "{" + "\n"
                + "  \"cmdId\": " + id() + "," + "\n"
                + "  \"head\": {" + "\n"
                + "    \"start\": " + 0 + "," + "\n"
                + "    \"ready\": " + 1 + "," + "\n"
                + "    \"strokeStart\": " + 1 + "," + "\n"
                + "    \"stroke\": " + 2 + "," + "\n"
                + "    \"strokeEnd\": " + 3 + "," + "\n"
                + "    \"relax\": " + 3 + "," + "\n"
                + "    \"end\": " + 2 + "," + "\n"
                + "    \"lexeme\": \"NOD\"" + "," + "\n"
                + "    \"amount\": " + amount + "," + "\n"
                + "    \"repetition\": \"" + repetition + "\"\n"
                + "  }" + "\n"
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
