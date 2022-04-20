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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.TimeZone;
import org.apache.solr.common.params.FacetParams.FacetRangeInclude;
import org.apache.solr.common.params.FacetParams.FacetRangeOther;
import org.apache.solr.util.DateMathParser;
import org.lareferencia.contrib.rcaap.search.extended.model.FiltersBuilder;
import org.lareferencia.contrib.rcaap.search.model.Facet;
import org.lareferencia.contrib.rcaap.search.model.Field;
import org.lareferencia.contrib.rcaap.search.model.Filter;
import org.lareferencia.contrib.rcaap.search.model.Filters;
import org.lareferencia.contrib.rcaap.search.model.Sort;
import org.lareferencia.contrib.rcaap.search.services.ISearchConfigurationContext;
import org.lareferencia.contrib.rcaap.search.services.ISearchResult;
import org.lareferencia.contrib.rcaap.search.services.ISearchService;
import org.lareferencia.contrib.rcaap.search.services.PageableSearchBuilder;
import org.lareferencia.contrib.rcaap.search.services.SearchConfigurationContext;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFacetService;
import org.lareferencia.core.entity.indexing.service.IEntity;
import org.lareferencia.core.entity.indexing.solr.EntitySolr;
import org.lareferencia.core.entity.search.solr.EntitySearchServiceSolrImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for performing the solr query
 * 
 * @author pgraca
 *
 */
@Service
public class SearchServiceSolrImpl extends EntitySearchServiceSolrImpl implements ISearchService {

    private static final String[] IENTITY_DEFAULT_FIELDS = { EntitySolr.ID_FIELD, EntitySolr.TYPE_FIELD, "semantic_id",
            EntitySolr.DYNAMIC_RELID_PREFIX + "*" };

    @Autowired
    private SolrTemplate solrTemplate;

    // TODO: try to use it as an injection
    private ISearchConfigurationContext searchConfigurationContext = new SearchConfigurationContext();

    @Override
    public ISearchResult searchEntitiesBySearchConfiguration(ISearchConfigurationContext configContext,
            FiltersBuilder filtersBuilder, PageableSearchBuilder pageableBuilder) {

        searchConfigurationContext = configContext;

        String index = this.getSolrIndex();
        String fields[] = buildFieldsSet();

        SimpleQuery query = new SimpleQuery(this.getDefaultQuery());

        // build sort query with a solr prefix
        query.setPageRequest(genPageable(pageableBuilder));

        addCriterias(query, filtersBuilder);

        // Set fields
        query.addProjectionOnFields(fields);
        // Solr query page
        return SearchResultSolr.fromPage(searchConfigurationContext,
                solrTemplate.queryForPage(index, query, EntitySolr.class));

    }

    @Override
    public ISearchResult searchEntitiesWFacetsBySearchConfiguration(ISearchConfigurationContext configContext,
            FiltersBuilder filtersBuilder, PageableSearchBuilder pageableBuilder) {

        searchConfigurationContext = configContext;

        String index = this.getSolrIndex();
        String fields[] = buildFieldsSet();

        SimpleQuery query = new SimpleQuery(this.getDefaultQuery());

        // build sort query with a solr prefix
        query.setPageRequest(genPageable(pageableBuilder));

        addCriterias(query, filtersBuilder);

        FacetQuery facetQuery = new SimpleFacetQuery(query.getCriteria()).addProjectionOnFields(fields)
                .setPageRequest(query.getPageRequest());

        // Get Fields
        facetQuery.setFacetOptions(this.genSolrFacetOptions());
        query.getFilterQueries().stream().forEach(queryFilter -> facetQuery.addFilterQuery(queryFilter));

        // map from a Solr FacetPage into a LaRef solution

        return SearchResultSolr.fromFacetPage(searchConfigurationContext,
                solrTemplate.queryForFacetPage(index, facetQuery, EntitySolr.class));
    }

    @Override
    public Optional<? extends IEntity> getEntityById(String id, String collection) {
        // Solr query page
        return solrTemplate.getById(collection, id, EntitySolr.class);
    }

    private void addCriterias(SimpleQuery query, FiltersBuilder filtersBuilder) {
        Filters filters = filtersBuilder.build();

        // Try to add filter queries based on custom filters
        for (Filter filter : filters.getFilter()) {

            if (filter != null) {
                SearchSolrCriteria solrCriteria = new SearchSolrCriteria(searchConfigurationContext);
                Criteria criteria = solrCriteria.fromFilter(filter).build();
                if (criteria != null) {
                    query.addCriteria(criteria);
                }
            }
        }
    }

    private String[] buildFieldsSet() {
        List<String> fields = new ArrayList<String>();
        fields.addAll(Arrays.asList(IENTITY_DEFAULT_FIELDS));
        fields.addAll(buildFieldsSetFromConfigurations());

        return fields.toArray(new String[0]);
    }

    private List<String> buildFieldsSetFromConfigurations() {
        List<String> indexFields = searchConfigurationContext.getSearchFieldService().getConfigurationIndexFields();

        ListIterator<String> fieldsIterator = indexFields.listIterator();

        // Add solr required prefix
        while (fieldsIterator.hasNext()) {
            String field = fieldsIterator.next();
            fieldsIterator.set("*." + field);
        }

        return indexFields;
    }

