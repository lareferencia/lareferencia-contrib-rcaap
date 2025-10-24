
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

import org.lareferencia.contrib.rcaap.search.services.IFacet;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class FacetedFieldAssembler implements RepresentationModelAssembler<IFacet, FacetedField> {

    @Override
    public FacetedField toModel(IFacet facet) {

        // get first element (key)
        FacetValueAssembler facetValueAssembler = new FacetValueAssembler();
        FacetedField facetedField = new FacetedField();
        facetedField.setFieldName(facet.getFieldName());

        PageMetadata pageMetadata = new PagedModel.PageMetadata(facet.getFacetValues().getSize(),
                facet.getFacetValues().getNumber(), facet.getFacetValues().getNumberOfElements(),
                facet.getFacetValues().getTotalPages());
        facetedField.setValues(PagedModel.of(
                facetValueAssembler.toCollectionModel(facet.getFacetValues().getContent()).getContent(), pageMetadata));

        return facetedField;
    }

}
