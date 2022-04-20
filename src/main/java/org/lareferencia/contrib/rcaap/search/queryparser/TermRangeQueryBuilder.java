
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

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.lareferencia.contrib.rcaap.search.extended.model.IValueImpl;
import org.lareferencia.contrib.rcaap.search.extended.model.IValue;
import org.lareferencia.contrib.rcaap.search.extended.model.ValueBuilder;
import org.lareferencia.contrib.rcaap.search.model.Filter;
import org.lareferencia.contrib.rcaap.search.services.model.FilterLoopException;
import org.lareferencia.contrib.rcaap.search.services.model.FilterNotFoundException;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFilterService;

/**
 * Class to handle term range queries, queries that have something like:
 * field:[2000+TO+2021]
 * 
 * @author pgraca
 *
 */
public class TermRangeQueryBuilder extends QueryBuilderAbstract {

    public TermRangeQueryBuilder(QueryBuilderContext builderContext) {
        super(builderContext);
    }

    @Override
    public Filter buildFromQuery(Query query) throws FilterLoopException {
        TermRangeQuery trq = (TermRangeQuery) query;
        String filterName = trq.getField();

        try {
            // get Filter by name
            Optional<Filter> optFilter = this.getFilterService().getFilterByName(filterName);

            // if filter exists
            if (optFilter.isPresent()) {

                if (trq.includesLower()) {
                    // System.out.println(">" + trq.getLowerTerm().utf8ToString());
                }
                if (trq.includesUpper()) {
                    // System.out.println("<" + trq.getUpperTerm().utf8ToString());
                }

                IValue value = new IValueImpl.IValueBuilder()
                        .fromStringValues(trq.getLowerTerm().utf8ToString(), trq.getUpperTerm().utf8ToString()).build();

                ValueBuilder valueBuilder = new ValueBuilder().addValue(value);

                // this will add the value to the default filter
                Filter filter = SearchFilterService.cloneFilter(optFilter.get());
                this.getFilterService().setFilterValue(filter, valueBuilder);
                return filter;
            }
        } catch (FilterNotFoundException e) {
            // TODO skip this filter - it doesn't exists
            e.printStackTrace();
        }

        return null;
    }

}
