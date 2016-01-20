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
package gr.iti.kristina.core.repository;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gmeditsk
 */
public class OWLIMRepositoryFactory {

    private static Repository _this;
    private static RepositoryConnection _connection;
    private static RemoteRepositoryManager _manager;

    static Logger logger = LoggerFactory.getLogger(OWLIMRepositoryFactory.class);

    private OWLIMRepositoryFactory() {
    }

    public static Repository newIstance(String serverURL, String repositoryId, String username, String password) {
        if (_this == null) {
            try {
                _manager = new RemoteRepositoryManager(serverURL);
                _manager.setUsernameAndPassword(username, password);
                _manager.initialize();
                _this = _manager.getRepository(repositoryId);
                _connection = _this.getConnection();
            } catch (RepositoryException | RepositoryConfigException ex) {
                logger.error("", ex);
            }
        }
        return _this;
    }

    public static RepositoryConnection getConnection() {
        return _connection;
    }

    public static RemoteRepositoryManager getManager() {
        return _manager;
    }

    public void shutDown() {
        if (_this != null) {
            try {
                _connection.close();
                _this.shutDown();
                _manager.shutDown();
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }
}
