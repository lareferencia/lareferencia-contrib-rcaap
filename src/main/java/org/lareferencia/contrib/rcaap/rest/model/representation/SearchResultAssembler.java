
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

import java.util.Collections;

import org.lareferencia.contrib.rcaap.search.services.ISearchResult;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.RepresentationModelAssembler;

//TODO: study - RepresentationModelAssemblerSupport abstract class
public class SearchResultAssembler implements RepresentationModelAssembler<ISearchResult, SearchResult> {

    @Override
    public SearchResult toModel(ISearchResult queryResults) {
        EntityAssembler entityAssembler = new EntityAssembler();
        SearchResult searchResult = new SearchResult();

        PageMetadata metadata = new PagedModel.PageMetadata(queryResults.getResults().getSize(),
                queryResults.getResults().getNumber(), queryResults.getResults().getTotalElements(),
                queryResults.getResults().getTotalPages());

        searchResult.setResults(PagedModel
                .of(entityAssembler.toCollectionModel(queryResults.getResults().getContent()).getContent(), metadata));

        // Create empty facet list
        searchResult.setFacets(Collections.emptyList());

        return searchResult;
    }
}
