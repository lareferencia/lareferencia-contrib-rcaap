
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

package org.lareferencia.contrib.rcaap.search.services;

import org.lareferencia.contrib.rcaap.search.model.Configuration;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFacetService;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFieldService;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFilterService;
import org.lareferencia.contrib.rcaap.search.services.model.SearchSizeService;
import org.lareferencia.contrib.rcaap.search.services.model.SearchSortService;

public class SearchConfigurationContext implements ISearchConfigurationContext {
    private Configuration config;
    private SearchFilterService filterService;
    private SearchFieldService fieldService;
    private SearchFacetService facetService;
    private SearchSortService sortService;
    private SearchSizeService sizeService;

    @Override
    public void setContextConfiguration(Configuration config) {
        this.config = config;
        // reset services
        this.filterService = null;
        this.fieldService = null;
        this.facetService = null;
    }

    @Override
    public Configuration getContextConfiguration() {
        return this.config;
    }

    @Override
    public SearchFilterService getSearchFilterService() {
        if (this.filterService == null) {
            this.filterService = new SearchFilterService(config.getFilters());
        }
        return this.filterService;
    }

    @Override
    public SearchFieldService getSearchFieldService() {
        if (this.fieldService == null) {
            this.fieldService = new SearchFieldService(config.getFields());
        }
        return this.fieldService;
    }

    @Override
    public SearchFacetService getSearchFacetService() {
        if (this.facetService == null) {
            this.facetService = new SearchFacetService(config.getFacets());
        }
        return this.facetService;
    }

    @Override
    public SearchSortService getSearchSortService() {
        if (this.sortService == null) {
            this.sortService = new SearchSortService(config.getSorts());
        }
        return this.sortService;
    }

    @Override
    public SearchSizeService getSearchSizeService() {
        if (this.sizeService == null) {
            this.sizeService = new SearchSizeService(config.getSizes());
        }
        return this.sizeService;
    }

}
