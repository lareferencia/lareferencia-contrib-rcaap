
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

package org.lareferencia.contrib.rcaap.search.queryparser;

import java.util.Optional;

import org.lareferencia.contrib.rcaap.search.model.Filter;
import org.lareferencia.contrib.rcaap.search.services.model.SearchConfigurationObjectFactoryService;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFilterService;

public class QueryBuilderContext {
    private SearchFilterService filterService;
    private static SearchConfigurationObjectFactoryService objFactoryService;
    private Filter baseFilter;

    public QueryBuilderContext(SearchFilterService filterService) {
        this.filterService = filterService;
        objFactoryService = new SearchConfigurationObjectFactoryService();
    }

    public Optional<Filter> getBaseFilter() {
        return Optional.ofNullable(this.baseFilter);
    }

    public void setBaseFilter(Filter parentFilter) {
        this.baseFilter = parentFilter;
    }

    public SearchFilterService getFilterService() {
        return this.filterService;
    }

    public SearchConfigurationObjectFactoryService getObjFactoryService() {
        return objFactoryService;
    }

}
