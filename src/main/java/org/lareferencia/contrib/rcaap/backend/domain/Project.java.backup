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
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;

/**
 * @author André Santos <asantos@keep.pt>
 */
@Getter
@Setter
@Entity
public class Project {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id = null;

    @Column(nullable = true)
    private String title;

    @Lob
    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private Date startDate;

    @Column(nullable = true)
    private Date endDate;

    @Column(nullable = true)
    private Double fundingAmount;

    @Column(nullable = true)
    private String fundingCurrency;

    @Column(nullable = true)
    private String reference;

    @Column(nullable = false)
    private String fundingProgram;

    @Column(nullable = true)
    private Date dateAwarded;

    @Column(nullable = true)
    private String fundRefURI;

    @Column(nullable = true)
    private String funderName;

    @Column(nullable = false)
    private String funderAcronym;

    @Column(nullable = true)
    private String funderCountry;

    @Column(nullable = false)
    private String projectID;

    public boolean isValid() {
        return (StringUtils.length(title) < 501) && (StringUtils.length(projectID) < 256)
                && StringUtils.isNotBlank(projectID) && (StringUtils.length(funderAcronym) < 256)
                && StringUtils.isNotBlank(funderAcronym) && (StringUtils.length(funderCountry) < 256)
                && (StringUtils.length(funderName) < 256) && (StringUtils.length(fundingCurrency) < 256)
                && (StringUtils.length(fundingProgram) < 256) && StringUtils.isNotBlank(fundingProgram)
                && (StringUtils.length(fundRefURI) < 256) && (StringUtils.length(reference) < 256);
    }

    @Override
    public String toString() {
        return "Project{" + "id='" + this.getId() + '\'' + ", title='" + title + '\'' + ", description='" + description
                + '\'' + ", startDate=" + startDate + ", endDate=" + endDate + ", fundingAmount=" + fundingAmount
                + ", fundingCurrency='" + fundingCurrency + '\'' + ", reference='" + reference + '\''
                + ", fundingProgram='" + fundingProgram + '\'' + ", dateAwarded=" + dateAwarded + ", fundRefURI='"
                + fundRefURI + '\'' + ", funderName='" + funderName + '\'' + ", funderAcronym='" + funderAcronym + '\''
                + ", projectID ='" + projectID + '\'' + ", funderCountry='" + funderCountry + '\'' + '}';
    }
}
