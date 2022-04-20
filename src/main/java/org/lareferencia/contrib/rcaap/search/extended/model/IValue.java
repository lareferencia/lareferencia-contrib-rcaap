
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

import org.lareferencia.contrib.rcaap.search.model.ConditionalOperatorEnumType;
import org.lareferencia.contrib.rcaap.search.model.QuantifierEnumType;

public interface IValue {
    /**
     * Get all Values
     * 
     * @return String[]
     */
    public Iterable<String> getFieldValue();

    /**
     * Get the Value Occurrences
     * 
     * @return
     */
    public QuantifierEnumType getOccurs();

    /**
     * Get the Conditional operator for the value
     * 
     * @return
     */
    public ConditionalOperatorEnumType getCondition();

    /**
     * Setting occurrences
     * 
     * @param value
     */
    public void setOccurs(QuantifierEnumType value);

    /**
     * Setting Conditional Operator
     * 
     * @param value
     */
    public void setCondition(ConditionalOperatorEnumType value);

    /**
     * Set values
     * 
     * @param fieldValue
     */
    public void setFieldValue(Iterable<String> fieldValue);
    

    /**
     * Set values
     * 
     * @param fieldValue
     */
    public void setFieldValue(String... fieldValue);    
}
