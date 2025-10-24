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

package org.lareferencia.contrib.rcaap.backend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.lareferencia.core.entity.indexing.service.EntityJsonSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Getter
@Setter
@NoArgsConstructor
@SolrDocument(collection = HistoricSolr.COLLECTION)
@JsonSerialize(using = EntityJsonSerializer.class, as = HistoricSolr.class)
public class HistoricSolr {
    public static final String COLLECTION = "historic";

    @Id
    @Indexed(name = "id", type = "string")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Indexed(name = "restrictedaccess", type = "int", required = true)
    private int restrictedAccess;

    @Indexed(name = "embargoedaccess", type = "int", required = true)
    private int embargoedAccess;

    @Indexed(name = "closedaccess", type = "int", required = true)
    private int closedAccess;

    @Indexed(name = "metadataonlyaccess", type = "int", required = true)
    private int metadataOnlyAccess;

    @Indexed(name = "openaccess", type = "int", required = true)
    private int openAccess;

    @Indexed(name = "year", type = "int", required = true)
    private int year;

    @Indexed(name = "month", type = "string", required = true)
    private String month;

    @Indexed(name = "archive_acronym", type = "string", required = true)
    private String archiveAcronym;

    @Indexed(name = "network_id", type = "string", required = true)
    private long networkId;

    public HistoricSolr(Historic historic) {
        String month = (historic.getMonth() <= 9) ? "0" + historic.getMonth() : "" + historic.getMonth();

        this.id = historic.getNetworkId() + ":" + historic.getYear() + "-" + month;
        this.restrictedAccess = historic.getRestrictedAccess();
        this.embargoedAccess = historic.getEmbargoedAccess();
        this.closedAccess = historic.getClosedAccess();
        this.metadataOnlyAccess = historic.getMetadataOnlyAccess();
        this.openAccess = historic.getOpenAccess();
        this.year = historic.getYear();
        this.month = month;

        this.archiveAcronym = historic.getArchiveAcronym();
        this.networkId = historic.getNetworkId();
    }

}