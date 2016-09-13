package eu.kristina.vsm.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONObject;

/**
 * @author Gregor Mehlmann
 */
public final class KristinaUtility {

    public final static String merge(
            final String lstr, final String rstr, final int indent) {
        final JSONObject lobj = new JSONObject(lstr);
        final JSONObject robj = new JSONObject(rstr);
        final JSONObject mobj = merge(lobj, robj);
        return mobj.toString(indent);
    }

    public final static JSONObject merge(
            final JSONObject lobj, final JSONObject robj) {
        final Map<String, Object> lmap = lobj.toMap();
        final Map<String, Object> rmap = robj.toMap();
        return new JSONObject(merge(lmap, rmap));
    }

    public final static Map<String, Object> merge(
            final Map<String, Object> lmap,
            final Map<String, Object> rmap) {
        final Map<String, Object> mmap = new HashMap();
        for (final Entry<String, Object> entry : lmap.entrySet()) {
            mmap.put(entry.getKey(), entry.getValue());
        }
        for (final Entry<String, Object> entry : rmap.entrySet()) {
            final String key = entry.getKey();
            final Object val = entry.getValue();
            if (mmap.containsKey(key)) {
                final Object obj = mmap.get(key);
                try {
                    mmap.put(key, merge(
                            (Map<String, Object>) obj,
                            (Map<String, Object>) val));
                } catch (final ClassCastException exc) {
                    System.err.println(exc.toString());
                }
            } else {
                mmap.put(key, val);
            }
        }
        return mmap;
    }

    public final static String read(final String filename) {
        int len;
        char[] chr = new char[4096];
        try {
            final File file = new File(filename);
            final StringBuffer buffer = new StringBuffer();
            final FileReader reader = new FileReader(file);

            while ((len = reader.read(chr)) > 0) {
                buffer.append(chr, 0, len);
            }
            reader.close();
            return buffer.toString();
        } catch (final IOException exc) {
            exc.printStackTrace();
        }
        return null;
    }
}
