package gr.iti.kristina.helpers.repository.utils.chunkloader;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Helper class for loading large datasets with chunking.
 * Chunk loading works by loading portions (chunks)
 * of the dataset and committing the transaction after each chunk.
 */
public class ChunkLoader {
    private boolean preserveBnodeIds;
    private boolean parallelInsertion;
    private long chunkSize;

    public ChunkLoader(long chunkSize, boolean preserveBnodeIds, boolean parallelInsertion) {
        this.chunkSize = chunkSize;
        this.preserveBnodeIds = preserveBnodeIds;
        this.parallelInsertion = parallelInsertion;
    }

    public long loadFile(RepositoryConnection repositoryConnection, File file, Resource context)
            throws IOException, RDFParseException, RDFHandlerException, RepositoryException {
        RDFFormat format = RDFFormat.forFileName(file.getName());

        if (format == null) {
            throw new IOException("Unknown format for file.");
        }

        InputStream reader = null;

        try {
            if (file.getName().endsWith("gz")) {
                reader = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file), 256 * 1024));
            } else {
                reader = new BufferedInputStream(new FileInputStream(file), 256 * 1024);
            }

            long start = System.currentTimeMillis();

            ParserConfig config = new ParserConfig();
            config.set(BasicParserSettings.PRESERVE_BNODE_IDS, preserveBnodeIds);

            RDFParser parser = Rio.createParser(format);
            parser.setParserConfig(config);

            // add our own custom RDFHandler to the parser. This handler takes care of adding
            // triples to our repository and doing intermittent commits
            ChunkCommitter handler = new ChunkCommitter(repositoryConnection, context, chunkSize);
            parser.setRDFHandler(handler);

            repositoryConnection.begin();

            if (parallelInsertion) {
                URI up = new URIImpl("http://www.ontotext.com/useParallelInsertion");
                repositoryConnection.add(up, up, up);
            }

            Resource importContext = context == null ? new URIImpl(file.toURI().toString()) : context;
            parser.parse(reader, importContext.toString());

            repositoryConnection.commit();

            long statementsLoaded = handler.getStatementCount();
            long time = System.currentTimeMillis() - start;

            System.out.println("Loaded " + statementsLoaded + " statements in " + time + " ms; avg speed = " + (statementsLoaded * 1000 / time) + " st/s");

            return statementsLoaded;
        } catch (RepositoryException | RDFParseException | RDFHandlerException e) {
            try {
                repositoryConnection.rollback();
            } catch (RepositoryException ex) {
                // ignored
            }
            throw e;
        } finally {
            if (reader != null)
                reader.close();
        }
    }

}
