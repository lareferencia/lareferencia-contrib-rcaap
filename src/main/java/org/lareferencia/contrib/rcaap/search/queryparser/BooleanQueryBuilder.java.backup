
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

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.lareferencia.contrib.rcaap.search.model.Condition;
import org.lareferencia.contrib.rcaap.search.model.Conditions;
import org.lareferencia.contrib.rcaap.search.model.Filter;
import org.lareferencia.contrib.rcaap.search.model.LogicalOperatorEnumType;
import org.lareferencia.contrib.rcaap.search.services.model.FilterLoopException;
import org.lareferencia.contrib.rcaap.search.services.model.FilterNotFoundException;

/**
 * /** Class to handle boolean terms, queries that have something like:
 * field1:sapo+AND+field2:galinha
 * 
 * @author pgraca
 *
 */
public class BooleanQueryBuilder extends QueryBuilderAbstract {

    public BooleanQueryBuilder(QueryBuilderContext builderContext) {
        super(builderContext);
    }

    @Override
    public Filter buildFromQuery(Query query) throws FilterNotFoundException, FilterLoopException {
        BooleanQuery bq = (BooleanQuery) query;
        QueryBuilderAdapter adapter = new QueryBuilderAdapter(this.getBuilderContext());

        return processNewFilter(adapter, bq);

    }

    private Filter processNewFilter(QueryBuilder qb, BooleanQuery bq)
            throws FilterNotFoundException, FilterLoopException {
        // Create new complex filter
        Filter customFilter = this.getBuilderContext().getObjFactoryService().getObjectFactory().createFilter();
        Conditions conditions = this.getBuilderContext().getObjFactoryService().getObjectFactory().createConditions();

        for (BooleanClause bc : bq.clauses()) {
            Filter filter = qb.buildFromQuery(bc.getQuery());

            // If a filter exists
            if (filter != null) {
                conditions.getCondition().add(createCondition(qb, filter, bc));
            }

        }
        customFilter.setConditions(conditions);
        return customFilter;
    }

    private Condition createCondition(QueryBuilder qb, Filter filter, BooleanClause bc)
            throws FilterNotFoundException, FilterLoopException {
        Condition condition = this.getBuilderContext().getObjFactoryService().getObjectFactory().createCondition();
        condition.setOperator(operatorFromClause(bc));
        condition.setFilter(filter);
        return condition;
    }

    private LogicalOperatorEnumType operatorFromClause(BooleanClause bc) {
        // TODO: occurs have other types that can be explored
        if (bc.isProhibited()) {
            return LogicalOperatorEnumType.NOT;
        }
        if (bc.isRequired()) {
            return LogicalOperatorEnumType.AND;
        }
        return LogicalOperatorEnumType.OR;
    }

}
