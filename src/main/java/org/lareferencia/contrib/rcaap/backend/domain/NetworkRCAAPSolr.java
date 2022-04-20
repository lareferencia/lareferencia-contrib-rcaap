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

import java.util.List;
import java.util.Map;

import org.lareferencia.backend.domain.Network;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@Getter
@Setter
@SolrDocument(collection = NetworkRCAAPSolr.COLLECTION)
public class NetworkRCAAPSolr {
    public static final String COLLECTION = "networks";

    @Id
    @Indexed(name = "id", type = "int")
    private Long id;

    @Indexed(type = "string", required = true)
    private String name;

    @Indexed(type = "string", name = "institution", required = true)
    private String institutionName;

    @Indexed(type = "string", required = false)
    private String isni;

    @Indexed(type = "string", required = false)
    private String ringold;

    @Indexed(type = "string", required = true)
    private String acronym;

    @Indexed(type = "string", required = false)
    private String type;

    @Indexed(type = "string", required = false)
    private String software;

    @Indexed(type = "string", required = false)
    private String email;

    @Indexed(type = "string", required = false)
    private String url;

    @Indexed(type = "strings", required = false)
    private List<String> tags;

    @Indexed(type = "string", required = false, name = "description.pt")
    private String description_pt;

    @Indexed(type = "string", required = false, name = "description.en")
    private String description_en;

    @Indexed(type = "string", required = false)
    private String country;

    @Indexed(type = "string", required = false, name = "directory.url")
    private String directoryURL;

    @Indexed(type = "string", required = false, name = "oai.url")
    private String oaiURL;

    @Indexed(name = "roarmap", type = "string", required = false)
    private String roarMap;

    @Indexed(name = "opendoar", type = "string", required = false)
    private String openDoar;

    @Indexed(name = "sherpa", type = "string", required = false)
    private String sherpa;

    @Indexed(type = "string", required = false, name = "eissn")
    private String eissn;

    @Indexed(type = "string", required = false)
    private String pissn;

    @Indexed(type = "string", required = false)
    private String issnL;

    @Indexed(type = "string", required = false)
    private String handle;

    @Indexed(type = "boolean", defaultValue = "false", required = false)
    private Boolean degois;

    @Indexed(type = "boolean", defaultValue = "false", required = false)
    private Boolean cienciaVitae;

    @Indexed(type = "boolean", defaultValue = "false", required = false)
    private Boolean cienciaId;

    @Indexed(type = "boolean", defaultValue = "false", required = false, name = "openAIRE2")
    private Boolean openAIRE = false;

    @Indexed(type = "boolean", defaultValue = "false", required = false)
    private Boolean openAIRE4 = false;

    @Indexed(type = "boolean", defaultValue = "false", required = false)
    private Boolean driver = false;

    @Indexed(type = "boolean", defaultValue = "false", required = false)
    private Boolean fct = false;

    @Indexed(type = "boolean", defaultValue = "false", required = false)
    private Boolean thesis = false;

    @Indexed(type = "boolean", defaultValue = "false", required = false)
    private Boolean fulltext = false;

    @Indexed(type = "boolean", defaultValue = "false", required = false)
    private Boolean accessibleContent = false;

    @Indexed(type = "string", required = false)
    private String handlePrefix;

    @Indexed(type = "string", required = false)
    private String doiPrefix;

    public NetworkRCAAPSolr(Network other) {
        Map<String, Object> attributes = other.getAttributes();
        this.id = other.getId();
        this.name = other.getName();
        this.institutionName = other.getInstitutionName();
        this.acronym = other.getAcronym();

        this.isni = (String) attributes.get("isni"); // this.isni = attributes.get(Isni();
        this.ringold = (String) attributes.get("ringold");

        this.type = (String) attributes.get("type");
        this.software = (String) attributes.get("software");
        this.email = (String) attributes.get("email");
        this.url = (String) attributes.get("url");
        this.tags = ((List<String>) attributes.get("tags"));
        this.description_pt = (String) attributes.get("description_pt");
        this.description_en = (String) attributes.get("description_en");
        this.country = (String) attributes.get("country");
        this.directoryURL = (String) attributes.get("directoryURL");
        this.oaiURL = (String) attributes.get("oaiURL");
        this.roarMap = (String) attributes.get("roarMap");
        this.openDoar = (String) attributes.get("openDoar");
        this.sherpa = (String) attributes.get("sherpa");
        this.eissn = (String) attributes.get("eissn");
        this.pissn = (String) attributes.get("pissn");
        this.issnL = (String) attributes.get("issnL");
        this.handle = (String) attributes.get("handle");
        this.handlePrefix = (String) attributes.get("handlePrefix");
        this.doiPrefix = (String) attributes.get("doiPrefix");

        this.degois = Boolean.valueOf(attributes.get("degois").toString());
        this.cienciaVitae = Boolean.valueOf(attributes.get("cienciaVitae").toString());
        this.cienciaId = Boolean.valueOf(attributes.get("cienciaId").toString());
        this.openAIRE = Boolean.valueOf(attributes.get("openAIRE").toString());
        this.openAIRE4 = Boolean.valueOf(attributes.get("openAIRE4").toString());
        this.driver = Boolean.valueOf(attributes.get("driver").toString());
        this.fct = Boolean.valueOf(attributes.get("fct").toString());
        this.thesis = Boolean.valueOf(attributes.get("thesis").toString());
        this.fulltext = Boolean.valueOf(attributes.get("fulltext").toString());
        this.accessibleContent = Boolean.valueOf(attributes.get("accessibleContent").toString());
    }
}