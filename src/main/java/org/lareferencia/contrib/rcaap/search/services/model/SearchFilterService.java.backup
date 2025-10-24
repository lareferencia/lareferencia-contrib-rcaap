
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.lareferencia.contrib.rcaap.search.extended.model.IValue;
import org.lareferencia.contrib.rcaap.search.extended.model.ValueBuilder;
import org.lareferencia.contrib.rcaap.search.extended.model.IValueImpl;
import org.lareferencia.contrib.rcaap.search.model.Condition;
import org.lareferencia.contrib.rcaap.search.model.Conditions;
import org.lareferencia.contrib.rcaap.search.model.Field;
import org.lareferencia.contrib.rcaap.search.model.Filter;
import org.lareferencia.contrib.rcaap.search.model.Filters;
import org.lareferencia.contrib.rcaap.search.model.Value;

public class SearchFilterService {
    protected Map<String, Filter> serviceFilters;
    private static final String FILTER_CUSTOM_PREFIX = "custom_filter_";
    private static final String FILTER_DEFAULT_NAME = "_default_";

    private static SearchConfigurationObjectFactoryService objFactoryService = new SearchConfigurationObjectFactoryService();

    public SearchFilterService(Filters filters) {
        this.serviceFilters = new HashMap<String, Filter>();
        if (filters != null) {
            for (Filter filter : filters.getFilter()) {
                String filterName = getFilterOrDefaultName(filter);
                this.serviceFilters.put(filterName, filter);
            }
        }
        if (filters.getDefault() != null) {
            try {
                // create generic default filter
                Optional<Filter> defaultFilter = this.getFilterByName(filters.getDefault());
                this.serviceFilters.put(FILTER_DEFAULT_NAME, defaultFilter.get());
            } catch (FilterNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Get Filters
     * 
     * @return
     */
    public Map<String, Filter> getServiceFilters() {
        if (serviceFilters == null) {
            return new HashMap<String, Filter>();
        }
        return serviceFilters;
    }

    /**
     * Create a new Filter replica
     * 
     * @param filter
     * @return
     * @throws FilterNotFoundException
     */
    public static Filter cloneFilter(Filter filter) throws FilterNotFoundException {
        if (filter == null) {
            throw new FilterNotFoundException("Trying to clone a null Filter");
        }
        Filter clonedFilter = objFactoryService.getObjectFactory().createFilter();
        Conditions conditions = filter.getConditions();
        Value value = filter.getValue();

        clonedFilter.setFieldRef(filter.getFieldRef());
        clonedFilter.setIndexField(filter.getIndexField());
        clonedFilter.setName(filter.getName());
        clonedFilter.setIndexField(filter.getIndexField());

        if (conditions != null) {
            clonedFilter.setConditions(cloneConditions(conditions));
        }

        if (value != null) {
            clonedFilter.setValue(cloneValue(value));
        }
        return clonedFilter;
    }

    /**
     * Create a new Conditions replica
     * 
     * @param conditions
     * @return
     * @throws FilterNotFoundException
     */
    public static Conditions cloneConditions(Conditions conditions) throws FilterNotFoundException {
        Conditions clonedConditions = objFactoryService.getObjectFactory().createConditions();

        clonedConditions.setDefaultFilterRef(conditions.getDefaultFilterRef());
        for (Condition condition : conditions.getCondition()) {
            clonedConditions.getCondition().add(cloneCondition(condition));
        }

        return clonedConditions;
    }

    /**
     * Create a new Condition replica
     * 
     * @param condition
     * @return
     * @throws FilterNotFoundException
     */
    public static Condition cloneCondition(Condition condition) throws FilterNotFoundException {
        Condition clonedCondition = objFactoryService.getObjectFactory().createCondition();
        Filter filter = condition.getFilter();

        clonedCondition.setName(condition.getName());
        String filterRef = condition.getFilterRef();
        clonedCondition.setFilterRef(filterRef);
        clonedCondition.setOperator(condition.getOperator());

        if (filter != null) {
            clonedCondition.setFilter(cloneFilter(filter));
        }

        return clonedCondition;
    }

    /**
     * Create a new Values replica
     * 
     * @param filterValues
     * @return
     */
    public static Value cloneValue(Value filterValues) {
        Value clonedFilterValues = objFactoryService.getObjectFactory().createValue();

        clonedFilterValues.getFieldValue().addAll(filterValues.getFieldValue());
        clonedFilterValues.setCondition(filterValues.getCondition());
        clonedFilterValues.setOccurs(filterValues.getOccurs());

        return clonedFilterValues;
    }

    /**
     * Get general default filter (if exists)
     * 
     * @return
     */
    public Optional<Filter> getDefaultFilter() {
        try {
            return this.getFilterByName(FILTER_DEFAULT_NAME);
        } catch (FilterNotFoundException e) {
            return Optional.empty();
        }
    }

    /**
     * Get a filter by it's name
     * 
     * @param name
     * @return
     * @throws FilterNotFoundException
     */
    public Optional<Filter> getFilterByName(String name) throws FilterNotFoundException {
        if (getServiceFilters().containsKey(name)) {
            return Optional.of(getServiceFilters().get(name));
        }
        throw new FilterNotFoundException("Filter '" + name + "' not found in search configuration");
    }

    /**
     * Get a filter recursively by it's name
     * 
     * @param filter
     * @param subFilterName
     * @return
     * @throws FilterNotFoundException
     * @throws FilterLoopException
     */
    public Optional<Filter> getFilterRecursivelyByName(Filter filter, String subFilterName)
            throws FilterNotFoundException, FilterLoopException {
        return getFilterRecursivelyByNameNoLoop(filter, subFilterName, new ArrayList<String>());
    }

    /**
     * This method will allow to get filters recursively and also tries to identify
     * loops loops occur when a child filter references a parent filter, creating a
     * closed and infinite loop. <filter name="authorFilter">
     * <conditions default-filter-ref="authorNameFilter">
     * <condition filter-ref="authorGivenNameFilter"/>
     * <condition filter-ref="authorFamilyNameFilter"/>
     * <condition filter-ref="authorOrcidFilter"/>
     * <condition filter-ref="authorCienciaIdFilter"/>
     * <condition filter-ref="authorElectronicAddressFilter"/>
     * <condition filter-ref="isniFilter"/> </conditions> </filter>
     * 
     * @param baseFilter
     * @param filterName
     * @param filterNamesTree
     * @return
     * @throws FilterLoopException
     */
    private Optional<Filter> getFilterRecursivelyByNameNoLoop(Filter baseFilter, String filterName,
            List<String> filterNamesTree) throws FilterNotFoundException, FilterLoopException {
        // No filter specified
        if (baseFilter == null) {
            return Optional.empty();
        }

        String parentFilterName = baseFilter.getName();
        // we wont find any name within a filter with no name
        // If this filter has no name, return it
        if (parentFilterName != null) {
            // TODO: probably replace this with a lambda
            for (String filterNameInTree : filterNamesTree) {
                // verify if the name is already on the list
                if (filterNameInTree != null && parentFilterName.equals(filterNameInTree)) {
                    throw new FilterLoopException(parentFilterName
                            + " filter already in the tree structure, there is probably a loop in this filter");
                }
            }
            // Add filter name to a list to prevent loops
            filterNamesTree.add(parentFilterName);
        }

        // Is itself
        if (filterName.equals(parentFilterName)) {
            return Optional.of(baseFilter);
        }

        // Search filter inside conditions
        if (hasConditions(baseFilter)) {
            Optional<Filter> optSubFilter;

            // if we have conditions
            for (Condition condition : baseFilter.getConditions().getCondition()) {
                Optional<Filter> conditionFilter = getFilterFromConditionOrByRef(condition);
                if (conditionFilter.isPresent()) {
                    optSubFilter = getFilterRecursivelyByNameNoLoop(conditionFilter.get(), filterName, filterNamesTree);
                    if (optSubFilter.isPresent()) {
                        return optSubFilter;
                    }
                }
            }
        }
        // no filter found
        return Optional.empty();
    }

    /**
     * Get a Filter from a Condition
     * 
     * @param condition
     * @return
     * @throws FilterNotFoundException
     */
    public Optional<Filter> getFilterFromConditionOrByRef(Condition condition) throws FilterNotFoundException {
        // Get a filter by a reference
        String filterRef = condition.getFilterRef();
        Optional<Filter> optConditionFilter = Optional.ofNullable(condition.getFilter());
        // Load filter from ref
        if (!optConditionFilter.isPresent() && filterRef != null) {
            optConditionFilter = Optional.ofNullable(createFilterByRef(filterRef));
            condition.setFilter(optConditionFilter.get());
        }
        return optConditionFilter;
    }

    /**
     * Set a baseFilter
     * 
     * @param baseFilter
     * @throws FilterNotFoundException
     */
    public void setDefaultConditionsFilter(Filter baseFilter) throws FilterNotFoundException {
        // no subfilter found, try default from conditions
        Optional<Filter> filterRefFromDefaultConditions = getConditionsDefaultFilter(baseFilter);
        if (filterRefFromDefaultConditions.isPresent()) {
            // replace existing filter with the default
            Filter defaultFilter = cloneFilter(filterRefFromDefaultConditions.get());
            // clean default filter name
            defaultFilter.setName(null);
            Condition condition = objFactoryService.getObjectFactory().createCondition();
            condition.setFilter(defaultFilter);

            Conditions conditions = objFactoryService.getObjectFactory().createConditions();
            conditions.getCondition().add(condition);
            // Replace existing conditions with the default
            baseFilter.setConditions(conditions);
        }
    }

    /**
     * Get Default conditions Filter from an base filter
     * 
     * @param baseFilter
     * @return
     */
    public Optional<Filter> getConditionsDefaultFilter(Filter baseFilter) {
        if (baseFilter == null || !hasConditions(baseFilter)) {
            return Optional.empty();
        }

        String defaultFilterName = baseFilter.getConditions().getDefaultFilterRef();
        if (defaultFilterName != null) {
            try {
                // Set a filter by default reference
                return this.getFilterByName(defaultFilterName);
            } catch (FilterNotFoundException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Return a new Filter from a reference
     * @param filterName
     * @return
     * @throws FilterNotFoundException
     */
    public Filter createFilterByRef(String filterName) throws FilterNotFoundException {
        // Get a filter by a reference
        Optional<Filter> filterByRef = this.getFilterByName(filterName);
        if (!filterByRef.isPresent()) {
            throw new FilterNotFoundException(filterName + " Condition Filter is unavailable");
        }
        return cloneFilter(filterByRef.get());
    }

    /**
     * Does Conditions has any filter
     * 
     * @param condition
     * @return
     */
    public static boolean hasConditionFilter(Condition condition) {
        return (condition.getFilter() != null);
    }

    /**
     * Does filter has Conditions
     * 
     * @param filter
     * @return
     */
    public static boolean hasConditions(Filter filter) {
        return (filter.getConditions() != null);
    }

    /**
     * Verify if this filter has any value
     * 
     * @param filter
     * @return true if the filter has a value
     */
    public static boolean hasFilterValue(Filter filter) {
        return (filter.getValue() != null);
    }

    /**
     * Verify if this filter has any field value
     * 
     * @param filter
     * @return true if it encounters any field value
     */
    public static boolean hasFilterAnyFieldValue(Filter filter) {
        return (hasFilterValue(filter) && !filter.getValue().getFieldValue().isEmpty());
    }

    /**
     * It will return a Filter Value
     * 
     * @param filter
     * @return
     */
    public Value getFilterValue(Filter filter) {
        if (!hasFilterValue(filter)) {
            return objFactoryService.getObjectFactory().createValue();
        }
        return filter.getValue();
    }

    /**
     * It will apply a list of String values to a filter (it will be recursively
     * applied)
     * 
     * @param filter
     * @param values
     * @throws FilterNotFoundException
     * @throws FilterLoopException
     */
    public void setFilterValue(Filter filter, Iterable<String> values)
            throws FilterNotFoundException, FilterLoopException {
        IValue valueObj = new IValueImpl();
        valueObj.setFieldValue(values);

        ValueBuilder valueBuilder = new ValueBuilder().setValue(valueObj);

        setFilterValueByName(filter, valueBuilder, filter.getName());
    }

    /**
     * It will apply String values to a filter (it will be recursively applied)
     * 
     * @param filter
     * @param values
     * @throws FilterNotFoundException
     * @throws FilterLoopException
     */
    public void setFilterValue(Filter filter, String... values) throws FilterNotFoundException, FilterLoopException {
        IValue value = new IValueImpl();
        value.setFieldValue(Arrays.asList(values));

        ValueBuilder valueBuilder = new ValueBuilder().setValue(value);

        setFilterValueByName(filter, valueBuilder, filter.getName());
    }

    /**
     * It will apply a value to a filter (it will be recursively applied)
     * 
     * @param filter
     * @param valueBuilder
     * @throws FilterNotFoundException
     * @throws FilterLoopException
     */
    public void setFilterValue(Filter filter, ValueBuilder valueBuilder)
            throws FilterNotFoundException, FilterLoopException {
        setFilterValueByName(filter, valueBuilder, filter.getName());
    }

    /**
     * It will apply Values recursively a Base Filter for a sub filter name
     * 
     * @param baseFilter
     * @param valueBuilder
     * @param filterName
     * @throws FilterNotFoundException
     * @throws FilterLoopException
     */
    public void setFilterValueByName(Filter baseFilter, ValueBuilder valueBuilder, String filterName)
            throws FilterNotFoundException, FilterLoopException {

        if (baseFilter == null) {
            throw new FilterNotFoundException(filterName + " Filter is unavailable");
        }

        String parentFilterName = baseFilter.getName();
        // if parent is the filter we are looking for
        if (parentFilterName == null) {
            baseFilter.setName(filterName);
        }
        if (!hasConditions(baseFilter)) {
            setChildrenFilterValue(baseFilter, valueBuilder);
        } else {
            // we have to search on subfilters
            Optional<Filter> optFilter = this.getFilterRecursivelyByName(baseFilter, filterName);
            if (!optFilter.isPresent()) {
                throw new FilterNotFoundException(filterName + " sub Filter is unavailable");
            }

            // no subfilter found, try default from conditions
            if (parentFilterName.equals(optFilter.get().getName())) {
                setDefaultConditionsFilter(baseFilter);
                setChildrenFilterValue(baseFilter, valueBuilder);
            } else if (hasConditions(baseFilter)) {
                for (Condition conditionItem : baseFilter.getConditions().getCondition()) {
                    Optional<Filter> opConditionFilter = getFilterFromConditionOrByRef(conditionItem);
                    // to do get filter by ref
                    Optional<Filter> optSubFilter = this.getFilterRecursivelyByName(opConditionFilter.get(),
                            filterName);
                    // if the filter we are searching for it's in this condition
                    if (optSubFilter.isPresent()) {
                        setChildrenFilterValue(conditionItem.getFilter(), valueBuilder);
                    }
                }
            }
        }
    }

    /**
     * It will return a filter name or a default one if the filter name doesn't
     * exist
     * 
     * @param filter
     * @return
     */
    public static String getFilterOrDefaultName(Filter filter) {
        // set a default name for this filter
        String fieldName = filter.getName();
        if (fieldName == null) {
            return FILTER_CUSTOM_PREFIX + filter.getIndexField();
        }
        return fieldName;
    }

    /**
     * Get the filter field
     * 
     * @param fieldService
     * @param filter
     * @return
     */
    public Field getFieldFromRefOrIndex(SearchFieldService fieldService, Filter filter) {
        String ref = filter.getFieldRef();
        if (ref != null) {
            return fieldService.getFieldByName(ref);
        } else {
            return fieldService.createFieldFromIndexFieldName(filter.getIndexField(), filter.getFieldType());
        }
    }

    private void setChildrenFilterValue(Filter baseFilter, ValueBuilder valueBuilder) throws FilterNotFoundException {

        if (baseFilter == null) {
            throw new FilterNotFoundException("Cannot set children value to a null Filter");
        }

        if (!hasConditions(baseFilter)) {
            baseFilter.setValue(valueBuilder.addConfigurationValue(baseFilter.getValue()).build());
        } else {
            for (Condition conditionItem : baseFilter.getConditions().getCondition()) {
                Optional<Filter> opConditionFilter = getFilterFromConditionOrByRef(conditionItem);
                // to do get filter by ref
                setChildrenFilterValue(opConditionFilter.get(), valueBuilder);
            }
        }
    }
}
