
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

package org.lareferencia.contrib.rcaap.search.extended.model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.lareferencia.contrib.rcaap.search.model.Filter;
import org.lareferencia.contrib.rcaap.search.model.Filters;
import org.lareferencia.contrib.rcaap.search.services.model.FilterLoopException;
import org.lareferencia.contrib.rcaap.search.services.model.FilterNotFoundException;
import org.lareferencia.contrib.rcaap.search.services.model.SearchConfigurationObjectFactoryService;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFilterService;
import org.lareferencia.core.util.date.DateHelper;

public class FiltersBuilder {
    private static SearchConfigurationObjectFactoryService objFactoryService = new SearchConfigurationObjectFactoryService();

    Filters filters;
    SearchFilterService filterService;

    public FiltersBuilder(SearchFilterService filterService) {
        this.filters = objFactoryService.getObjectFactory().createFilters();
        this.filterService = filterService;
    }

    /**
     * Adds a Filter and it's values by filter name
     * 
     * @param filterName
     * @param values
     * @return
     * @throws FilterNotFoundException
     * @throws FilterLoopException
     */
    public FiltersBuilder addFilter(String filterName, String... values)
            throws FilterNotFoundException, FilterLoopException {
        if (values != null && values.length > 0 && values[0] != null) {
            Filter filter = SearchFilterService.cloneFilter(filterService.getFilterByName(filterName).get());
            filterService.setFilterValue(filter, values);
            filters.getFilter().add(filter);
        }
        return this;
    }

    /**
     * Adds a Date Filter it requires at least 2 dates to be used in a range
     * 
     * @param filterName
     * @param startDate
     * @param endDate
     * @return
     * @throws FilterNotFoundException
     * @throws FilterLoopException
     */
    public FiltersBuilder addDateRangeFilter(String filterName, LocalDateTime startDate, LocalDateTime endDate)
            throws FilterNotFoundException, FilterLoopException {
        if (startDate != null && startDate != null) {
            List<String> formattedDates = new LinkedList<String>();
            formattedDates.add(DateHelper.getInstantDateString(startDate));
            formattedDates.add(DateHelper.getInstantDateString(endDate));

            Filter filter = SearchFilterService.cloneFilter(filterService.getFilterByName(filterName).get());
            filterService.setFilterValue(filter, formattedDates);
            filters.getFilter().add(filter);
        }

        return this;
    }

    /**
     * Adds an already build Filter and it's values
     * 
     * @param filter
     * @return
     */
    public FiltersBuilder addFilter(Filter filter) {
        if (filter != null) {
            try {
                Filter clonedFilter = SearchFilterService.cloneFilter(filter);
                filters.getFilter().add(clonedFilter);
            } catch (FilterNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Adds a list of filters
     * 
     * @param filters
     * @return
     */
    public FiltersBuilder addFilter(Iterable<Filter> filters) {
        if (filters != null) {
            for (Filter filter : filters) {
                this.addFilter(filter);
            }
        }
        return this;
    }

    /**
     * Build a filter list
     * 
     * @return Filters object
     */
    public Filters build() {
        return this.filters;
    }

}
