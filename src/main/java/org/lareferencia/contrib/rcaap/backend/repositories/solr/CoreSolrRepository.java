
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

package org.lareferencia.contrib.rcaap.backend.repositories.solr;

import org.lareferencia.contrib.rcaap.backend.domain.CoreSolr;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

public interface CoreSolrRepository extends SolrCrudRepository<CoreSolr, String> {
    // core/select?facet=on&facet.field=rights.access&indent=on&q=*:*&fq=network_acronym_str:arca&fq=date.harvested:[2020-08-01T00:00:00Z%20TO%202020-08-31T23:59:59Z]&rows=0&wt=json

    @Query(value = CoreSolr.NETWORK_ACRONYM_FIELD + ":?0", fields = { "id" }, filters = {
            CoreSolr.DATE_HARVESTED_FIELD + ":[?1 TO ?2]" })
    @Facet(fields = { CoreSolr.RIGHTS_ACCESS_FIELD }, limit = 100)
    FacetPage<CoreSolr> findRightsAccessByNetwork(String acronym, String firstDate, String lastDate, Pageable page);

}
