package eu.kristina.vsm.gti;

/**
 * @author Gregor Mehlmann
 */
public final class ActionFactory {

    // Get a blink command
    public static String blink(final float duration) {
        return "{" + "\n"
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
                + "  \"head\": {" + "\n"
                + "    \"start\": " + 0 + "," + "\n"
                + "    \"ready\": " + 0.25 + "," + "\n"
                + "    \"strokeStart\": " + 0.25 + "," + "\n"
                + "    \"stroke\": " + 0.5 + "," + "\n"
                + "    \"strokeEnd\": " + 0.75 + "," + "\n"
                + "    \"relax\": " + 0.75 + "," + "\n"
                + "    \"end\": " + 1 + "," + "\n"
                + "    \"lexeme\": \"NOD\"" + "," + "\n"
                + "    \"amount\": " + amount + "," + "\n"
                + "    \"repetition\": " + repetition + "\n"
                + "  }" + "\n"
                + "}";
    }

    // Get a shake command
    public static String shake(
            final float amount,
            final int repetition) {
        return "{" + "\n"
                + "  \"head\": {" + "\n"
                + "    \"start\": " + 0 + "," + "\n"
                + "    \"ready\": " + 0.25 + "," + "\n"
                + "    \"strokeStart\": " + 0.25 + "," + "\n"
                + "    \"stroke\": " + 0.5 + "," + "\n"
                + "    \"strokeEnd\": " + 0.75 + "," + "\n"
                + "    \"relax\": " + 0.75 + "," + "\n"
                + "    \"end\": " + 1 + "," + "\n"
                + "    \"lexeme\": \"SHAKE\"" + "," + "\n"
                + "    \"amount\": " + amount + "," + "\n"
                + "    \"repetition\": " + repetition + "\n"
                + "  }" + "\n"
                + "}";
    }

    // Get a shake command
    public static String tilt(
            final float amount,
            final int repetition) {
        return "{" + "\n"
                + "  \"head\": {" + "\n"
                + "    \"start\": " + 0 + "," + "\n"
                + "    \"ready\": " + 0.25 + "," + "\n"
                + "    \"strokeStart\": " + 0.25 + "," + "\n"
                + "    \"stroke\": " + 0.5 + "," + "\n"
                + "    \"strokeEnd\": " + 0.75 + "," + "\n"
                + "    \"relax\": " + 0.75 + "," + "\n"
                + "    \"end\": " + 1 + "," + "\n"
                + "    \"lexeme\": \"TILT\"" + "," + "\n"
                + "    \"amount\": " + amount + "," + "\n"
                + "    \"repetition\": " + repetition + "\n"
                + "  }" + "\n"
                + "}";
    }

    // Get an anim command
    public static String anim(
            final String name,
            final String mode,
            final float speed) {
        return "{" + "\n"
                + "  \"animation\": {" + "\n"
                + "    \"gesture\":\"" + name + "\"" + "," + "\n"
                + "    \"mode\":\"" + mode + "\"" + "," + "\n"
                + "    \"speed\":" + speed + "\n"
                + "  }" + "\n"
                + "}";
    }
}
