
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

import java.util.HashMap;
import java.util.Map;

import org.lareferencia.contrib.rcaap.search.model.Field;
import org.lareferencia.contrib.rcaap.search.model.Sort;
import org.lareferencia.contrib.rcaap.search.model.Sorts;

public class SearchSortService {
    private Sorts serviceSorts;
    private Map<String, Sort> sortsMap;

    private static SearchConfigurationObjectFactoryService objFactoryService = new SearchConfigurationObjectFactoryService();

    public SearchSortService(Sorts sorts) {
        this.serviceSorts = sorts;

        this.setSortsHash(getServiceSorts());
    }

    private void setSortsHash(Sorts sorts) {
        this.sortsMap = new HashMap<String, Sort>();
        for (Sort sort : sorts.getSort()) {
            sortsMap.put(sort.getName(), sort);
        }
    }

    public Sorts getServiceSorts() {
        if (this.serviceSorts == null) {
            this.serviceSorts = objFactoryService.getObjectFactory().createSorts();
        }
        return this.serviceSorts;
    }

    public Sort getDefaultSort() {
        String defaultSortName = this.serviceSorts.getDefault();
        return getSortByName(defaultSortName);
    }

    public Sort getSortByName(String name) {
        Sort sort = null;
        return this.sortsMap.getOrDefault(name, sort);
    }

    public Field getFieldFromRefOrIndex(SearchFieldService fieldService, Sort sort) {
        String ref = sort.getFieldRef();
        if (ref != null) {
            return fieldService.getFieldByName(ref);
        } else {
            return fieldService.createFieldFromIndexFieldName(sort.getIndexField(), sort.getFieldType());
        }
    }

    public static Sorts cloneSorts(Sorts sorts) {
        Sorts clonedSorts = objFactoryService.getObjectFactory().createSorts();

        clonedSorts.setDefault(sorts.getDefault());
        for (Sort sort : sorts.getSort()) {
            clonedSorts.getSort().add(cloneSort(sort));
        }
        return clonedSorts;
    }

    public static Sort cloneSort(Sort sort) {
        Sort clonedSort = objFactoryService.getObjectFactory().createSort();

        clonedSort.setName(sort.getName());
        clonedSort.setFieldRef(sort.getFieldRef());
        clonedSort.setIndexField(sort.getIndexField());
        clonedSort.setDefaultDirection(sort.getDefaultDirection());

        return clonedSort;
    }

    public boolean isValidSort(String sort) {
        return this.sortsMap.containsKey(sort);
    }

}
