package org.lareferencia.contrib.rcaap.search.server;

import java.util.List;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

public class SolrSearchEngineClient implements ISearchEngineClient {

    private final SolrClient solrClient;

    public SolrSearchEngineClient(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    @Override
    public <T> void add(T document) throws Exception {
        solrClient.addBean(document);
    }

    @Override
    public <T> void addAll(List<T> documents) throws Exception {
        solrClient.addBeans(documents);
    }

    @Override
    public void commit() throws Exception {
        solrClient.commit();
    }

    @Override
    public <T> List<T> query(String query, Class<T> clazz) throws Exception {
        SolrQuery solrQuery = new SolrQuery(query);
        QueryResponse response = solrClient.query(solrQuery);

        return response.getBeans(clazz);
    }

    @Override
    public QueryResponse rawQuery(SolrQuery query) throws Exception {
        return solrClient.query(query);
    }

    @Override
    public <T> void update(T document) throws Exception {
        solrClient.addBean(document);
    }

    @Override
    public void deleteById(String id) throws Exception {
        solrClient.deleteById(id);
    }

    @Override
    public void deleteByQuery(SolrQuery query) throws Exception {
        solrClient.deleteByQuery(query.toString());
    }

}