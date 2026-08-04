
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.response.FacetField;
import org.lareferencia.contrib.rcaap.search.services.IFacet;
import org.lareferencia.contrib.rcaap.search.services.IFacetValue;
import org.lareferencia.contrib.rcaap.search.services.IField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * This class is used to convert facets from solr into an internal lareferencia
 * format
 * 
 * @author pgraca
 *
 */
public class FacetSolrJ implements IFacet {
    protected IField field;
    protected Page<? extends IFacetValue> facetValues;

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
        private FacetSolrJ facetSolr;

        private int page = 0;
        private int size = Integer.MAX_VALUE;

        public FacetSolrBuilder() {
            this.facetSolr = new FacetSolrJ();
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

        public FacetSolrBuilder page(int page) {
            this.page = page;
            return this;
        }

        public FacetSolrBuilder size(int size) {
            this.size = size;
            return this;
        }

        public FacetSolrBuilder fromFacetField(FacetField solrFacetField) {
            List<FacetValueSolrJ> facetValues = new LinkedList<FacetValueSolrJ>();
            List<FacetField.Count> counts = solrFacetField.getValues();

            if (counts == null) {
                counts = Collections.emptyList();
            }

            facetValues = solrFacetField.getValues().stream()
               .map(c-> new FacetValueSolrJ.FacetValueSolrBuilder(this.facetSolr)
                       .fromFacetFieldCount(c)
                       .build() )
               .collect(Collectors.toList());

            Pageable pageable = PageRequest.of(page, size);

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), facetValues.size());

            List<FacetValueSolrJ> pagedContent;

            if (start > end) {
                pagedContent = Collections.emptyList();
            } else {
                pagedContent = facetValues.subList(start, end);
            }

            this.facetSolr.facetValues =
                new PageImpl<>(
                    pagedContent,
                    pageable,
                    facetValues.size()
                );

            this.facetSolr.field.setFieldName(solrFacetField.getName());
            return this;
        }

        public FacetSolrBuilder fromFacetValuesSolrJ(String fieldName, List<IFacetValue> facetValues) {

            if (facetValues == null) {
                facetValues = Collections.emptyList();
            }

            Pageable pageable = PageRequest.of(page, size);

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), facetValues.size());

            if (start > end) {
                facetValues = Collections.emptyList();
            } else {
                facetValues = facetValues.subList(start, end);
            }

            this.facetSolr.facetValues =
                    new PageImpl<>(
                        facetValues,
                        pageable,
                        facetValues.size()
                    );

            this.facetSolr.field.setFieldName(fieldName);

            return this;
        }
        
        /**
         * 
         * @return FacetSolr mapped from a single FacetFieldEntry
         */
        public FacetSolrJ build() {
            return this.facetSolr;
        }
    }
}
