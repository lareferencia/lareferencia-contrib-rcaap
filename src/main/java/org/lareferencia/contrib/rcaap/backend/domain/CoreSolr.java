
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

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.lareferencia.core.entity.indexing.service.EntityJsonSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SolrDocument(collection = CoreSolr.COLLECTION)
@JsonSerialize(using = EntityJsonSerializer.class, as = CoreSolr.class)
public class CoreSolr {
    public static final String COLLECTION = "core";
    public static final String RIGHTS_ACCESS_FIELD = "rights.access";
    public static final String DATE_HARVESTED_FIELD = "date.harvested";
    public static final String NETWORK_ACRONYM_FIELD = "network_acronym_str";

    public enum COAR_RIGHTS_ACCESS {
        OPEN_ACCESS("http://purl.org/coar/access_right/c_abf2", "open access"), EMBARGOED_ACCESS(
                "http://purl.org/coar/access_right/c_f1cf", "embargoed access"), RESTRICTED_ACCESS(
                        "http://purl.org/coar/access_right/c_16ec", "restricted access"), METADATA_ONLY_ACCESS(
                                "http://purl.org/coar/access_right/c_14cb", "metadata only access");

        public final String label;
        public final String uri;

        private COAR_RIGHTS_ACCESS(String uri, String label) {
            this.label = label;
            this.uri = uri;
        }

        public static COAR_RIGHTS_ACCESS valueOfURI(String uri) {
            for (COAR_RIGHTS_ACCESS e : values()) {
                if (e.uri.equals(uri)) {
                    return e;
                }
            }
            return null;
        }
    }

    @Id
    @Indexed(name = "id", type = "string")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Indexed(name = "country", type = "string", required = true)
    private String country;

    @Indexed(name = CoreSolr.NETWORK_ACRONYM_FIELD, type = "string", required = true)
    private String networkAcronymStr;

    @Indexed(name = "network_name_str", type = "string", required = true)
    private String networkNameStr;

    @Indexed(name = "title.main", type = "text_rev", required = true)
    private String[] titleMain;

    @Indexed(name = "creator", type = "strings", required = true)
    private String[] creator;

    @Indexed(name = "contributor.all", type = "strings", required = true)
    private String[] contributorAll;

    @Indexed(name = "contributor.other", type = "strings", required = true)
    private String[] contributorOther;

    @Indexed(name = "subject", type = "text_rev", required = true)
    private String[] subject;

    @Indexed(name = "abstract", type = "text_rev", required = false)
    private String[] abstractField;

    @Indexed(name = "description.version", type = "strings", required = true)
    private String[] descriptionVersion;

    @Indexed(name = "type.coar", type = "strings", required = true)
    private String[] typeCoar;

    @Indexed(name = "identifier.citation", type = "strings", required = true)
    private String[] identifierCitation;

    @Indexed(name = "identifier.issn", type = "strings", required = true)
    private String[] identifierIssn;

    @Indexed(name = "link", type = "strings", required = true)
    private String[] link;

    @Indexed(name = "identifier.doi", type = "strings", required = true)
    private String[] identifierDoi;

    @Indexed(name = "language", type = "strings", required = true)
    private String[] language;

    @Indexed(name = CoreSolr.RIGHTS_ACCESS_FIELD, type = "strings", required = true)
    private String[] rightsAccess;

    @Indexed(name = "publisher", type = "strings", required = true)
    private String[] publisher;

    @Indexed(name = CoreSolr.DATE_HARVESTED_FIELD, type = "date", required = true)
    private Date dateHarvested;

    @Indexed(name = "date.accessioned", type = "dates", required = true)
    private Date[] dateAccessioned;

    @Indexed(name = "date.available", type = "dates", required = true)
    private Date[] dateAvailable;

    @Indexed(name = "date.issued", type = "int", required = true)
    private int dateIssued;

    @Indexed(name = "date.issued.long", type = "strings", required = true)
    private String dateIssuedLong;

    @Indexed(name = "date.lastModified", type = "tdate", required = true)
    private String dateLastModified;

    @Indexed(name = "fulltext", type = "strings", required = true)
    private String[] fulltext;

    @Indexed(name = "fulltext.url", type = "string", required = true)
    private String fulltextURL;

    @Indexed(name = "title.all", type = "strings", required = true)
    private String[] titleAll;

    @Indexed(name = "subject.all", type = "strings", required = true)
    private String[] subjectAll;

    @Indexed(name = "date.all", type = "strings", required = true)
    private String[] dateAll;

    @Indexed(name = "identifier.all", type = "strings", required = true)
    private String[] identifierAll;

}
