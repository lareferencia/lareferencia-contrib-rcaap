
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

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.lareferencia.contrib.rcaap.search.model.Filter;
import org.lareferencia.contrib.rcaap.search.services.model.FilterLoopException;
import org.lareferencia.contrib.rcaap.search.services.model.FilterNotFoundException;

public class QueryBuilderAdapter extends QueryBuilderAbstract {
    public QueryBuilderAdapter(QueryBuilderContext builderContext) {
        super(builderContext);
    }

    @Override
    public Filter buildFromQuery(Query query) throws FilterNotFoundException, FilterLoopException {
        if (query instanceof BooleanQuery) {
            BooleanQueryBuilder queryParser = new BooleanQueryBuilder(builderContext);
            return queryParser.buildFromQuery(query);
        } else if (query instanceof TermRangeQuery) {
            TermRangeQueryBuilder queryParser = new TermRangeQueryBuilder(builderContext);
            return queryParser.buildFromQuery(query);
        } else if (query instanceof TermQuery) {
            TermQueryBuilder queryParser = new TermQueryBuilder(builderContext);
            return queryParser.buildFromQuery(query);
        } else {
            // By default use termQuery
            TermQueryBuilder queryParser = new TermQueryBuilder(builderContext);
            return queryParser.buildFromQuery(query);
        }
    }

}
