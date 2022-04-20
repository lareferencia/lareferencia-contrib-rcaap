
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

package org.lareferencia.contrib.rcaap.rest.model.representation;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class EntityValue {
    @Getter
    @Setter
    @JsonInclude(Include.NON_NULL)
    @JsonProperty("v")
    protected String content;

    @Getter
    @Setter
    @JsonProperty("l")
    @JsonInclude(Include.NON_NULL)
    protected String lang;
    
    @Getter
    @JsonInclude(Include.NON_NULL)
    @JsonProperty("content")
    protected Map<String, String> jsonContent;
    
    protected void setJsonContent (String strJson) {        
        Map<String, String> data = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            data =  objectMapper.readValue(strJson, Map.class);
            this.jsonContent = data;
        } catch (final IOException e) {
            //TODO handle exception
        }
 
        
    }
}
