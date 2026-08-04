package org.lareferencia.contrib.rcaap.search.server;

import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

public interface ISearchEngineClient {
    <T> void add(T document) throws Exception;

    <T> void addAll(List<T> documents) throws Exception;

    void commit() throws Exception;

    <T> List<T> query(String query, Class<T> clazz) throws Exception;

    QueryResponse rawQuery(SolrQuery query) throws Exception;

    <T> void update(T document) throws Exception;

    void deleteById(String id) throws Exception;

    void deleteByQuery(SolrQuery query) throws Exception;
}