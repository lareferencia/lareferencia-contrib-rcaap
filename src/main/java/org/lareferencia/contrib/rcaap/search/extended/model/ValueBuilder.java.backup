
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

package org.lareferencia.contrib.rcaap.search.extended.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.lareferencia.contrib.rcaap.search.model.Value;
import org.lareferencia.contrib.rcaap.search.services.model.SearchConfigurationObjectFactoryService;

/**
 * This class holds Values that can be prioritized and reorganized and it will
 * retrieve a processed Value because one can have a Value from a Client or a
 * Configuration
 * 
 * @author pgraca
 *
 */
public class ValueBuilder {
    private HashMap<VALUE_PRIORITY, List<IValue>> cascadeValuesMap = new HashMap<>();

    /**
     * Enum for VALUE priority this is used to give the sense of cascade notion of
     * setting values 1 is the lowest priority defined
     * 
     * @author pgraca
     *
     */
    public enum VALUE_PRIORITY {
        CONFIGURATION_VALUE(1), CLIENT_VALUE(2);

        public final int priority;

        private VALUE_PRIORITY(int priority) {
            this.priority = priority;
        }

        public static VALUE_PRIORITY valueOfPriority(int priority) {
            for (VALUE_PRIORITY e : values()) {
                if (e.priority == priority) {
                    return e;
                }
            }
            return null;
        }
    }

    private static SearchConfigurationObjectFactoryService objFactoryService = new SearchConfigurationObjectFactoryService();

    /**
     * Sets a configuration Value
     * 
     * @param value
     */
    public ValueBuilder setValue(Value value) {
        if (value == null) {
            return this;
        }

        List<IValue> valueList = new LinkedList<IValue>();
        valueList.add(new IValueImpl.IValueBuilder().fromValue(value).build());
        cascadeValuesMap.put(VALUE_PRIORITY.CONFIGURATION_VALUE, valueList);
        return this;
    }
    
    
    /**
     * Sets a client Value
     * 
     * @param value
     */
    public ValueBuilder setValue(IValue value) {
        if (value == null) {
            return this;
        }

        List<IValue> valueList = new LinkedList<IValue>();
        valueList.add(value);
        cascadeValuesMap.put(VALUE_PRIORITY.CLIENT_VALUE, valueList);
        return this;
    }    

    /**
     * Adds a configuration Value
     * 
     * @param value
     */
    public ValueBuilder addConfigurationValue(Value value) {
        if (value == null) {
            return this;
        }
        return addValue(new IValueImpl.IValueBuilder().fromValue(value).build(), VALUE_PRIORITY.CONFIGURATION_VALUE);
    }

    /**
     * Adds a client Value
     * 
     * @param value
     */
    public ValueBuilder addValue(IValue value) {
        if (value == null) {
            return this;
        }
        return addValue(value, VALUE_PRIORITY.CLIENT_VALUE);
    }

    /**
     * Adds a generic Value with a priority enum
     * 
     * @param value
     * @param priority
     */
    public ValueBuilder addValue(IValue value, VALUE_PRIORITY priority) {
        List<IValue> valueList;
        switch (priority) {
        default:
            valueList = cascadeValuesMap.getOrDefault(priority, new LinkedList<IValue>());
            valueList.add(value);
            cascadeValuesMap.put(priority, valueList);
            break;
        }
        return this;
    }

    /**
     * Build and return a Value
     * 
     * @return
     */
    public Value build() {
        List<IValue> clientValues = cascadeValuesMap.getOrDefault(VALUE_PRIORITY.CLIENT_VALUE,
                new LinkedList<IValue>());
        List<IValue> configValues = cascadeValuesMap.getOrDefault(VALUE_PRIORITY.CONFIGURATION_VALUE,
                new LinkedList<IValue>());

        IValue valueHolder = new IValueImpl();

        // Configuration has low priority to code settings
        for (IValue value : configValues) {
            setDefaults(value, valueHolder);
        }
        for (IValue value : clientValues) {
            setDefaults(value, valueHolder);
        }
        return buildValue(valueHolder);
    }

    private IValue setDefaults(IValue inValue, IValue outValue) {
        if (inValue.getCondition() != null) {
            outValue.setCondition(inValue.getCondition());
        }
        if (inValue.getFieldValue() != null) {
            outValue.setFieldValue(inValue.getFieldValue());
        }
        if (inValue.getOccurs() != null) {
            outValue.setOccurs(inValue.getOccurs());
        }
        return outValue;
    }

    private Value buildValue(IValue sourceValue) {
        Value returnValue = objFactoryService.getObjectFactory().createValue();
        if (sourceValue.getOccurs() != null) {
            returnValue.setOccurs(sourceValue.getOccurs());
        }
        if (sourceValue.getCondition() != null) {
            returnValue.setCondition(sourceValue.getCondition());
        }
        if (sourceValue.getFieldValue() != null) {
            returnValue.getFieldValue().addAll(StreamSupport.stream(sourceValue.getFieldValue().spliterator(), false)
                    .collect(Collectors.toList()));
        }
        return returnValue;
    }

}
