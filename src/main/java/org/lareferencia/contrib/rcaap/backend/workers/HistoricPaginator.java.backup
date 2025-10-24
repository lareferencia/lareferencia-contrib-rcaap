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

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lareferencia.backend.domain.Network;
import org.lareferencia.contrib.rcaap.backend.domain.Historic;
import org.lareferencia.contrib.rcaap.backend.repositories.jpa.HistoricRepository;
import org.lareferencia.core.worker.IPaginator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class HistoricPaginator implements IPaginator<Historic> {

    // implements dummy starting page
    @Override
    public int getStartingPage() { return 1; }

    private static final int DEFAULT_PAGE_SIZE = 12;
    private static Logger logger = LogManager.getLogger(HistoricPaginator.class);

    private int pageSize = DEFAULT_PAGE_SIZE;
    private int totalPages = 0;

    private HistoricRepository historicRepository;

    private Network network;

    private LocalDateTime date;

    /**
     * Este pedido paginado pide siempre la primera página restringida a que el id
     * sea mayor al ultimo anterior
     **/
    public HistoricPaginator(HistoricRepository historicRepository, Network network) {

        this.historicRepository = historicRepository;
        this.network = network;
        obtainPage();
    }

    public HistoricPaginator(HistoricRepository historicRepository, Network network, LocalDateTime date) {

        this.historicRepository = historicRepository;
        this.network = network;
        this.date = date;
        obtainPage();
    }

    public int getTotalPages() {
        return totalPages;
    }

    public Page<Historic> nextPage() {
        return obtainPage();
    }

    public void setPageSize(int size) {
        this.pageSize = size;
    }

    private Page<Historic> obtainPage() {

        Page<Historic> page = null;

        try {
            if (date == null) {
                page = historicRepository.findLastNByNetworkID(network.getId(),
                        PageRequest.of(0, pageSize, Sort.Direction.DESC, "year", "month"));
            } else {
                List<Historic> historicList = new LinkedList<Historic>();
                Historic historicFromDB = historicRepository.findNetworkMonthHistoric(network.getId(),
                        date.getDayOfMonth(), date.getYear());
                if (historicFromDB != null) {
                    historicList.add(historicFromDB);
                }
                page = new PageImpl<Historic>(historicList, PageRequest.of(0, pageSize), historicList.size());
            }

            this.totalPages = page.getTotalPages();
        } catch (Exception | Error e) {
            logger.fatal(e.getMessage());
            e.printStackTrace();
        }

        return page;

    }
}
