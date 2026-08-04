package org.lareferencia.contrib.rcaap.search.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.solr.client.solrj.beans.Field;

public abstract class AbstractBaseEntity {

    @Field("id")
    private String id;

    @Field("title")
    private String title;

    @Field("title_sort")
    private String titleSort;

    
    @Field("description")
    private String description;

    @Field("dirty")
    private Long dirty;

    @Field("visible")
    private Long visible;

    @Field("status")
    private String status;

    @Field("institution")
    private List<String> institutions;

    @Field("collection")
    private List<String> collections;

    // dynamic fields single value (*_str)
    @Field("*_str")
    private Map<String, String> dynamicStrFields = new HashMap<>();

    // dynamic fields multivalue (*_str_mv)
    @Field("*_str_mv")
    private Map<String, List<String>> dynamicStrMvFields = new HashMap<>();

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTitleSort() { return titleSort; }
    public void setTitleSort(String titleSort) { this.titleSort = titleSort; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getDirty() { return dirty; }
    public void setDirty(Long dirty) { this.dirty = dirty; }

    public Long getVisible() { return visible; }
    public void setVisible(Long visible) { this.visible = visible; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public EntityStatusType getEntityStatusType() {
        if (status != null) {
            return EntityStatusType.valueOf(status);
        } else {
            this.setStatus(EntityStatusType.NEW);
            return EntityStatusType.NEW;
        }
    }
    public void setStatus(EntityStatusType status) { this.status = status.name(); }

    public List<String> getInstitutions() { return institutions; }
    public void setInstitutions(List<String> institutions) { this.institutions = institutions; }

    public List<String> getCollections() { return collections; }
    public void setCollections(List<String> collections) { this.collections = collections; }

    public Map<String, String> getDynamicStrFields() {
        return dynamicStrFields;
    }

    public Map<String, List<String>> getDynamicStrMvFields() {
        return dynamicStrMvFields;
    }

    // helpers

    public void addDynamicField(String field, String value) {
        dynamicStrFields.put(field, value);
    }

    public void addDynamicMultiField(String field, List<String> values) {
        dynamicStrMvFields.put(field, values);
    }

    public List<String> getMultiField(String field) {
        return dynamicStrMvFields.getOrDefault(field, Collections.emptyList());
    }

    public String getField(String field) {
        return dynamicStrFields.get(field);
    }

    public void addCanonicalRepoFacetMultiField(String value) {
        addValueToMultiField("canonical_repo_facet_str_mv", value);    }

    public void addCanonicalInstFacetMultiField(String value) {
        addValueToMultiField("canonical_inst_facet_str_mv", value);
    }

    private void addValueToMultiField (String field, String value) {
        if (value == null || value.isEmpty()) { return; }

        List<String> multiField = this.getMultiField(field);
        Set<String> set = new LinkedHashSet<>(multiField != null ? multiField : List.of());

        set.add(value);
        this.addDynamicMultiField(field, new ArrayList<>(set));
    }

}
