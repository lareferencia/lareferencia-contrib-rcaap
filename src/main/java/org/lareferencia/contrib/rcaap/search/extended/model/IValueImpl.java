
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

import java.util.Arrays;

import org.lareferencia.contrib.rcaap.search.model.ConditionalOperatorEnumType;
import org.lareferencia.contrib.rcaap.search.model.QuantifierEnumType;
import org.lareferencia.contrib.rcaap.search.model.Value;

/**
 * This class is a wrapper for Value from a client since we can't use
 * model.Value class because of the default attributes that might change
 * expected result
 * 
 * @author pgraca
 *
 */
public class IValueImpl implements IValue {

    protected Iterable<String> fieldValue;
    protected QuantifierEnumType occurs;
    protected ConditionalOperatorEnumType condition;

    @Override
    public Iterable<String> getFieldValue() {
        return fieldValue;
    }

    @Override
    public QuantifierEnumType getOccurs() {
        return occurs;
    }

    @Override
    public ConditionalOperatorEnumType getCondition() {
        return condition;
    }

    @Override
    public void setOccurs(QuantifierEnumType value) {
        this.occurs = value;
    }

    @Override
    public void setCondition(ConditionalOperatorEnumType value) {
        this.condition = value;
    }

    @Override
    public void setFieldValue(String... fieldValue) {
        this.fieldValue = Arrays.asList(fieldValue);
    }

    @Override
    public void setFieldValue(Iterable<String> fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * Class to build IValues
     * 
     * @author pgraca
     *
     */
    public static class IValueBuilder {
        private IValueImpl configurationValue;

        /**
         * from a Value object
         * 
         * @param value
         * @return
         */
        public IValueBuilder fromValue(Value value) {
            this.configurationValue = new IValueImpl();

            this.configurationValue.setCondition(value.getCondition());
            this.configurationValue.setFieldValue(value.getFieldValue());
            this.configurationValue.setOccurs(value.getOccurs());

            return this;
        }

        /**
         * from a string array
         * 
         * @param value
         * @return
         */
        public IValueBuilder fromStringValues(String... value) {
            this.configurationValue = new IValueImpl();
            this.configurationValue.setFieldValue(value);
            return this;
        }

        /**
         * Build an IValueImpl
         * 
         * @return
         */
        public IValueImpl build() {
            return this.configurationValue;
        }

    }

}
