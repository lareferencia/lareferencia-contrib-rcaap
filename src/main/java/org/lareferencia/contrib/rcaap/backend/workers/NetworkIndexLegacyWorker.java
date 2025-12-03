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
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.backend.domain.Network;
import org.lareferencia.backend.repositories.jpa.NetworkRepository;
import org.lareferencia.contrib.rcaap.backend.domain.NetworkRCAAPSolr;
import org.lareferencia.contrib.rcaap.backend.repositories.solr.NetworkSolrRepository;
import org.lareferencia.core.worker.BaseWorker;
import org.lareferencia.core.worker.NetworkRunningContext;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

public class NetworkIndexLegacyWorker extends BaseWorker<NetworkRunningContext> {

    /**
     * fin de Datos para la exclusión
     */

    @Getter
    @Setter
    private boolean executeDeletion;

    private static Logger logger = LogManager.getLogger(NetworkIndexLegacyWorker.class);

    NumberFormat percentajeFormat = NumberFormat.getPercentInstance();

    @Autowired
    NetworkSolrRepository networkSolrRepository;

    @Autowired
    private NetworkRepository networkRepository;

    @Override
    public String toString() {
        return "Network" + (executeDeletion ? " delete index" : " index");
    }

    @Override
    public void run() {
        Long id = runningContext.getNetwork().getId();
        if (id == null) {
            logger.error("Identifier not found... for " + this.runningContext.toString());
            return;
        }
        if (!executeDeletion) {
            Optional<Network> currentNetwork = networkRepository.findById(id);

            if (!currentNetwork.isPresent()) {
                logger.warn("Network with id: " + id + " doesn't exist.");
                return;
            }
            NetworkRCAAPSolr networkSolr = new NetworkRCAAPSolr(currentNetwork.get());
            String networkAcronymn = currentNetwork.get().getAcronym();

            networkSolr.setId(id);
            networkSolr.setAcronym(networkAcronymn);
            NetworkRCAAPSolr x = networkSolrRepository.save(networkSolr);
            logger.info("END - Indexed network: " + networkAcronymn);
            logger.debug("saved network: " + x);
        } else {
            networkSolrRepository.deleteById(id);
            logger.info("Delete network from index: " + id);
        }
    }

}
