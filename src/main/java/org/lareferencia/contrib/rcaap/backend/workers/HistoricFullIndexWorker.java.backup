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

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.backend.domain.Network;
import org.lareferencia.contrib.rcaap.backend.domain.Historic;
import org.lareferencia.contrib.rcaap.backend.domain.HistoricSolr;
import org.lareferencia.contrib.rcaap.backend.repositories.jpa.HistoricRepository;
import org.lareferencia.contrib.rcaap.backend.repositories.solr.HistoricSolrRepository;
import org.lareferencia.core.worker.BaseBatchWorker;
import org.lareferencia.core.worker.NetworkRunningContext;
import org.springframework.beans.factory.annotation.Autowired;

public class HistoricFullIndexWorker extends BaseBatchWorker<Historic, NetworkRunningContext> {

    private static Logger logger = LogManager.getLogger(HistoricFullIndexWorker.class);

    @Autowired
    HistoricRepository historicRepository;

    @Autowired
    HistoricSolrRepository historicSolrRepository;

    private List<HistoricSolr> historicSolr;

    NumberFormat percentajeFormat = NumberFormat.getPercentInstance();

    @Override
    public void preRun() {
        Optional<Network> optCurrentNetwork = Optional.of(runningContext.getNetwork());

        if (!optCurrentNetwork.isPresent() || optCurrentNetwork.get().getId() == null) {
            logger.warn("Network with id: " + optCurrentNetwork.get().getId() + " doesn't exist.");
            return;
        }

        // Remove all from solr
        // Based on:
        // https://docs.spring.io/spring-data/solr/docs/current/reference/html/#appendix.query.method.subject
        historicSolrRepository.deleteByNetworkId(optCurrentNetwork.get().getId());

        // establece una paginator para recorrer los registros que no sean inválidos
        HistoricPaginator paginator = new HistoricPaginator(historicRepository, optCurrentNetwork.get());

        this.setPaginator(paginator);

        logger.info("Indexing all Historic for Network: " + optCurrentNetwork.get().getAcronym() + "(id:"
                + optCurrentNetwork.get().getId() + ")");

    }

    @Override
    public void prePage() {
        historicSolr = new LinkedList<HistoricSolr>();
    }

    @Override
    public void processItem(Historic historic) {
        logger.debug("Historic INFO: " + historic.toString());
        this.historicSolr.add(new HistoricSolr(historic));
    }

    @Override
    public void postPage() {
        if (!historicSolr.isEmpty()) {
            historicSolrRepository.saveAll(historicSolr);
        }
    }

    @Override
    public void postRun() {
    }

    @Override
    public String toString() {
        return "Historic[" + percentajeFormat.format(this.getCompletionRate()) + "]";
    }

}
