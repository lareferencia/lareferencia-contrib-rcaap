
/*
 *   Copyright (c) 2013-2022. LA Referencia / Red CLARA and others
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   This file is part of LA Referencia software platform LRHarvester v4.x
 *   For any further information please contact Lautaro Matas <lmatas@gmail.com>
 */

package org.lareferencia.contrib.rcaap.search.services.solr;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.lareferencia.contrib.rcaap.search.model.Facet;
import org.lareferencia.contrib.rcaap.search.model.Field;
import org.lareferencia.contrib.rcaap.search.services.IFacet;
import org.lareferencia.contrib.rcaap.search.services.ISearchConfigurationContext;
import org.lareferencia.contrib.rcaap.search.services.ISearchResult;
import org.lareferencia.contrib.rcaap.search.services.SearchConfigurationContext;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFacetService;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFieldService;
import org.lareferencia.core.entity.indexing.service.IEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.solr.core.query.result.FacetPage;

public class SearchResultSolr implements ISearchResult {
    private Page<? extends IEntity> results;
    private Collection<? extends IFacet> facets;

    // TODO: try to use it as an injection
    private ISearchConfigurationContext searchConfigurationContext;

    public static SearchResultSolr fromPage(ISearchConfigurationContext searchConfigurationContext,
            Page<? extends IEntity> solrPage) {
        SearchResultSolr searchResultSolr = new SearchResultSolr();
        searchResultSolr.setSearchConfigurationContext(searchConfigurationContext);
        searchResultSolr
                .setResults(new PageImpl<>(solrPage.getContent(), solrPage.getPageable(), solrPage.getTotalElements()));
        return searchResultSolr;
    }

    public static SearchResultSolr fromFacetPage(ISearchConfigurationContext searchConfigurationContext,
            FacetPage<? extends IEntity> solrFacetPage) {
        SearchResultSolr searchResultSolr = new SearchResultSolr();
        searchResultSolr.setSearchConfigurationContext(searchConfigurationContext);
        searchResultSolr.setResults(new PageImpl<>(solrFacetPage.getContent(), solrFacetPage.getPageable(),
                solrFacetPage.getTotalElements()));

        SearchFacetService facetService = searchConfigurationContext.getSearchFacetService();
        SearchFieldService fieldService = searchConfigurationContext.getSearchFieldService();

        List<FacetSolr> facetedResult = new LinkedList<FacetSolr>();

        // For each facet field in search configuration
        for (Facet facet : facetService.getFacetFields()) {

            Field field = SearchFacetService.getFieldFromRefOrIndex(fieldService, facet);
            String solrFieldName = SearchSolrHelper.indexName(field.getIndexField());

            FacetSolr facetSolr = new FacetSolr.FacetSolrBuilder(searchConfigurationContext)
                    .fromFacetFieldEntryPage(solrFacetPage.getFacetResultPage(solrFieldName)).build();

            facetSolr.setFieldName(facet.getFilterRef());

            facetedResult.add(facetSolr);
        }

        // For each facet range field in search configuration
        for (Facet facet : facetService.getFacetRanges()) {

            Field field = SearchFacetService.getFieldFromRefOrIndex(fieldService, facet);
            String solrFieldName = SearchSolrHelper.rangeDateIndexName(field.getIndexField());

            FacetSolr facetSolr = new FacetSolr.FacetSolrBuilder(searchConfigurationContext)
                    .fromFacetFieldEntryPage(solrFacetPage.getRangeFacetResultPage(solrFieldName)).build();

            facetSolr.setFieldName(facet.getFilterRef());

            facetedResult.add(facetSolr);
        }

        searchResultSolr.facets = facetedResult;
        return searchResultSolr;
    }

    @Override
    public Page<? extends IEntity> getResults() {
        return this.results;
    }

    @Override
    public Collection<? extends IFacet> getFacets() {
        if (!hasFacets()) {
            this.facets = new LinkedList<FacetSolr>();
        }
        return this.facets;
    }

    @Override
    public boolean hasFacets() {
        return (facets != null);
    }

    @Override
    public ISearchConfigurationContext getSearchConfigurationContext() {
        if (searchConfigurationContext == null) {
            this.searchConfigurationContext = new SearchConfigurationContext();
        }
        return this.searchConfigurationContext;
    }

    private void setSearchConfigurationContext(ISearchConfigurationContext searchConfigurationContext) {
        this.searchConfigurationContext = searchConfigurationContext;
    }

    private void setResults(Page<? extends IEntity> results) {
        this.results = results;
    }

}
