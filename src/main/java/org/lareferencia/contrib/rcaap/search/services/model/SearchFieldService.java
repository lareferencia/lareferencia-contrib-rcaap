
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

package org.lareferencia.contrib.rcaap.search.services.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lareferencia.contrib.rcaap.search.model.Field;
import org.lareferencia.contrib.rcaap.search.model.FieldEnumType;
import org.lareferencia.contrib.rcaap.search.model.Fields;

public class SearchFieldService {
    private Fields serviceFields;
    private Map<String, Field> fieldsMap;
    private Map<String, Field> fieldsIndexMap;

    private List<String> indexFieldsCache;

    private final String FIELD_CUSTOM_PREFIX = "custom_field_";

    private static SearchConfigurationObjectFactoryService objFactoryService = new SearchConfigurationObjectFactoryService();

    public SearchFieldService(Fields fields) {
        this.serviceFields = fields;

        setFieldsHash(getServiceFields());
    }

    private void setFieldsHash(Fields fields) {
        this.fieldsMap = new HashMap<String, Field>();
        this.fieldsIndexMap = new HashMap<String, Field>();

        for (Field field : fields.getField()) {
            fieldsMap.put(field.getName(), field);
            fieldsIndexMap.put(field.getIndexField(), field);
        }
    }

    public Fields getServiceFields() {
        if (this.serviceFields == null) {
            this.serviceFields = objFactoryService.getObjectFactory().createFields();
        }
        return this.serviceFields;
    }

    public Field getFieldByName(String name) {
        Field field = null;
        return this.fieldsMap.getOrDefault(name, field);
    }

    public Field getFieldByNameIndex(String indexName) {
        Field field = null;
        return this.fieldsIndexMap.getOrDefault(indexName, field);
    }

    public Field createFieldFromIndexFieldName(String indexFieldName, FieldEnumType fieldType) {
        Field field = objFactoryService.getObjectFactory().createField();
        field.setName(FIELD_CUSTOM_PREFIX + indexFieldName);
        field.setIndexField(indexFieldName);
        if (fieldType != null) {
            field.setType(fieldType);
        }

        return field;
    }

    public static Fields cloneFields(Fields fields) {
        Fields clonedFields = objFactoryService.getObjectFactory().createFields();

        for (Field field : fields.getField()) {
            clonedFields.getField().add(cloneField(field));
        }
        return clonedFields;
    }

    public static Field cloneField(Field field) {
        Field clonedField = objFactoryService.getObjectFactory().createField();

        clonedField.setName(field.getName());
        clonedField.setIndexField(field.getIndexField());
        return clonedField;
    }

    public List<String> getConfigurationIndexFields() {
        if (indexFieldsCache == null) {

            indexFieldsCache = new ArrayList<String>();
            for (Field field : getServiceFields().getField()) {
                indexFieldsCache.add(field.getIndexField());
            }
        }
        return indexFieldsCache;
    }

}
