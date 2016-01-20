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
package gr.iti.kristina.core.irmapper.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author gmeditsk
 */
public class ExResource implements Serializable, Comparable<ExResource> {

    @SerializedName("term")
    private String term;
    @SerializedName("concept")
    private String concept;
    @SerializedName("BabelNet")
    private String babelNet;
    @SerializedName("DBPedia")
    private String dbPedia;
    private transient String localId;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getBabelNet() {
        return babelNet;
    }

    public void setBabelNet(String babelNet) {
        this.babelNet = babelNet;
    }

    public String getDbPedia() {
        return dbPedia;
    }

    public void setDbPedia(String dbPedia) {
        this.dbPedia = dbPedia;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final ExResource other = (ExResource) obj;
        if (!Objects.equals(this.term, other.term)) {
            return false;
        }
        if (!Objects.equals(this.concept, other.concept)) {
            return false;
        }
        if (!Objects.equals(this.babelNet, other.babelNet)) {
            return false;
        }
        if (!Objects.equals(this.dbPedia, other.dbPedia)) {
            return false;
        }
        if (!Objects.equals(this.localId, other.localId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ExResource{" + "term=" + term + ", concept=" + concept + ", babelNet=" + babelNet + ", dbPedia=" + dbPedia + ", localId=" + localId + '}';
    }

    @Override
    public int compareTo(ExResource o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
