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
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.backend.domain.Network;
import org.lareferencia.contrib.rcaap.backend.domain.CoreFacets;
import org.lareferencia.contrib.rcaap.backend.domain.Historic;
import org.lareferencia.contrib.rcaap.backend.services.CoreService;
import org.lareferencia.contrib.rcaap.backend.services.HistoricService;
import org.lareferencia.core.worker.BaseBatchWorker;
import org.lareferencia.core.worker.NetworkRunningContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

public class HistoricWorker extends BaseBatchWorker<CoreFacets, NetworkRunningContext> {

    /**
     * fin de Datos para la exclusión
     */

    private static Logger logger = LogManager.getLogger(HistoricWorker.class);

    @Autowired
    private CoreService coreService;

    @Autowired
    private HistoricService historicService;

    private LocalDateTime currentDate;

    NumberFormat percentajeFormat = NumberFormat.getPercentInstance();

    public HistoricWorker() {
        super();
    }

    @Override
    public void preRun() {
        // busca el lgk
        Optional<Network> optCurrentNetwork = Optional.of(runningContext.getNetwork());

        if (!optCurrentNetwork.isPresent() || optCurrentNetwork.get().getId() == null) {
            logger.fatal("Network with id: " + runningContext.getId() + " doesn't exist.");
            return;
        }

        logger.info("Processing Historic for Network: " + runningContext.toString());

        this.currentDate = LocalDateTime.now();
        LocalDateTime fromDate = currentDate.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);

        LocalDateTime toDate = currentDate.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59)
                .withSecond(59).withNano(999999999);

        CoreFacetsPaginator paginator = new CoreFacetsPaginator(coreService.getRightsAccessFacetsFromSolrByNetwork(
                optCurrentNetwork.get(), fromDate, toDate, PageRequest.of(0, 1)));
        this.setPaginator(paginator);

    }

    @Override
    public void prePage() {
    }

    @Override
    public void processItem(CoreFacets facets) {
        Historic historic = new Historic.Builder().valuesFromCoreFacets(facets).setDateFromLocalTime(currentDate)
                .setNetwork(runningContext.getNetwork()).build();
        logger.debug(
                "Creating historic for: " + historic.getYear() + "-" + historic.getMonth() + "-" + historic.getDay());
        historicService.save(historic);
    }

    @Override
    public void postPage() {
    }

    @Override
    public void postRun() {
        logger.info("Processed Historic for: " + runningContext.toString() + " with " + paginator.getTotalPages() + " pages" );
    }

    @Override
    public String toString() {
        return "Historic[" + percentajeFormat.format(this.getCompletionRate()) + "]";
    }

}
