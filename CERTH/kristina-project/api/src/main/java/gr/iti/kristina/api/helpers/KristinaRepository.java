/*
 * The MIT License
 *
 * Copyright 2015 gmeditsk.
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
package gr.iti.kristina.api.helpers;

import com.franz.agraph.http.exception.AGHttpException;
import com.franz.agraph.jena.AGGraphMaker;
import com.franz.agraph.repository.AGCatalog;
import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGServer;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gmeditsk
 */
public class KristinaRepository {

    public static String SERVER_URL = "http://localhost:10035";
    public static String CATALOG_ID = "Kristina";
    public static String REPOSITORY_ID = "MS2-Repository";
    public static String USERNAME = "admin";
    public static String PASSWORD = "Paran01@!#10";

    private final AGGraphMaker maker;
    private final AGRepositoryConnection conn;
    AGRepository myRepository;

    Logger logger = LoggerFactory.getLogger(KristinaRepository.class);

    public KristinaRepository() throws AGHttpException, RepositoryException {
        logger.debug("Connecting to Kristina Repository...");
        AGServer server = new AGServer(SERVER_URL, USERNAME, PASSWORD);
        AGCatalog catalog = server.getCatalog(CATALOG_ID);
        myRepository = catalog.createRepository(REPOSITORY_ID);
        conn = myRepository.getConnection();
        maker = new AGGraphMaker(conn);
        logger.debug("Done.");
    }

    public AGGraphMaker getMaker() {
        return maker;
    }

    public void shutDown() throws RepositoryException {
        logger.debug("Closing Kristina Repository...");
        maker.close();
        conn.close();
        myRepository.shutDown();
        logger.debug("Done.");
    }

}
