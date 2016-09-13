package eu.kristina.vsm.test;

import eu.kristina.vsm.util.KristinaUtility;

/**
 * @author Gregor Mehlmann
 */
public final class MergingTestSuite {

    // Execute the json test
    public static final void main(final String args[]) {
        testMerge();
    }

    private static void testMerge() {

        final String l1 = "{\"0\":\"l\"}";
        final String r1 = "{\"0\":\"r\"}";
        final String m1 = KristinaUtility.merge(l1, r1, 4);
        System.out.println(l1);
        System.out.println(r1);
        System.out.println(m1);

        final String l2 = "{\"0\":{\"1\":\"l\"}}";
        final String r2 = "{\"0\":{\"2\":\"r\"}}";
        final String m2 = KristinaUtility.merge(l2, r2, 4);
        System.out.println(l2);
        System.out.println(r2);
        System.out.println(m2);

        final String json_lg = KristinaUtility.read("res/exp/output_language_generation.txt");
        final String json_ms = KristinaUtility.read("res/exp/output_mode_selection.txt");
        final String merge = KristinaUtility.merge(json_lg, json_ms, 2);
        System.out.println(merge);
    }

}
