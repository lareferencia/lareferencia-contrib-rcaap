
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.QueryParameter;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.http.HttpMethod;

/**
 * This processor is run after all is processed it's particular useful when you
 * want to add a MVC controller URL to an already processed RepresentationModel
 * 
 * @author pgraca
 *
 */
public class FacetValueProcessor implements RepresentationModelProcessor<SearchResult> {

    @Override
    public SearchResult process(SearchResult model) {

        Optional<Collection<FacetedField>> optFacets = Optional.ofNullable(model.getFacets());

        if (optFacets.isPresent()) {

            Optional<Link> optSelfLink = model.getLink(IanaLinkRelations.SELF);
            if (optSelfLink.isPresent()) {
                for (FacetedField field : model.getFacets()) {
                    for (FacetValue value : field.getValues().getContent()) {

                        // Grab the current main link and add the facet parameter filter
                        /*
                         * Link facetLink = Affordances.of(optSelfLink.get()).afford(HttpMethod.GET)
                         * .addParameters(
                         * QueryParameter.optional(field.getFieldName()).withValue(value.getValue()))
                         * .build() .toLink();
                         */
                        try {
                            Map<String, String> values = new HashMap<>();
                            values.put(field.getFieldName(), URLEncoder.encode(value.getValue(), "UTF-8"));

                            // add a parameter to the query string with the filter facet
                            // NOTE: query string fields names must not have special chars
                            value.add(optSelfLink.get().expand(values).withRel("search"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return model;
    }

}
