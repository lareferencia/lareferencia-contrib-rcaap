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
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.Date;

@Getter
@Setter
@SolrDocument(collection = ProjectSolr.COLLECTION)
public class ProjectSolr {
    public static final String COLLECTION = "projects";

    @Id
    @Indexed(name = "id", type = "string")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Indexed
    private String title;

    @Indexed
    private String description;

    @Indexed(value = "start_date")
    private Date startDate;

    @Indexed(value = "end_date")
    private Date endDate;

    @Indexed(value = "funding_amount")
    private Double fundingAmount;

    @Indexed(value = "funding_currency")
    private String fundingCurrency;

    @Indexed
    private String reference;

    @Indexed(value = "funding_program")
    private String fundingProgram;

    @Indexed(value = "date_awarded")
    private Date dateAwarded;

    @Indexed(value = "fundref_uri")
    private String fundRefURI;

    @Indexed(value = "funder_name")
    private String funderName;

    @Indexed(value = "funder_acronym")
    private String funderAcronym;

    @Indexed(value = "funder_country")
    private String funderCountry;

    @Indexed(value = "project_id")
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
        return "Project{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", description='" + description + '\''
                + ", startDate=" + startDate + ", endDate=" + endDate + ", fundingAmount=" + fundingAmount
                + ", fundingCurrency='" + fundingCurrency + '\'' + ", reference='" + reference + '\''
                + ", fundingProgram='" + fundingProgram + '\'' + ", dateAwarded=" + dateAwarded + ", fundRefURI='"
                + fundRefURI + '\'' + ", funderName='" + funderName + '\'' + ", funderAcronym='" + funderAcronym + '\''
                + ", funderCountry='" + funderCountry + '\'' + '}';
    }
}
