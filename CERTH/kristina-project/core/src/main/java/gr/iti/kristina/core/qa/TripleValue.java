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
package gr.iti.kristina.core.qa;

import gr.iti.kristina.helpers.functions.Print;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.openrdf.model.Value;

/**
 *
 * @author gmeditsk
 */
public class TripleValue implements Serializable, Comparable<TripleValue> {

    public Value s, p, o;
    public List<TripleValue> connections;
    public int level;

    public TripleValue(Value s, Value p, Value o) {
        this.s = s;
        this.p = p;
        this.o = o;

        connections = new ArrayList<>();
    }
    
    

    @Override
    public String toString() {
        return "<" + "" + Print.printValue(s) + ", " + Print.printValue(p) + ", " + Print.printValue(o) + '>';
    }

    @Override
    public int compareTo(TripleValue o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.s);
        hash = 89 * hash + Objects.hashCode(this.p);
        hash = 89 * hash + Objects.hashCode(this.o);
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
        final TripleValue other = (TripleValue) obj;
        if (!Objects.equals(this.s, other.s)) {
            return false;
        }
        if (!Objects.equals(this.p, other.p)) {
            return false;
        }
        if (!Objects.equals(this.o, other.o)) {
            return false;
        }
        return true;
    }

    public void add(TripleValue t) {
        this.connections.add(t);
    }

    public Value getS() {
        return s;
    }

    public void setS(Value s) {
        this.s = s;
    }

    public Value getP() {
        return p;
    }

    public void setP(Value p) {
        this.p = p;
    }

    public Value getO() {
        return o;
    }

    public void setO(Value o) {
        this.o = o;
    }

    public List<TripleValue> getConnections() {
        return connections;
    }

    public void setConnections(List<TripleValue> connections) {
        this.connections = connections;
    }

}
