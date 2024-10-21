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
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.lareferencia.backend.domain.Network;

/**
 * Historic Entity
 */

@Entity
@Getter
@Setter
public class Historic {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id = null;

    @Column(nullable = false)
    private int restrictedAccess;

    @Column(nullable = false)
    private int embargoedAccess;

    @Column(nullable = false)
    private int closedAccess;

    @Column(nullable = false)
    private int metadataOnlyAccess;

    @Column(nullable = false)
    private int openAccess;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int day;

    @Column(name = "archive_acronym", nullable = false)
    private String archiveAcronym;

    @Column(name = "network_id", nullable = false)
    private long networkId;

    public static class Builder {
        protected Historic historic;

        public Builder() {
            this.historic = new Historic();
        }

        public Builder(Historic historic) {
            this.historic = historic;
        }

        public Builder setNetwork(Network network) {
            if (network == null) {
                return this;
            }
            this.historic.setArchiveAcronym(network.getAcronym());
            this.historic.setNetworkId(network.getId());
            return this;
        }

        public Builder valuesFromOther(Historic historic) {
            if (historic == null) {
                return this;
            }
            this.historic.setOpenAccess(historic.getOpenAccess());
            this.historic.setMetadataOnlyAccess(historic.getMetadataOnlyAccess());
            this.historic.setClosedAccess(historic.getClosedAccess());
            this.historic.setEmbargoedAccess(historic.getEmbargoedAccess());
            this.historic.setRestrictedAccess(historic.getRestrictedAccess());
            return this;
        }

        public Builder valuesFromCoreFacets(CoreFacets facets) {
            if (facets == null) {
                return this;
            }

            HashMap<String, Long> rightsFacets = facets.getFacetsByName(CoreSolr.RIGHTS_ACCESS_FIELD);

            this.historic.setRestrictedAccess(
                    rightsFacets.getOrDefault(CoreSolr.COAR_RIGHTS_ACCESS.RESTRICTED_ACCESS.uri, 0L).intValue());
            this.historic.setEmbargoedAccess(
                    rightsFacets.getOrDefault(CoreSolr.COAR_RIGHTS_ACCESS.EMBARGOED_ACCESS.uri, 0L).intValue());
            this.historic.setClosedAccess(0);
            this.historic.setMetadataOnlyAccess(
                    rightsFacets.getOrDefault(CoreSolr.COAR_RIGHTS_ACCESS.METADATA_ONLY_ACCESS.uri, 0L).intValue());
            this.historic.setOpenAccess(
                    rightsFacets.getOrDefault(CoreSolr.COAR_RIGHTS_ACCESS.OPEN_ACCESS.uri, 0L).intValue());

            return this;
        }

        public Builder setDateFromLocalTime(LocalDateTime date) {
            if (date == null) {
                return this;
            }
            this.historic.setYear(date.getYear());
            this.historic.setMonth(date.getMonthValue());
            // historic.setDay(date.getDayOfMonth());
            return this;
        }

        public Historic build() {
            return this.historic;
        }
        
    }
    
    @Override
    public String toString() {
        return "Historic{" + "id='" + this.getId() + '\'' + ", restrictedAccess='" + restrictedAccess + '\'' + ", embargoedAccess='" + embargoedAccess
                + '\'' + ", closedAccess=" + closedAccess + ", metadataOnlyAccess=" + metadataOnlyAccess + ", openAccess=" + openAccess
                + ", year='" + year + '\'' + ", month='" + month + '\''
                + ", archiveAcronym='" + archiveAcronym + '\'' + '}';
    }    
}
