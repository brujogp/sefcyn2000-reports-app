package com.sefcyn2000.reports.data.entities;

import java.util.List;

import com.google.firebase.Timestamp;
import com.sefcyn2000.reports.data.entities.reportscomponents.Category;

/*
 * Se determinará si es o no es una primera,
 * segunda o enesima visita a partir del campo usedCount de la plantilla usada para el reporte
 */
public class Report {
    // Nombre del reporte (Este nombre aparecerá en el listado
    String reportName;

    // Identificador (Este ID se generará a partir de cierta info si es que no es una visita extra)
    String reportId;

    // Número que es modificado a partir del número total de reportes creados +1
    int reportNum;

    // Si es o no es una visita extra
    boolean extraVisit;

    /**
     * La lista de categorías para enlistar. Esta lista será el mapa para el reporte
     * Estas categorías son parte del paquete reportscomponets ya que contienen campos para las preguntas
     * especiales para cada campo
     */
    List<Category> map;

    // Momento en el que se comenzó a redactar el reporte
    Timestamp createdAt;

    // Momento en el que se terminó el reporte
    Timestamp finishedAt;

    // Indentificador del cliente al que se ligó el reporte
    String linkedWithClient;

    // Plantilla que se uso para generar el reporte
    String fromTemplate;

    // Version de la plantilla usada. Esta servirá para comparar la plantilla en dado caso que haya una edición
    int versionTemplatedUsed = 0;

    // El mapa de la plantilla que se usó para generar el reporte
    List<com.sefcyn2000.reports.data.entities.templatecomponents.Category> templateMap;

    String confirmedWith;
    String receivedBy;
    String technicianName;

    String unityName;

    public Report() {
    }

    public Report(
            String reportName,
            int reportNum,
            boolean extraVisit,
            List<Category> map,
            Timestamp createdAt,
            Timestamp finishedAt,
            String linkedWithClient,
            String fromTemplate,
            List<com.sefcyn2000.reports.data.entities.templatecomponents.Category> templateMap,
            String confirmedWith,
            String receivedBy,
            String technicianName,
            String unityName,
            String reportId,
            int versionTemplatedUsed
    ) {
        this.reportName = reportName;
        this.reportNum = reportNum;
        this.extraVisit = extraVisit;
        this.map = map;
        this.reportId = reportId;
        this.createdAt = createdAt;
        this.finishedAt = finishedAt;
        this.linkedWithClient = linkedWithClient;
        this.fromTemplate = fromTemplate;
        this.templateMap = templateMap;
        this.confirmedWith = confirmedWith;
        this.receivedBy = receivedBy;
        this.technicianName = technicianName;
        this.versionTemplatedUsed = versionTemplatedUsed;
        this.unityName = unityName;
    }

    public List<com.sefcyn2000.reports.data.entities.templatecomponents.Category> getTemplateMap() {
        return templateMap;
    }

    public void setTemplateMap(List<com.sefcyn2000.reports.data.entities.templatecomponents.Category> templateMap) {
        this.templateMap = templateMap;
    }

    public int getVersionTemplatedUsed() {
        return versionTemplatedUsed;
    }

    public void setVersionTemplatedUsed(int versionTemplatedUsed) {
        this.versionTemplatedUsed = versionTemplatedUsed;
    }

    public String getUnityName() {
        return unityName;
    }

    public void setUnityName(String unityName) {
        this.unityName = unityName;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public int getReportNum() {
        return reportNum;
    }

    public void setReportNum(int reportNum) {
        this.reportNum = reportNum;
    }

    public boolean isExtraVisit() {
        return extraVisit;
    }

    public void setExtraVisit(boolean extraVisit) {
        this.extraVisit = extraVisit;
    }

    public List<Category> getMap() {
        return map;
    }

    public void setMap(List<Category> map) {
        this.map = map;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Timestamp finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getLinkedWithClient() {
        return linkedWithClient;
    }

    public void setLinkedWithClient(String linkedWithClient) {
        this.linkedWithClient = linkedWithClient;
    }

    public String getFromTemplate() {
        return fromTemplate;
    }

    public void setFromTemplate(String fromTemplate) {
        this.fromTemplate = fromTemplate;
    }

    public String getConfirmedWith() {
        return confirmedWith;
    }

    public void setConfirmedWith(String confirmedWith) {
        this.confirmedWith = confirmedWith;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
}
