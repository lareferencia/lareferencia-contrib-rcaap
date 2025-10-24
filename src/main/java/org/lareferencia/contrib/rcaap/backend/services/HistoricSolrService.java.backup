
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.contrib.rcaap.backend.domain.HistoricSolr;
import org.lareferencia.contrib.rcaap.backend.repositories.solr.HistoricSolrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class HistoricSolrService {

    private static Logger logger = LogManager.getLogger(HistoricSolrService.class);

    @Autowired
    @Qualifier("historicSolrTemplate")
    private SolrTemplate historicSolrTemplate;

    private String core = HistoricSolr.COLLECTION;

    @Autowired
    HistoricSolrRepository historicSolrRepository;

    public boolean isServiceAvaliable() {

        try {
            this.historicSolrTemplate.ping(core);
        } catch (Exception e) {
            logger.error("Historic test failed. Error in SOLR connection: ");
            logger.error(e.getMessage());
            return false;
        }
        
        return true;
    }


}
