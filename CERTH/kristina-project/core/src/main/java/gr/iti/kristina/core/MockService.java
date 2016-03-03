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
package gr.iti.kristina.core;

import gr.iti.kristina.core.qa.QuestionAnswer;
import gr.iti.kristina.core.state.State;
import gr.iti.kristina.core.state.StateFactory;
import gr.iti.kristina.helpers.files.FileHelper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import org.openrdf.model.util.GraphUtilException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gmeditsk
 */
public class MockService {

    private State state;
    String serverUrl = "http://localhost:8084/graphdb-workbench-free",
            username = "kristina", password = "samiam#2";

    static Logger logger = LoggerFactory.getLogger(MockService.class);

    public MockService(boolean reloadStateRepository) throws FileNotFoundException {
        try {
            state = StateFactory.newInstance(serverUrl, username, password, reloadStateRepository);
        } catch (IOException | RepositoryException | RDFParseException | GraphUtilException | RepositoryConfigException | RDFHandlerException ex) {
            logger.error("", ex);
            if (state != null) {
                state.close();
            }
        }
    }

    public void updateState(String frameSituations) throws RepositoryConfigException, RepositoryException {
        state.updateState(frameSituations);
        QuestionAnswer qa = new QuestionAnswer(state);
        qa.start();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, RepositoryConfigException, RepositoryException {
        MockService mockService = new MockService(false);
        mockService.updateState(FileHelper.readFile("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/examples/inform.ttl", Charset.forName("utf-8")));
        mockService.shutDown();
    }

    private void shutDown() {
        if (state != null) {
            state.close();
        }
    }

}
