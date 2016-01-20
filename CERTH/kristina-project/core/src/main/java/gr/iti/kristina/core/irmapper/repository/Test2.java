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
package gr.iti.kristina.core.irmapper.repository;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.ontotext.jena.SesameDataset;
import com.ontotext.trree.OwlimSchemaRepository;
import java.io.File;
import org.openrdf.model.Model;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sail.SailRepository;

/**
 *
 * @author gmeditsk
 */
public class Test2 {

    public static void main(String[] args) throws RepositoryException, RepositoryConfigException {

        OwlimSchemaRepository schema = new OwlimSchemaRepository();
        schema.setDataDir(new File("./local-sotrage"));
        SailRepository repository = new SailRepository(schema);
        repository.initialize();
        RepositoryConnection connection = repository.getConnection();
        SesameDataset dataset = new SesameDataset(connection);

        Model model = (Model) ModelFactory.createModelForGraph(dataset.getDefaultGraph());

    }

}
