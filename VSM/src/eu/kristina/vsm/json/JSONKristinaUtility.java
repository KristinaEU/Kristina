package eu.kristina.vsm.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONObject;

/**
 * @author Gregor Mehlmann
 */
public final class JSONKristinaUtility {

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
                mmap.put(key, merge(
                        (Map<String, Object>) obj,
                        (Map<String, Object>) val));
            } else {
                mmap.put(key, val);
            }
        }
        return mmap;
    }
}
