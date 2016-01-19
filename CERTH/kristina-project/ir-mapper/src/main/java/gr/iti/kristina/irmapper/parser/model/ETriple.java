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
package gr.iti.kristina.irmapper.parser.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 *
 * @author gmeditsk
 */
public class ETriple {

    @SerializedName("Subject")
    private List<Resource> subject;
    @SerializedName("Predicate")
    private Resource predicate;
    @SerializedName("Object")
    private List<Resource> object;
    @SerializedName("ObjectSentence")
    private List<ETriple> ObjectSentence;

    private transient Resource parent;

    public List<Resource> getSubject() {
        return subject;
    }

    public void setSubject(List<Resource> subject) {
        this.subject = subject;
    }

    public Resource getPredicate() {
        return predicate;
    }

    public void setPredicate(Resource predicate) {
        this.predicate = predicate;
    }

    public List<Resource> getObject() {
        return object;
    }

    public void setObject(List<Resource> object) {
        this.object = object;
    }

    public List<ETriple> getObjectSentence() {
        return ObjectSentence;
    }

    public void setObjectSentence(List<ETriple> ObjectSentence) {
        this.ObjectSentence = ObjectSentence;
    }

    public Resource getParent() {
        return parent;
    }

    public void setParent(Resource parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "ETriple{" + "subject=" + subject + ", predicate=" + predicate + ", object=" + object + ", ObjectSentence=" + ObjectSentence + ", parent=" + parent + '}';
    }

}
