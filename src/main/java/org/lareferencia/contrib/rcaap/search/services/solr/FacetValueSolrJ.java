
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

import org.apache.solr.client.solrj.response.FacetField;
import org.lareferencia.contrib.rcaap.search.services.IFacet;
import org.lareferencia.contrib.rcaap.search.services.IFacetValue;
import org.lareferencia.contrib.rcaap.search.services.IField;

/**
 * FacetValueSolr class a specific implementation for facets values from Apache
 * Solr
 * 
 * @author pgraca
 *
 */
public class FacetValueSolrJ implements IFacetValue {
    protected IField field;
    protected String value;
    protected Long valueCount;
    protected IFacet facet;

    @Override
    public String getFieldName() {
        return this.field.getFieldName();
    }

    @Override
    public void setFieldName(String name) {
        this.field.setFieldName(name);
    }

    @Override
    public IField getField() {
        return this.field;
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
        private FacetValueSolrJ facetValue;

        public FacetValueSolrBuilder(IFacet facet) { 
            this.facetValue = new FacetValueSolrJ();
            this.facetValue.facet = facet;
            this.facetValue.field = new IField() {
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
        public FacetValueSolrBuilder fromFacetFieldCount(FacetField.Count solrFacet) {

            this.facetValue.setFieldName(facetValue.facet.getFieldName());
            this.facetValue.value = solrFacet.getName();
            this.facetValue.valueCount = solrFacet.getCount();

            return this;
        }

        /**
         * build from Solr FacetFieldEntry 
         * @param solrFacet
         * @return FacetValueSolrBuilder
         */
        public FacetValueSolrBuilder fromString(String field, String facet, Long count) {

            this.facetValue.setFieldName(field);
            this.facetValue.value = facet;
            this.facetValue.valueCount = count;

            return this;
        }

        /**
         * 
         * @return FacetValueSolr mapped from a single FacetFieldEntry
         */
        public FacetValueSolrJ build() {
            return this.facetValue;
        }
    }

}
