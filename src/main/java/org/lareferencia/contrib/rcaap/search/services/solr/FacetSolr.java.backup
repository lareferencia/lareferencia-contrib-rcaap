
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

import java.util.LinkedList;
import java.util.List;

import org.lareferencia.contrib.rcaap.search.services.IFacet;
import org.lareferencia.contrib.rcaap.search.services.IFacetValue;
import org.lareferencia.contrib.rcaap.search.services.IField;
import org.lareferencia.contrib.rcaap.search.services.ISearchConfigurationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.lareferencia.contrib.rcaap.search.services.solr.FacetValueSolr;

/**
 * This class is used to convert facets from solr into an internal lareferencia
 * format
 * 
 * @author pgraca
 *
 */
public class FacetSolr implements IFacet {
    protected IField field;
    protected Page<? extends IFacetValue> facetValues;

    // TODO: try to use it as an injection
    private ISearchConfigurationContext searchConfigurationContext;

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
    public Page<? extends IFacetValue> getFacetValues() {
        return this.facetValues;
    }

    /**
     * FacetSolr Builder class from a Solr <code>Page&lt;FacetFieldEntry&gt;</code>
     * 
     * @author pgraca
     *
     */
    public static class FacetSolrBuilder {
        private FacetSolr facetSolr;

        public FacetSolrBuilder(ISearchConfigurationContext searchConfigurationContext) {
            this.facetSolr = new FacetSolr();
            this.facetSolr.searchConfigurationContext = searchConfigurationContext;
            this.facetSolr.field = new IField() {
                private String fieldName;

                @Override
                public void setFieldName(String name) {
                    this.fieldName = name;
                }

                @Override
                public String getFieldName() {
                    return this.fieldName;
                }
            };

        }

        public FacetSolrBuilder fromFacetFieldEntryPage(Page<FacetFieldEntry> solrFacetFieldEntryPage) {
            List<FacetValueSolr> facetValues = new LinkedList<FacetValueSolr>();

            for (FacetFieldEntry solrFacetFieldEntry : solrFacetFieldEntryPage.getContent()) {
                facetValues.add(new FacetValueSolr.FacetValueSolrBuilder(this.facetSolr.searchConfigurationContext)
                        .fromFacetFieldEntry(solrFacetFieldEntry).build());
            }

            this.facetSolr.facetValues = new PageImpl<>(facetValues, solrFacetFieldEntryPage.getPageable(),
                    solrFacetFieldEntryPage.getTotalElements());

            this.facetSolr.field.setFieldName("");
            return this;
        }

        /**
         * 
         * @return FacetSolr mapped from a single FacetFieldEntry
         */
        public FacetSolr build() {
            return this.facetSolr;
        }
    }
}
