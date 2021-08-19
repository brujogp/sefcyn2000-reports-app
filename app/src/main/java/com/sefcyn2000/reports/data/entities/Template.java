package com.sefcyn2000.reports.data.entities;

import com.google.firebase.Timestamp;
import com.sefcyn2000.reports.data.entities.templatecomponents.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>POJO para la generación de plantillas</p>
 */
public class Template {
    Timestamp createdAt;
    long usedCount;
    String linkedClientId;
    String address;
    String name;
    String clientName;
    String notes;
    String templateId;

    // Versión de la plantilla. Este número servirá para comparar los cambios que se hayan hecho con los reportes
    //  que se hayan generado a partir de esta plantilla
    int templateVersion = 0;

    // Los reportes generados usando esta plantilla
    List<String> reportsChildren;
    List<Category> categories;

    public Template() {
    }

    public Template(
            Timestamp createdAt,
            long usedCount,
            String linkedClientId,
            String address,
            String name,
            String notes,
            String templateId,
            List<String> reportsChildren,
            List<Category> categories,
            String clientName,
            int templateVersion
    ) {
        this.createdAt = createdAt;
        this.usedCount = usedCount;
        this.linkedClientId = linkedClientId;
        this.address = address;
        this.name = name;
        this.notes = notes;
        this.templateId = templateId;
        this.reportsChildren = reportsChildren;
        this.categories = categories;
        this.templateVersion = templateVersion;
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(int templateVersion) {
        this.templateVersion = templateVersion;
    }

    public void addCategory(Category category) {
        if (this.categories == null) {
            this.categories = new ArrayList<>();
        }

        this.categories.add(category);
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public long getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(long usedCount) {
        this.usedCount = usedCount;
    }

    public String getLinkedClientId() {
        return linkedClientId;
    }

    public void setLinkedClientId(String linkedClientId) {
        this.linkedClientId = linkedClientId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<String> getReportsChildren() {
        return reportsChildren;
    }

    public void setReportsChildren(List<String> reportsChildren) {
        this.reportsChildren = reportsChildren;
    }

    public List<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
