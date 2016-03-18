/*
 * The MIT License
 *
 * Copyright 2016 gmeditsk.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package gr.iti.kristina.helpers.functions;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author gmeditsk
 */
public class Print {

    public static <K, V> void printMap(Multimap<K, V> source) {
        Set<K> keys = source.keySet();
        for (K key : keys) {
            Collection<V> value = source.get(key);
            System.out.printf(" - %-30s %s \n", key instanceof URI ? ((URI) key).getLocalName()
                    : key,
                    flattenCollection(value));
        }
    }

    public static String flattenCollection(Collection col) {
        String result = "[\n";
        for (Object object : col) {
            if (object instanceof URI) {
                result += ((URI) object).getLocalName() + ", \n ";
            } else {
                result += object + ", \n";
            }
        }
        result += "]";
        return result;
    }

    public static String printValue(Value v) {
        String s = v.stringValue();
        if (s.contains("#")) {
            return StringUtils.substringAfterLast(v.stringValue(), "#");
        } 
        
        if (s.contains("/")) {
            return StringUtils.substringAfterLast(v.stringValue(), "/");
        }
        return s;

    }

}
