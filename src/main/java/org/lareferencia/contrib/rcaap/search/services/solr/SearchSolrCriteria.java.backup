
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lareferencia.contrib.rcaap.search.model.Condition;
import org.lareferencia.contrib.rcaap.search.model.ConditionalOperatorEnumType;
import org.lareferencia.contrib.rcaap.search.model.Field;
import org.lareferencia.contrib.rcaap.search.model.Filter;
import org.lareferencia.contrib.rcaap.search.model.LogicalOperatorEnumType;
import org.lareferencia.contrib.rcaap.search.model.QuantifierEnumType;
import org.lareferencia.contrib.rcaap.search.model.Value;
import org.lareferencia.contrib.rcaap.search.services.ISearchConfigurationContext;
import org.lareferencia.contrib.rcaap.search.services.model.FilterNotFoundException;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFilterService;
import org.lareferencia.core.entity.indexing.solr.EntitySolr;
import org.springframework.data.solr.core.query.Criteria;

public class SearchSolrCriteria {
    Criteria criteria;
    private ISearchConfigurationContext context;

    public SearchSolrCriteria(ISearchConfigurationContext context) {
        this.context = context;
    }

    public SearchSolrCriteria fromFilter(Filter filter) {
        if (filter != null) {
            try {
                criteria = criteriaRecursivelyForFilter(criteria, filter);
            } catch (FilterNotFoundException e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }
        return this;
    }

    private Criteria criteriaRecursivelyForFilter(Criteria criteria, Filter filter) throws FilterNotFoundException {
        if (SearchFilterService.hasFilterAnyFieldValue(filter)) {
            Field field = context.getSearchFilterService().getFieldFromRefOrIndex(context.getSearchFieldService(),
                    filter);
            return criteriaForValue(field, filter.getValue());
        }

        if (SearchFilterService.hasConditions(filter)) {
            int i = 0;
            for (Condition condition : filter.getConditions().getCondition()) {
                Optional<Filter> subFilter = context.getSearchFilterService().getFilterFromConditionOrByRef(condition);
                if (subFilter.isPresent()) {
                    // Condition has a filter? and doesn't have a value
                    if (SearchFilterService.hasConditionFilter(condition)
                            && !SearchFilterService.hasFilterAnyFieldValue(subFilter.get())) {
                        // don't have value - loop on sub conditions
                        if (criteria == null) {
                            criteria = criteriaRecursivelyForFilter(criteria, subFilter.get());
                        } else {
                            criteria = criteria.connect().and(criteriaRecursivelyForFilter(criteria, subFilter.get()));
                        }
                    } else if (SearchFilterService.hasConditionFilter(condition)) {
                        if (i++ == 0) {
                            // First element
                            if (criteria == null) {
                                criteria = criteriaRecursivelyForFilter(criteria, subFilter.get());
                            } else {
                                criteria = criteria.connect()
                                        .and(criteriaRecursivelyForFilter(criteria, subFilter.get()));
                            }
                        } else {
                            LogicalOperatorEnumType operator = condition.getOperator();
                            if (SearchFilterService.hasConditionFilter(condition)) {
                                switch (operator) {
                                case AND:
                                    criteria = criteria.connect()
                                            .and(criteriaRecursivelyForFilter(criteria, subFilter.get()));
                                    break;
                                case OR:
                                    criteria = criteria.connect()
                                            .or(criteriaRecursivelyForFilter(criteria, subFilter.get()));
                                    break;
                                case NOT:
                                    Criteria subCriteria = criteriaRecursivelyForFilter(criteria, subFilter.get());
                                    criteria = criteria.connect().not().expression(subCriteria.toString());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return criteria;
    }

    private static Criteria criteriaForEQ(Field field, String fieldValue) {
        String indexName = field.getIndexField();
        switch (field.getType()) {
        case DATE:
            // DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd
            // HH:mm:ss");
            // DateTime dateTime = formatter.parseDateTime(fieldValue);
            // return new Criteria(EntitySolr.DYNAMIC_DATE_PREFIX + indexName).is(dateTime);
        case KEYWORD:
        case NUMBER:
        case STRING:
        case TEXT:
        default:
            return new Criteria(EntitySolr.DYNAMIC_FIELD_PREFIX + indexName).is(fieldValue);
        }
    }

    private static Criteria criteriaForRangeValue(Field field, Value value) {
        String indexName = field.getIndexField();
        String lowerLimit = value.getFieldValue().get(0); // get first
        String upperLimit = value.getFieldValue().get(value.getFieldValue().size() - 1); // get last

        switch (field.getType()) {
        case DATE:
            return new Criteria(EntitySolr.DYNAMIC_DATE_PREFIX + indexName).between(lowerLimit, upperLimit);
        case KEYWORD:
        case NUMBER:
        case STRING:
        case TEXT:
        default:
            return new Criteria(EntitySolr.DYNAMIC_FIELD_PREFIX + indexName).between(lowerLimit, upperLimit);
        }
    }

    private static Criteria criteriaForSingleValue(Field field, ConditionalOperatorEnumType condition,
            String fieldValue) {
        String indexName = field.getIndexField();
        switch (condition) {
        case EQ:
            return criteriaForEQ(field, fieldValue);
        case GT:
            return new Criteria(EntitySolr.DYNAMIC_FIELD_PREFIX + indexName).greaterThanEqual(fieldValue);
        case LT:
            return new Criteria(EntitySolr.DYNAMIC_FIELD_PREFIX + indexName).lessThanEqual(fieldValue);
        case NE:
            return new Criteria(EntitySolr.DYNAMIC_FIELD_PREFIX + indexName).not().is(fieldValue);
        case RANGE:
            break;
        case CONTAINS:
            return new Criteria(EntitySolr.DYNAMIC_TXT_FIELD_PREFIX + indexName).in(tokenizeFieldString(fieldValue));

        default:
            break;
        }
        return new Criteria(EntitySolr.DYNAMIC_FIELD_PREFIX + indexName).is(fieldValue);
    }

    private static Criteria criteriaForValue(Field field, Value value) {
        ConditionalOperatorEnumType valueCondition = value.getCondition();
        QuantifierEnumType occurs = value.getOccurs();

        Criteria conditionValues = new Criteria("*:*");

        switch (value.getCondition()) {
        case RANGE:
            conditionValues = criteriaForRangeValue(field, value);
            break;
        case CONTAINS:
        case EQ:
        case GT:
        case LT:
        case NE:
        default:
            int i = 0;
            for (String fieldValue : value.getFieldValue()) {
                if (fieldValue != null) {
                    if (i++ == 0) {
                        // first item ignore logical operator
                        conditionValues = criteriaForSingleValue(field, valueCondition, fieldValue);
                    } else {
                        switch (occurs) {
                        case ALL:
                            conditionValues = conditionValues.connect()
                                    .and(criteriaForSingleValue(field, valueCondition, fieldValue));
                            break;
                        case ONE_OR_MORE:
                            conditionValues = conditionValues.connect()
                                    .or(criteriaForSingleValue(field, valueCondition, fieldValue));
                            break;
                        case NONE:
                            Criteria subCriteria = criteriaForSingleValue(field, valueCondition, fieldValue);
                            conditionValues = conditionValues.connect().not().expression(subCriteria.toString());
                            break;
                        }
                    }
                }
            }
            break;
        }

        return conditionValues;
    }

    private static List<String> tokenizeFieldString(String field) {
        List<String> tokens = new ArrayList<String>();

        // will tokenize value by quotes and plain keywords
        String regex = "\"([^\"]*)\"|(\\S+)";

        Matcher m = Pattern.compile(regex).matcher(field);
        while (m.find()) {
            if (m.group(1) != null) {
                tokens.add(m.group(1));
            } else {
                tokens.add(m.group(2));
            }
        }

        return tokens;
    }

    public Criteria build() {
        return criteria;
    }
}
