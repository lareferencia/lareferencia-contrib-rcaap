
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

package org.lareferencia.contrib.rcaap.backend.workers;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.contrib.rcaap.backend.domain.CoreFacets;
import org.lareferencia.contrib.rcaap.backend.domain.CoreSolr;
import org.lareferencia.core.worker.IPaginator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;

public class CoreFacetsPaginator implements IPaginator<CoreFacets> {

    // implements dummy starting page
    @Override
    public int getStartingPage() { return 1; }

    private static final int DEFAULT_PAGE_SIZE = 1;
    private static Logger logger = LogManager.getLogger(CoreFacetsPaginator.class);
    private List<CoreFacets> coreFacets;

    private int pageSize = DEFAULT_PAGE_SIZE;
    private int totalPages = 0;

    public CoreFacetsPaginator(FacetPage<CoreSolr> results) {
        CoreFacets facets = new CoreFacets();
        for (Page<FacetFieldEntry> page : results.getFacetResultPages()) {
            for (FacetFieldEntry entry : page) {
                facets.addFacet(entry.getField().getName(), entry.getValue(), entry.getValueCount());
                logger.debug("Facet " + entry.getField().getName() + " value: " + entry.getValue() + " count: "
                        + entry.getValueCount());
            }
        }
        coreFacets = new LinkedList<CoreFacets>();
        if (facets.size() > 0) {
            coreFacets.add(facets);
        }
        this.totalPages = coreFacets.size();
    }

    @Override
    public Page<CoreFacets> nextPage() {
        Page<CoreFacets> page = new PageImpl<CoreFacets>(coreFacets, PageRequest.of(0, pageSize), coreFacets.size());
        return page;
    }

    @Override
    public void setPageSize(int size) {
        this.pageSize = size;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }
}
