
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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.03.29 at 08:39:01 AM GMT 
//


package org.lareferencia.contrib.rcaap.search.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.lareferencia.info/schema/4.0/search}value" minOccurs="0"/>
 *         &lt;element ref="{http://www.lareferencia.info/schema/4.0/search}conditions" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="index-field" type="{http://www.lareferencia.info/schema/4.0/search}indexFieldType" />
 *       &lt;attribute name="field-type" type="{http://www.lareferencia.info/schema/4.0/search}fieldEnumType" default="STRING" />
 *       &lt;attribute name="field-ref" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value",
    "conditions"
})
@XmlRootElement(name = "filter")
public class Filter {

    protected Value value;
    protected Conditions conditions;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "index-field")
    protected String indexField;
    @XmlAttribute(name = "field-type")
    protected FieldEnumType fieldType;
    @XmlAttribute(name = "field-ref")
    protected String fieldRef;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link Value }
     *     
     */
    public Value getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link Value }
     *     
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * Gets the value of the conditions property.
     * 
     * @return
     *     possible object is
     *     {@link Conditions }
     *     
     */
    public Conditions getConditions() {
        return conditions;
    }

    /**
     * Sets the value of the conditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Conditions }
     *     
     */
    public void setConditions(Conditions value) {
        this.conditions = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the indexField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndexField() {
        return indexField;
    }

    /**
     * Sets the value of the indexField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndexField(String value) {
        this.indexField = value;
    }

    /**
     * Gets the value of the fieldType property.
     * 
     * @return
     *     possible object is
     *     {@link FieldEnumType }
     *     
     */
    public FieldEnumType getFieldType() {
        if (fieldType == null) {
            return FieldEnumType.STRING;
        } else {
            return fieldType;
        }
    }

    /**
     * Sets the value of the fieldType property.
     * 
     * @param value
     *     allowed object is
     *     {@link FieldEnumType }
     *     
     */
    public void setFieldType(FieldEnumType value) {
        this.fieldType = value;
    }

    /**
     * Gets the value of the fieldRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldRef() {
        return fieldRef;
    }

    /**
     * Sets the value of the fieldRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldRef(String value) {
        this.fieldRef = value;
    }

}
