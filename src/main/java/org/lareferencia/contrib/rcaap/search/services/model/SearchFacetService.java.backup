
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

package org.lareferencia.contrib.rcaap.search.services.model;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.lareferencia.contrib.rcaap.search.model.Facet;
import org.lareferencia.contrib.rcaap.search.model.FacetFields;
import org.lareferencia.contrib.rcaap.search.model.FacetRanges;
import org.lareferencia.contrib.rcaap.search.model.Facets;
import org.lareferencia.contrib.rcaap.search.model.Field;

public class SearchFacetService {
    private Map<String, Facet> facetsByNameMap;
    private Map<String, Facet> facetsRangesByNameMap;
    private Map<String, Facet> facetsByFilterMap;

    private static SearchConfigurationObjectFactoryService objFactoryService = new SearchConfigurationObjectFactoryService();

    private Map<String, Facet> serviceFacets;

    /**
     * Minimum of counts to consider
     */
    BigInteger minCount = BigInteger.ONE;

    private static final String FACET_CUSTOM_PREFIX = "custom_facet_";

    public SearchFacetService(Facets facets) {
        if (facets == null) {
            facets = objFactoryService.getObjectFactory().createFacets();
        }
        this.setFacetsHash(facets);
        this.minCount = facets.getMinCount();
    }

    private void setFacetsHash(Facets facets) {
        this.serviceFacets = new HashMap<String, Facet>();
        this.facetsByNameMap = new HashMap<String, Facet>();
        this.facetsRangesByNameMap = new HashMap<String, Facet>();
        this.facetsByFilterMap = new HashMap<String, Facet>();

        FacetFields facetFields = facets.getFacetFields();
        FacetRanges facetRangeFields = facets.getFacetRanges();

        if (facetFields != null) {

            for (Facet facet : facetFields.getFacet()) {
                String name = getFacetName(facet);
                this.facetsByNameMap.put(name, facet);
                this.facetsByFilterMap.put(facet.getFilterRef(), facet);

                this.serviceFacets.put(name, facet);
            }

            // build facet range
            for (Facet facet : facetRangeFields.getFacet()) {
                String name = getFacetName(facet);
                this.facetsRangesByNameMap.put(name, facet);

                this.serviceFacets.put(name, facet);
            }
        }
    }

    public Iterable<Facet> getServiceFacets() {
        if (this.serviceFacets == null) {
            this.serviceFacets = new HashMap<String, Facet>();
        }
        return this.serviceFacets.values();
    }

    /**
     * This method returns a Facet field by a ref or index definition, one is
     * required
     * 
     * @param fieldService
     * @param facet
     * @return
     */
    public static Field getFieldFromRefOrIndex(SearchFieldService fieldService, Facet facet) {
        String ref = facet.getFieldRef();
        if (ref != null) {
            return fieldService.getFieldByName(ref);
        } else {
            return fieldService.createFieldFromIndexFieldName(facet.getIndexField(), facet.getFieldType());
        }
    }

    /**
     * Return Facets fields list
     * 
     * @return
     */
    public Iterable<Facet> getFacetFields() {
        return this.facetsByNameMap.values();
    }

    /**
     * Return Facets ranges list
     * 
     * @return
     */
    public Iterable<Facet> getFacetRanges() {
        return this.facetsRangesByNameMap.values();
    }

    /**
     * Return a Facet based on a name
     * 
     * @param filter
     * @return
     */
    public Optional<Facet> getFacetByName(String name) {
        return Optional.ofNullable(this.serviceFacets.getOrDefault(name, null));
    }

    /**
     * Is there any configured facets
     * 
     * @return true if it has configured facets
     */
    public Boolean hasFacets() {
        return !this.serviceFacets.isEmpty();
    }

    public BigInteger getMinCount() {
        return this.minCount;
    }

    private static String getFacetName(Facet facet) {
        String name = facet.getName();
        if (name == null) {
            name = FACET_CUSTOM_PREFIX + facet.getFilterRef();
        }
        return name;
    }
}