    private String getSolrIndex() {
        return (searchConfigurationContext.getContextConfiguration().getIndex() != null)
                ? searchConfigurationContext.getContextConfiguration().getIndex()
                : EntitySolr.COLLECTION;
    }

    private String getDefaultQuery() {
        String baseQuery = searchConfigurationContext.getContextConfiguration().getBaseQuery();
        String defaultQuery = Criteria.WILDCARD + ":" + Criteria.WILDCARD;

        return (baseQuery != null) ? baseQuery : defaultQuery;
    }

    private FacetOptions genSolrFacetOptionsFields(Iterable<Facet> facets, FacetOptions facetOptions) {

        for (Facet facet : facets) {
            String solrIndexName = SearchSolrHelper.indexName(SearchFacetService
                    .getFieldFromRefOrIndex(searchConfigurationContext.getSearchFieldService(), facet).getIndexField());
            facetOptions.addFacetOnField(solrIndexName);
        }
        return facetOptions;
    }

    private FacetOptions genSolrFacetOptionsRanges(Iterable<Facet> facets, FacetOptions facetOptions) {

        for (Facet facet : facets) {
            Field field = SearchFacetService.getFieldFromRefOrIndex(searchConfigurationContext.getSearchFieldService(),
                    facet);
            String solrIndexName = SearchSolrHelper.rangeDateIndexName(field.getIndexField());

            switch (field.getType()) {
                case DATE:
                    String dateStart = (facet.getRangeStart() != null) ? facet.getRangeStart() : "NOW";
                    String dateEnd = (facet.getRangeEnd() != null) ? facet.getRangeEnd() : "NOW";
                    String dateGap = (facet.getRangeGap() != null) ? facet.getRangeGap() : "+1YEAR";

                    // Use solr parse date math to extract a date to define the facet option
                    // This class enables the use of stuff like: +1YEAR, or NOW+1YEAR/YEAR
                    // please check -
                    // https://solr.apache.org/guide/6_6/working-with-dates.html#WorkingwithDates-DateMath
                    facetOptions.addFacetByRange(new FacetOptions.FieldWithDateRangeParameters(solrIndexName,
                            DateMathParser.parseMath(null, dateStart, TimeZone.getDefault()),
                            DateMathParser.parseMath(null, dateEnd, TimeZone.getDefault()), dateGap)
                                    .setInclude(FacetRangeInclude.ALL).setOther(FacetRangeOther.BEFORE));

                    break;
                case NUMBER:
                    Number start = (facet.getRangeStart() != null) ? Double.valueOf(facet.getRangeStart()) : 0;
                    Number end = (facet.getRangeEnd() != null) ? Double.valueOf(facet.getRangeEnd()) : 0;
                    Number gap = (facet.getRangeGap() != null) ? Double.valueOf(facet.getRangeGap()) : 1;

                    facetOptions.addFacetByRange(
                            new FacetOptions.FieldWithNumericRangeParameters(solrIndexName, start, end, gap)
                                    .setInclude(FacetRangeInclude.ALL).setOther(FacetRangeOther.BEFORE));
                    break;
                default:
                    break;

            }

        }

        return facetOptions;
    }

    private FacetOptions genSolrFacetOptions() {
        FacetOptions facetOptions = new FacetOptions();

        SearchFacetService facetService = this.searchConfigurationContext.getSearchFacetService();

        // If no configured facets
        if (!facetService.hasFacets()) {
            return facetOptions;
        }

        facetOptions.setFacetMinCount(facetService.getMinCount().intValue());
        this.genSolrFacetOptionsFields(facetService.getFacetFields(), facetOptions);
        this.genSolrFacetOptionsRanges(facetService.getFacetRanges(), facetOptions);

        return facetOptions;
    }

    private Pageable genPageable(PageableSearchBuilder pageableBuilder) {
        Optional<Sort> currentOptSort = pageableBuilder.getSort();

        if (currentOptSort.isPresent()) {
            Sort currentSort = currentOptSort.get();
            Field sortField = searchConfigurationContext.getSearchSortService()
                    .getFieldFromRefOrIndex(searchConfigurationContext.getSearchFieldService(), currentSort);

            Sort newSort = new Sort();
            newSort.setFieldType(sortField.getType());
            newSort.setDefaultDirection(currentSort.getDefaultDirection());

            switch (sortField.getType()) {
                case DATE:
                    // workaround for sorting on multivalued dates field
                    // sort should be translated to something like:
                    // ?sort=field(df.CreativeWork.datePublished,min)+ASC
                    // it depends on solr version
                    newSort.setIndexField(
                            "field(" + EntitySolr.DYNAMIC_DATE_PREFIX + sortField.getIndexField() + ",min)");
                    break;
                case STRING:
                case KEYWORD:
                case NUMBER:
                case TEXT:
                default:
                    newSort.setIndexField(EntitySolr.DYNAMIC_SORT_FIELD_PREFIX + sortField.getIndexField());
                    break;
            }

            // Change the sort to add the correct prefix
            pageableBuilder.setSort(newSort);
        }
        return pageableBuilder.build();
    }

}
