package org.lareferencia.contrib.rcaap.search.server.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.lareferencia.contrib.rcaap.search.merge.EntityType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BaseEntityType {
    EntityType type();
}