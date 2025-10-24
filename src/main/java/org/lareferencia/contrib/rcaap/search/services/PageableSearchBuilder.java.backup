
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

package org.lareferencia.contrib.rcaap.search.services;

import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.Optional;

import org.lareferencia.contrib.rcaap.search.model.Field;
import org.lareferencia.contrib.rcaap.search.model.OrderEnumType;
import org.lareferencia.contrib.rcaap.search.model.Sort;
import org.lareferencia.contrib.rcaap.search.services.model.SearchFieldService;
import org.lareferencia.contrib.rcaap.search.services.model.SearchSizeService;
import org.lareferencia.contrib.rcaap.search.services.model.SearchSortService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

public class PageableSearchBuilder {
    private static final int SIZE = 10;
    Integer page;
    Integer size;
    Sort sort;
    Direction order;
    SearchSizeService sizeService;
    SearchSortService sortsService;
    SearchFieldService fieldService;

    public PageableSearchBuilder(ISearchConfigurationContext searchConfigurationContext) {
        sortsService = searchConfigurationContext.getSearchSortService();
        sizeService = searchConfigurationContext.getSearchSizeService();
        fieldService = searchConfigurationContext.getSearchFieldService();

        this.setDefaults();
    }

    private void setDefaults() {
        this.page = 0;

        Sort defaultSort = sortsService.getDefaultSort();
        if (defaultSort != null) {
            setSort(defaultSort);
        }

        this.size = (sizeService.getServiceSizes().getDefault() != null)
                ? sizeService.getServiceSizes().getDefault().intValue()
                : SIZE;
        setOrder(Direction.ASC);
    }

    public PageableSearchBuilder setFromPageable(Pageable pageable) {
        if (pageable != null) {
            org.springframework.data.domain.Sort pageableSort = pageable.getSort();

            if (!pageableSort.isEmpty()) {
                for (org.springframework.data.domain.Sort.Order order : pageableSort) {
                    this.setSort(order.getProperty());
                    this.setOrder(order.getDirection().toString().toUpperCase());
                }
            }

            this.setPage(pageable.getPageNumber()).setSize(pageable.getPageSize());
        }
        return this;
    }

    public PageableSearchBuilder setOrder(String order) {
        if (order != null) {
            return setOrder(OrderEnumType.fromValue(order));
        }
        return this;
    }

    public PageableSearchBuilder setOrder(OrderEnumType order) {
        if (order != null) {
            switch (order) {
            case ASC:
                setOrder(Direction.ASC);
                break;
            case DESC:
                setOrder(Direction.DESC);
                break;
            default:
                setOrder(Direction.ASC);
                break;
            }
        }
        return this;
    }

    private void setOrder(Direction order) {
        this.order = order;
    }

    /**
     * Set Sort based on searchConfigurations Note: setSort must be called before
     * {@link setOrder}() if you want to consider sort default direction
     * 
     * @param sort
     * @return SearchApiPageable builder
     */
    public PageableSearchBuilder setSort(String sort) {
        // TODO: throw an exception if sort doesnt exists?
        if (sortsService.isValidSort(sort)) {
            Sort configSort = sortsService.getSortByName(sort);
            setSort(configSort);
            // Set default direction
            setOrder(configSort.getDefaultDirection());
        }
        return this;
    }

    public PageableSearchBuilder setSort(Sort searchSort) {
        this.sort = searchSort;
        return this;
    }

    public Optional<Sort> getSort () {
        return Optional.ofNullable(this.sort);
    }    
    
    public PageableSearchBuilder setSize(String size) {
        return setSize(Integer.valueOf(size));
    }

    public PageableSearchBuilder setSize(BigInteger size) {
        return setSize(size.intValue());
    }

    public PageableSearchBuilder setSize(int size) {
        return setSize(Integer.valueOf(size));
    }

    public PageableSearchBuilder setSize(Integer size) {
        if (sizeService.isValidSize(BigInteger.valueOf(size))) {
            this.size = size;
        }
        return this;
    }

    public PageableSearchBuilder setPage(String page) {
        return setPage(Integer.valueOf(page));
    }

    public PageableSearchBuilder setPage(int page) {
        return setPage(Integer.valueOf(page));
    }

    public PageableSearchBuilder setPage(BigInteger page) {
        return setPage(page.intValue());
    }

    public PageableSearchBuilder setPage(Integer page) {
        this.page = page;
        return this;
    }

    public Pageable build() {
        if (sort != null) {
            Field sortField = sortsService.getFieldFromRefOrIndex(fieldService, sort);

            // TODO: support multiple sorts
            return PageRequest.of(page.intValue(), size.intValue(), order, sortField.getIndexField());
        }
        return PageRequest.of(page.intValue(), size.intValue());
    }
}