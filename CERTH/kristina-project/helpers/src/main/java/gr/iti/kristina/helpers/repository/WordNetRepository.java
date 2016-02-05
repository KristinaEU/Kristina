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
package gr.iti.kristina.helpers.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashSet;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author gmeditsk
 */
public class WordNetRepository extends GraphDbRepositoryManager {

    public static String wordnetURI = "http://localhost:8084/graphdb-workbench-free/repositories/wordnet";
    public static String babelnetURI = "http://babelnet.org/sparql/";
    private static String REP_ID = "wordnet";

    public WordNetRepository(String serverURL, String username, String password) {
        super(serverURL, username, password);
    }

    public static String call(HttpHost target, HttpGet httpget, CloseableHttpClient httpclient, HttpClientContext context)
            throws ClientProtocolException, IOException {

        CloseableHttpResponse response = httpclient.execute(target, httpget, context);
        HttpEntity entity = response.getEntity();
        String json = EntityUtils.toString(entity, "UTF-8");
        EntityUtils.consume(entity);

        return json;

    }

    public static String getWordNetSynsetFromBabelNet(String babelNetSynsetURI) throws UnsupportedEncodingException, IOException {
        String query = "SELECT ?wnsynset \n"
                + "WHERE {\n"
                + "<" + babelNetSynsetURI + "> <http://www.w3.org/2004/02/skos/core#exactMatch> ?wnsynset.\n"
                + "FILTER (contains(str(?wnsynset), \"http://wordnet-rdf.princeton.edu/wn31/\"))"
                + "}";

        HttpGet httpget = new HttpGet(babelnetURI + "?query=" + URLEncoder.encode(query, "utf-8")
                + "&format=" + URLEncoder.encode("application/json", "utf-8")
                + "&key=" + URLEncoder.encode("cc071edd-7b70-44d0-88ce-32ae5618528a", "utf-8"));
//        httpget.setHeader("Accept",
//                "application/json;charset=utf-8");

        HttpHost target = new HttpHost("babelnet.org");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();
        String json = call(target, httpget, httpclient, context);
        System.out.println(json);
        JsonParser parser = new JsonParser();
        JsonObject rootObj = parser.parse(json).getAsJsonObject();
        JsonArray locObj = rootObj.getAsJsonObject("results").getAsJsonArray("bindings");
        String wnsynset = locObj.get(0).getAsJsonObject().get("wnsynset").getAsJsonObject().get("value").getAsString();
        return wnsynset;
    }

    //test with: http://babelnet.org/rdf/s00029895n
    public static HashSet<String> getWordNetHypernymsFromBabelNet(String babelNetSynsetURI) throws URISyntaxException, IOException {

        String wnsynset = getWordNetSynsetFromBabelNet(babelNetSynsetURI);

        //DefaultHttpClient httpclient = new DefaultHttpClient();
        //get hypernyms
        String query = "PREFIX wordnet-ontology: <http://wordnet-rdf.princeton.edu/ontology#>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "select ?h where { \n"
                + "	<" + wnsynset + "> wordnet-ontology:hypernym+ ?h .\n"
                + " FILTER (contains(str(?h), \"http://wordnet-rdf.princeton.edu/wn31/\"))"
                + "} ";

        HttpHost target = new HttpHost("localhost", 8084, "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials("kristina", "samiam#2"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();

        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(target, basicAuth);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        System.out.println(URLEncoder.encode(query, "utf-8"));
        HttpGet httpget = new HttpGet(wordnetURI + "?query=" + URLEncoder.encode(query, "utf-8"));
        httpget.setHeader("Accept", "application/sparql-results+json;charset=UTF-8");

        String json = call(target, httpget, httpclient, localContext);
        System.out.println(json);

        JsonParser parser = new JsonParser();
        JsonObject rootObj = parser.parse(json).getAsJsonObject();
        JsonArray locObj = rootObj.getAsJsonObject("results").getAsJsonArray("bindings");
        JsonArray asJsonArray = locObj.getAsJsonArray();
        HashSet<String> hs = new HashSet();
        for (JsonElement e : asJsonArray) {
            hs.add(e.getAsJsonObject().get("h").getAsJsonObject().get("value").getAsString());
        }
        return hs;
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        WordNetRepository.getWordNetHypernymsFromBabelNet("http://babelnet.org/rdf/s00029895n");

    }

}
