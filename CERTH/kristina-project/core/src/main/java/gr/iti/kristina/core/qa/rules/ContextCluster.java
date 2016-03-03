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
package gr.iti.kristina.core.qa.rules;

import gr.iti.kristina.core.qa.Triple;
import gr.iti.kristina.helpers.functions.Print;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

/**
 *
 * @author gmeditsk
 */
public class ContextCluster implements Serializable {

    HashSet<Triple> triples;

    public ContextCluster() {
        triples = new HashSet<>();
    }
    
    public void add(Triple t){
        triples.add(t);
    }
    
    public void addAll(HashSet<Triple> triples){
        this.triples.addAll(triples);
    }

    public HashSet<Triple> getTriples() {
        return triples;
    }

    public void setTriples(HashSet<Triple> triples) {
        this.triples = triples;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.triples);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContextCluster other = (ContextCluster) obj;
        if (!Objects.equals(this.triples, other.triples)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ContextCluster{" + "triples=" + Print.flattenCollection(triples) + '}';
    }
    
    
    
    
    
    

}
