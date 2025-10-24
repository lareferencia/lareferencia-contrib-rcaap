
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

import org.lareferencia.contrib.rcaap.search.model.Facet;
import org.lareferencia.contrib.rcaap.search.model.Field;
import org.lareferencia.contrib.rcaap.search.services.IFacetValue;
import org.lareferencia.contrib.rcaap.search.services.IField;
import org.lareferencia.contrib.rcaap.search.services.ISearchConfigurationContext;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFacetService;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFieldService;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;

/**
 * FacetValueSolr class a specific implementation for facets values from Apache
 * Solr
 * 
 * @author pgraca
 *
 */
public class FacetValueSolr implements IFacetValue {
    protected IField iField;
    protected String value;
    protected Long valueCount;

    // TODO: try to use it as an injection
    private ISearchConfigurationContext searchConfigurationContext;

    @Override
    public String getFieldName() {
        return this.iField.getFieldName();
    }

    @Override
    public void setFieldName(String name) {
        this.iField.setFieldName(name);
    }

    @Override
    public IField getField() {
        return this.iField;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public Long getValueCount() {
        return this.valueCount;
    }

    /**
     * FacetValueSolr Builder class from a Solr FacetFieldEntry
     * 
     * @author pgraca
     *
     */
    public static class FacetValueSolrBuilder {
        private FacetValueSolr iFacet;

        public FacetValueSolrBuilder(ISearchConfigurationContext searchConfigurationContext) {
            this.iFacet = new FacetValueSolr();
            this.iFacet.searchConfigurationContext = searchConfigurationContext;
            this.iFacet.iField = new IField() {
                private String fieldName;

                @Override
                public String getFieldName() {
                    return fieldName;
                }

                @Override
                public void setFieldName(String name) {
                    this.fieldName = name;
                }

            };
        }

        /**
         * build from Solr FacetFieldEntry 
         * @param solrFacet
         * @return FacetValueSolrBuilder
         */
        public FacetValueSolrBuilder fromFacetFieldEntry(FacetFieldEntry solrFacet) {
            SearchFacetService facetService = this.iFacet.searchConfigurationContext.getSearchFacetService();
            SearchFieldService fieldService = this.iFacet.searchConfigurationContext.getSearchFieldService();

            //look for a facet based on 
            for (Facet facet : facetService.getServiceFacets()) {
                Field field = SearchFacetService.getFieldFromRefOrIndex(fieldService, facet);

                // remove the solr prefix from fieldname
                String solrFieldIndexName = SearchSolrHelper.indexNameWithoutPrefix(solrFacet.getField().getName());

                // verify if both field and solr field are the same
                if (solrFieldIndexName.equals(field.getIndexField())) {
                    this.iFacet.setFieldName(facet.getFilterRef());
                    this.iFacet.value = solrFacet.getValue();
                    this.iFacet.valueCount = solrFacet.getValueCount();
                    break;
                }
            }

            return this;
        }

        /**
         * 
         * @return FacetValueSolr mapped from a single FacetFieldEntry
         */
        public FacetValueSolr build() {
            return this.iFacet;
        }
    }

}
