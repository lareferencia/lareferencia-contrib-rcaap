
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
import org.apache.lucene.search.TermQuery;
import org.lareferencia.contrib.rcaap.search.extended.model.IValueImpl;
import org.lareferencia.contrib.rcaap.search.extended.model.IValue;
import org.lareferencia.contrib.rcaap.search.extended.model.ValueBuilder;
import org.lareferencia.contrib.rcaap.search.model.Filter;
import org.lareferencia.contrib.rcaap.search.services.model.FilterLoopException;
import org.lareferencia.contrib.rcaap.search.services.model.FilterNotFoundException;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFilterService;

/**
 * Class to handle simple terms, queries that have something like: 
 *   field:sapo
 * 
 * @author pgraca
 *
 */
public class TermQueryBuilder extends QueryBuilderAbstract {

    public TermQueryBuilder(QueryBuilderContext builderContext) {
        super(builderContext);
    }

    @Override
    public Filter buildFromQuery(Query query) throws FilterLoopException {
        TermQuery tc = (TermQuery) query;
        String filterName = tc.getTerm().field();

        try {
            // get Filter by name
            Optional<Filter> optFilter = this.getFilterService().getFilterByName(filterName);

            // if filter exists
            if (optFilter.isPresent()) {
                // group(1) - Single value
                IValue value = new IValueImpl.IValueBuilder().fromStringValues(tc.getTerm().text()).build();
                ValueBuilder valueBuilder = new ValueBuilder().addValue(value);

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
