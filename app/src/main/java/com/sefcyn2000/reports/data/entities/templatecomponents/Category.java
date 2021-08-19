package com.sefcyn2000.reports.data.entities.templatecomponents;

import java.util.ArrayList;
import java.util.List;

public class Category {
    List<Zone> zones;
    String nameCategory;

    public Category(List<Zone> zones, String nameCategory) {
        this.zones = zones;
        this.nameCategory = nameCategory;
    }

    public Category() {
    }

    public Category(String toString) {
        this.nameCategory = toString;
        this.zones = new ArrayList<>();
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public void addZone(Zone zone) {
        if (this.zones != null) {
            this.zones.add(zone);
        }
    }
}
