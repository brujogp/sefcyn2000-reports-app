package com.sefcyn2000.reports.data.entities.templatecomponents;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    List<Section> sections;
    String nameZone;

    public Zone(List<Section> sections, String nameZone) {
        this.sections = sections;
        this.nameZone = nameZone;
    }

    public Zone() {
    }

    public Zone(String toString) {
        this.nameZone = toString;
        this.sections = new ArrayList<>();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public String getNameZone() {
        return nameZone;
    }

    public void setNameZone(String nameZone) {
        this.nameZone = nameZone;
    }

    public void addSection(Section section) {
        if (this.sections != null) {
            this.sections.add(section);
        }
    }
}
