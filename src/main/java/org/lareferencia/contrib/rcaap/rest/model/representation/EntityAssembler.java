
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.lareferencia.core.entity.indexing.service.EntityFieldValue;
import org.lareferencia.core.entity.indexing.service.EntityFieldValue.LangFieldType;
import org.lareferencia.core.entity.indexing.service.IEntity;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class EntityAssembler implements RepresentationModelAssembler<IEntity, Entity> {
    private String entityName = org.lareferencia.core.entity.domain.Entity.NAME;

    @Override
    public Entity toModel(IEntity iEntity) {
        Entity entity = new Entity();

        entity.setId(iEntity.getId());
        entity.setType(iEntity.getType());

        Map<String, List<EntityValue>> fields = new HashMap<String, List<EntityValue>>();
        for (Map.Entry<String, List<EntityFieldValue>> occrs : iEntity.getFieldOccurrenceMap().entrySet()) {
            List<EntityValue> values = new LinkedList<>();
            for (EntityFieldValue fieldValue : occrs.getValue()) {
                String strValue = fieldValue.getValue();
                EntityValue value = new EntityValue();

                if (verifyJSONContent(strValue)) {
                    value.setJsonContent(strValue);
                } else {
                    value.setContent(strValue);
                }

                LangFieldType lang = fieldValue.getLanguage();
                if (lang != LangFieldType.UND) {
                    value.setLang(fieldValue.getLanguage().lang_639_3);
                }
                values.add(value);
            }

            fields.put(occrs.getKey(), values);
        }
        entity.setFields(fields);

        List<Relations> relations = new LinkedList<>();
        for (String relationName : iEntity.getRelationNames()) {
            Relations entityRelation = new Relations();
            entityRelation.setField(relationName);

            RelatedEntityIdentifierAssembler relatedEntityIdAssembler = new RelatedEntityIdentifierAssembler();
            entityRelation.setEntities(relatedEntityIdAssembler
                    .toCollectionModel(iEntity.getIdentifiersByRelationName(relationName)).getContent());
            relations.add(entityRelation);
        }
        entity.setRelations(relations);

        // add default links
        WebMvcLinkBuilder baseLink = linkTo(IEntity.class).slash(entityName).slash(entity.getId());

        entity.add(baseLink.withSelfRel());
        entity.add(baseLink.withRel(LinkRelation.of(entityName)));

        // TODO check the EntityProcessor to replace these links
        //entity.add(baseLink.slash("entityType").withRel(LinkRelation.of("entityType")));

        return entity;
    }

    private boolean verifyJSONContent(String content) {
        if (content == null) {
            return false;
        }
        return (content.indexOf("{") == 0 && content.lastIndexOf("}") == content.length() - 1);
    }

}
