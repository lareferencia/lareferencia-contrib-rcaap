
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

package org.lareferencia.contrib.rcaap.backend.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.backend.domain.Network;
import org.lareferencia.contrib.rcaap.backend.domain.CoreSolr;
import org.lareferencia.contrib.rcaap.backend.repositories.solr.CoreSolrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CoreService {

    @Autowired
    private CoreSolrRepository coreSolrRepository;

    @Autowired
    @Qualifier("coreSolrTemplate")
    private SolrTemplate coreSolrTemplate;
    
    private String core = CoreSolr.COLLECTION;
    
    private static Logger logger = LogManager.getLogger(CoreService.class);
    
    public boolean isServiceAvaliable() {

        try {
            this.coreSolrTemplate.ping(core);
        } catch (Exception e) {
            logger.error("Core test failed. Error in SOLR connection: ");
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    public FacetPage<CoreSolr> getRightsAccessFacetsFromSolrByNetwork(Network network, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
         return coreSolrRepository.findRightsAccessByNetwork(network.getAcronym(),
                DateTimeFormatter.ISO_INSTANT.format(fromDate.toInstant(ZoneOffset.UTC)),
                DateTimeFormatter.ISO_INSTANT.format(toDate.toInstant(ZoneOffset.UTC)), pageable);
    }

}
