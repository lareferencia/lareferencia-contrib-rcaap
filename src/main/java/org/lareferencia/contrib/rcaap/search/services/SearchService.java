package org.lareferencia.contrib.rcaap.search.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.util.NamedList;
import org.lareferencia.contrib.rcaap.search.merge.EntityType;
import org.lareferencia.contrib.rcaap.search.server.ISearchEngineClient;
import org.lareferencia.contrib.rcaap.search.server.ISearchEngineClientResolver;
import org.lareferencia.contrib.rcaap.search.server.model.AbstractBaseEntity;
import org.lareferencia.contrib.rcaap.search.server.model.EntityStatusType;
import org.lareferencia.contrib.rcaap.search.server.model.Person;
import org.lareferencia.contrib.rcaap.search.services.solr.FacetSolr;
import org.lareferencia.contrib.rcaap.search.services.solr.FacetSolrJ;
import org.lareferencia.contrib.rcaap.search.services.solr.FacetValueSolrJ;
import org.lareferencia.contrib.rcaap.search.services.solr.FacetSolrJ.FacetSolrBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class SearchService {

    private final ISearchEngineClient client;

    private static final int NUM_ROWS = 100;

    public SearchService(ISearchEngineClientResolver resolver, EntityType entityType) {
        this.client = resolver.resolve(entityType);
    }

    public ISearchEngineClient getClient() {
        return this.client;
    }

    public void indexDocuments(List<Person> docs) throws Exception {
        client.addAll(docs);
        client.commit();
    }

    public List<Person> searchAll() throws Exception {
        return client.query("*:*", Person.class);
    }


    @SuppressWarnings("unchecked")
    public Optional<IFacet> findSinglesFacetsByNetwork(String facetedField, String networkAcronym) throws Exception {

        SolrQuery query = new SolrQuery("*:*");

        if (networkAcronym != null && !networkAcronym.isEmpty()) {
            String escapedNetwork =
                    ClientUtils.escapeQueryChars(networkAcronym);

            query.addFilterQuery("network_acronym_str:\"" + escapedNetwork + "\"");
        }

        query.addFilterQuery("-(-status:\"" + EntityStatusType.NEW + "\" status:*)");

        // JSON Facet API
        String jsonFacet = "{ " +
                "  uniqueValues: { " +
                "    type: terms, " +
                "    field: \"" + facetedField + "\", " +
                "    limit: -1, " +
                "    facet: { " +
                "      docCount: \"count\"" +
                "    }" +
                "  }" +
                "}";

        query.set("json.facet", jsonFacet);
        query.setRows(0); 

        QueryResponse response = client.rawQuery(query);

        NamedList<Object> facets = (NamedList<Object>) response.getResponse().get("facets");
        if (facets == null) {
            return Optional.empty();
        }

        NamedList<Object> uniqueValues = (NamedList<Object>) facets.get("uniqueValues");
        if (uniqueValues == null) {
            return Optional.empty();
        }

        List<NamedList<Object>> buckets = (List<NamedList<Object>>) uniqueValues.get("buckets");
        if (buckets == null || buckets.isEmpty()) {
            return Optional.empty();
        }
        List<IFacetValue> singles = new ArrayList<>();

        for (NamedList<Object> bucket : buckets) {
            String value = (String) bucket.get("val");
            long count = ((Number) bucket.get("docCount")).longValue();

            if (count == 1) {
                singles.add(new FacetValueSolrJ.FacetValueSolrBuilder(null)
                        .fromString(facetedField, value, count)
                        .build());
            }
        }


        IFacet facetField = new FacetSolrJ.FacetSolrBuilder()
                                    .fromFacetValuesSolrJ(facetedField,singles)
                                    .build();

        return Optional.of(facetField);
    }


    public Page<? extends AbstractBaseEntity> findSingleDocumentsByFacets(
                String field, 
                String networkAcronym, 
                List<String> singleFacets,
                Class<? extends AbstractBaseEntity> clazz, 
                int start, 
                int rows
            ) throws Exception {


        SolrQuery query = new SolrQuery("*:*");

        query.setStart(start);
        if (rows == 0) {
            // prevent dividing by 0
            rows = 1;
        }
        query.setRows(rows);
        int page = start / rows;

        if (!singleFacets.isEmpty()) {

            List<String> escaped = singleFacets.stream()
                    //.map(ClientUtils::escapeQueryChars)
                    //.map(v -> "\"" + v + "\"")
                    .collect(Collectors.toList());

            if (escaped.isEmpty()) {
                throw new IllegalArgumentException("No valid values");
            }

            query.addFilterQuery( field + ":(\"" + String.join("\" OR \"",escaped) + "\")");

        }

        if (networkAcronym != null && !networkAcronym.isEmpty()) {
            String escapedNetwork =
                    ClientUtils.escapeQueryChars(networkAcronym);

            query.addFilterQuery("network_acronym_str:\"" + escapedNetwork + "\"");
        }

        // only new entries or entries without status
        query.addFilterQuery("-(-status:\"" + EntityStatusType.NEW.toString() + "\" status:*)");

        QueryResponse response = client.rawQuery(query);

        Pageable pageable = PageRequest.of(page, rows);

        return
                new PageImpl<>(
                    response.getBeans(clazz),
                    pageable,
                    response.getResults().getNumFound()
                );

    }

    public Page<? extends AbstractBaseEntity> findSingleDocumentsByDuplicatedFacets(
                String field, 
                String networkAcronym, 
                Optional<IFacet> duplicatedFacets,
                Class<? extends AbstractBaseEntity> clazz, 
                int start, 
                int rows
            ) throws Exception {


        SolrQuery query = new SolrQuery("*:*");

        query.setStart(start);
        if (rows == 0) {
            // prevent dividing by 0
            rows = 1;
        }
        query.setRows(rows);
        int page = start / rows;

        if (duplicatedFacets.isPresent()) {
            Page<? extends IFacetValue> duplicatedFacetValues = duplicatedFacets.get().getFacetValues();
            for (IFacetValue duplicatedFacetValue : duplicatedFacetValues) {
                query.addFilterQuery("-" + field + ":\"" + ClientUtils.escapeQueryChars(duplicatedFacetValue.getValue()) + "\"");
            }
        }

        if (networkAcronym != null && !networkAcronym.isEmpty()) {
            String escapedNetwork =
                    ClientUtils.escapeQueryChars(networkAcronym);

            query.addFilterQuery("network_acronym_str:\"" + escapedNetwork + "\"");
        }

        // only new entries or entries without status
        query.addFilterQuery("-(-status:\"" + EntityStatusType.NEW.toString() + "\" status:*)");

        QueryResponse response = client.rawQuery(query);

        Pageable pageable = PageRequest.of(page, rows);

        return
                new PageImpl<>(
                    response.getBeans(clazz),
                    pageable,
                    response.getResults().getNumFound()
                );

    }



    public Optional<IFacet> findAllDuplicatesFacetsByNetwork(String facetedField, String networkAcronym) throws Exception {
        // only NOT merged records
        SolrQuery query = new SolrQuery("*:*" );

        if (networkAcronym != null && !networkAcronym.isEmpty()) {
            String escapedNetwork =
                    ClientUtils.escapeQueryChars(networkAcronym);

            query.addFilterQuery("network_acronym_str:\"" + escapedNetwork + "\"");
        }

        return findDuplicates(query, facetedField);
    }

    public Optional<IFacet> findNewDuplicatesFacetsByNetwork(String facetedField, String networkAcronym) throws Exception {
        // only NOT merged records
        SolrQuery query = new SolrQuery("*:*" );

        if (networkAcronym != null && !networkAcronym.isEmpty()) {
            String escapedNetwork =
                    ClientUtils.escapeQueryChars(networkAcronym);

            query.addFilterQuery("network_acronym_str:\"" + escapedNetwork + "\"");
        }

        // all NEW status items
        query.addFilterQuery("-(-status:\"" + EntityStatusType.NEW.toString() + "\" status:*)");

        return findDuplicates(query, facetedField);
    }

    public Optional<IFacet> findDuplicates(SolrQuery query, String facetedField) throws Exception {

        query.setRows(0);
        query.setFacet(true);
        query.addFacetField(facetedField);
        query.setFacetMinCount(2); //duplicates only
        query.setFacetLimit(-1);  //all values
        //query.setFacetLimit(10000);

        QueryResponse response = client.rawQuery(query);
        IFacet facets = new FacetSolrJ.FacetSolrBuilder()
                                    .fromFacetField(response.getFacetField(facetedField))
                                    .build();

        return Optional.of(facets);
    }

    /*public List<FacetField.Count> findDuplicateBySemanticIdentifiers() throws Exception {

        SolrQuery query = new SolrQuery("*:*");

        query.setRows(0); // no rows
        query.setFacet(true);
        query.addFacetField("semanticIdentifier_str_mv");
        query.setFacetMinCount(2); //duplicates only
        //query.setFacetLimit(-1);  //all values
        query.setFacetLimit(10000);

        QueryResponse response = client.rawQuery(query);
        FacetField facetField = response.getFacetField("semanticIdentifier_str_mv");

        if (facetField == null) {
            return Collections.emptyList();
        }

        return facetField.getValues();
    }*/


    public <T> List<T> findDocumentsByFieldValue(String field, String value,
            Class<T> clazz) throws Exception {
        return findDocumentsByFieldValues(field, Arrays.asList(value), clazz);
    }

    public <T> List<T> findDocumentsByFieldValues(String field, List<String> values,
            Class<T> clazz, int start, int rows, Aggregator<T> total) throws Exception {

        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values cannot be empty");
        }

        List<String> escaped = values.stream()
                //.map(ClientUtils::escapeQueryChars)
                //.map(v -> "\"" + v + "\"")
                .collect(Collectors.toList());

        if (escaped.isEmpty()) {
            throw new IllegalArgumentException("No valid values");
        }

        String q = field + ":(\"" + String.join("\" OR \"",escaped) + "\")";

        SolrQuery query = new SolrQuery(q);
        query.setStart(start);
        query.setRows(rows);

        QueryResponse response = client.rawQuery(query);

        if (start == 0) {
            total.setTotal(response.getResults().getNumFound());
        }

        return response.getBeans(clazz);
    }
    

    public <T> List<T> findDocumentsByFieldValues(String field, List<String> values,
            Class<T> clazz) throws Exception {

        List<T> results = new ArrayList<>();

        int start = 0;

        Aggregator<T> aggregator = new Aggregator<>();
        aggregator.setTotal(Long.MAX_VALUE);

        while (start < aggregator.getTotal()) {
            List<T> batch = findDocumentsByFieldValues(field, values, clazz, start, NUM_ROWS, aggregator);
            results.addAll(batch);
            start += NUM_ROWS;
        }

        return results;
    }

    private class Aggregator<T> {
        List<T> results;
        Long total;

        public void setResults (List<T> results) {
            this.results = results;
        }

        public void setTotal (Long total) {
            this.total = total;
        }

        public Long getTotal () {
            return this.total;
        }

        public List<T> getResults () {
            return this.results;
        }
    }
}