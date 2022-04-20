
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.Query;
import org.lareferencia.contrib.rcaap.search.model.Filter;
import org.lareferencia.contrib.rcaap.search.services.model.FilterLoopException;
import org.lareferencia.contrib.rcaap.search.services.model.FilterNotFoundException;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFilterService;

public class SearchQueryParser {

    /**
     * This method adds filter building from a parsed query using Lucene QueryParser
     * 
     * @param filterService
     * @param query
     * @return Filter with value
     * @throws QueryNodeException
     * @throws FilterNotFoundException
     * @throws FilterLoopException
     */
    public static List<Filter> filterFromQuery(SearchFilterService filterService, String query)
            throws FilterNotFoundException, FilterLoopException {
        return buildFilterFromQuery(filterService, null, query);
    }

    /**
     * This method adds filter building from a parsed query using Lucene QueryParser
     * using a base Filter
     * 
     * @param filterService
     * @param baseFilterName
     * @param queries
     * @return
     * @throws QueryNodeException
     * @throws FilterNotFoundException
     * @throws FilterLoopException
     */
    public static List<Filter> filterFromQuery(SearchFilterService filterService, String baseFilterName,
            String... queries) throws FilterNotFoundException, FilterLoopException {
        return buildFilterFromQuery(filterService,
                SearchFilterService.cloneFilter(filterService.getFilterByName(baseFilterName).get()), queries);
    }

    /**
     * This method intends to replace initial addFilterValuesfromParam
     * implementations created to parse complex data using Lucene QueryParser
     * 
     * @param filterService
     * @param baseFilter
     * @param query
     * @return Filter with value from complex filters
     * @throws QueryNodeException
     * @throws FilterNotFoundException
     * @throws FilterLoopException
     */
    public static List<Filter> buildFilterFromQuery(SearchFilterService filterService, Filter baseFilter,
            String... queries) throws FilterNotFoundException, FilterLoopException {
        List<Filter> filterList = new ArrayList<Filter>();

        if (queries == null) {
            return filterList;
        }

        for (String query : queries) {
            if (query != null) {
                QueryParser queryParser = new QueryParser();
                QueryBuilderContext context = new QueryBuilderContext(filterService);
                context.setBaseFilter(baseFilter);
                QueryBuilderAdapter queryBuilder = new QueryBuilderAdapter(context);

                try {
                    Query parsedQuery = queryParser.parse(query, getDefaultFieldName(context));
                    filterList.add(queryBuilder.buildFromQuery(parsedQuery));
                } catch (QueryNodeException e) {
                    // TODO handle exception
                    e.printStackTrace();
                } catch (Exception e1) {
                    // TODO handle exception
                    e1.printStackTrace();
                }
            }
        }
        return filterList;
    }

    /**
     * 
     * @return Default conditions filter or main default or global default
     */
    protected static String getDefaultFieldName(QueryBuilderContext builderContext) {
        String defaultField = QueryBuilder.DEFAULT_FIELD;

        Optional<Filter> baseFilter = builderContext.getBaseFilter();

        if (baseFilter.isPresent()) {
            // Verify if parentFilter has a default filter
            Optional<Filter> defaultBaseFilter = builderContext.getFilterService()
                    .getConditionsDefaultFilter(baseFilter.get());
            if (defaultBaseFilter.isPresent()) {
                defaultField = defaultBaseFilter.get().getName();
            }
        } else {
            // Verify if we have a generic default filter
            Optional<Filter> defaultFilter = builderContext.getFilterService().getDefaultFilter();
            if (defaultFilter.isPresent()) {
                defaultField = defaultFilter.get().getName();
            }

        }

        return defaultField;
    }

}
