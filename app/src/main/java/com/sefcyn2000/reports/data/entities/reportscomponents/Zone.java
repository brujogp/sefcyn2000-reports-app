package com.sefcyn2000.reports.data.entities.reportscomponents;


import com.sefcyn2000.reports.data.entities.templatecomponents.Section;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    // Nombre de la zona
    String zoneName;

    // Lista de preguntas y respuestas
    List<Questionnaire> questionnairePageList;

    /**
     * <p>Lista de secciones base para la zona actual.</p>
     * <p>Esta lista servirá para construir una lista de secciones para cada una de las hojas de cuestionario.</p>
     */
    List<Section> sectionsName;

    // Bandera que indica si para esa sección ya fueron respondidas todas las preguntas seleccionadas
    boolean totalResolved = false;

    // Lista de equipos para esta zona
    List<Equipment> equipmentList;

    public Zone(String zoneName, List<Questionnaire> questionnairePageList, List<Section> sectionsName, List<Equipment> equipmentList) {
        this.zoneName = zoneName;
        this.questionnairePageList = questionnairePageList;
        this.equipmentList = equipmentList;
        this.sectionsName = sectionsName;
    }


    public Zone(String zoneName) {
        this.zoneName = zoneName;
    }

    public Zone() {
    }

    public List<Equipment> getEquipmentList() {

        return equipmentList;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public List<Questionnaire> getQuestionnairePageList() {
        if (this.questionnairePageList == null) {
            this.questionnairePageList = new ArrayList<>();
        }
        return questionnairePageList;
    }

    public void setQuestionnairePageList(List<Questionnaire> questionnairePageList) {
        this.questionnairePageList = questionnairePageList;
    }

    public List<Section> getSectionsName() {
        return sectionsName;
    }

    public void setSectionsName(List<Section> sectionsName) {
        this.sectionsName = sectionsName;
    }

    public void addSection(Section section) {
        if (this.sectionsName == null) {
            this.sectionsName = new ArrayList<>();
        }

        this.sectionsName.add(section);
    }

    public void addQuestionnairePage(Questionnaire questionnaire) {
        if (this.questionnairePageList == null) {
            this.questionnairePageList = new ArrayList<>();
        }

        this.questionnairePageList.add(questionnaire);
    }

    public boolean isTotalResolved() {
        return totalResolved;
    }

    public void setTotalResolved(boolean totalResolved) {
        this.totalResolved = totalResolved;
    }

    public void addEquipment(Equipment e) {
        if (this.equipmentList == null) {
            equipmentList = new ArrayList<>();
        }

        this.equipmentList.add(e);
    }
}

