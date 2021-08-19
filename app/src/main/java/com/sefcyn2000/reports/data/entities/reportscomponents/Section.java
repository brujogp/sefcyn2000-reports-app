package com.sefcyn2000.reports.data.entities.reportscomponents;

/**
 * <p>POJO usado en los reportes</p>
 */
public class Section {
    String sectionName;

    // Campo que indica si la sección fue marcada para indicar que ensa sección de la zona se respondió afirmativamente a la pregunta
    boolean isMarkedThisSection;

    // URL de la imagen que respalda la respuesta afirmativa a la pregunta
    String urlImage;

    public Section(String sectionName, boolean isMarkedThisSection, String urlImage) {
        this.sectionName = sectionName;
        this.isMarkedThisSection = isMarkedThisSection;
        this.urlImage = urlImage;
    }

    public Section() {
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public boolean isMarkedThisSection() {
        return isMarkedThisSection;
    }

    public void setMarkedThisSection(boolean markedThisSection) {
        this.isMarkedThisSection = markedThisSection;
    }
}
