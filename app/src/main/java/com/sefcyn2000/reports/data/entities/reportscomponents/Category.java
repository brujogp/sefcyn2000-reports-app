package com.sefcyn2000.reports.data.entities.reportscomponents;

import java.util.ArrayList;
import java.util.List;

public class Category {
    // Nombre de la categoría
    String categoryName;

    // Total de zonas para la categoría
    int totalZones;

    // El porcentaje resuelto de de las zonas de la categoría
    int percentageResolved;

    // Observaciones para la categoría
    String observations;

    // Lista de zonas las cuales contienen info. sobre las preguntas que se han hecho sobre cada una de las zonas
    List<Zone> zones;


    public Category(String categoryName, int totalZones, int percentageResolved, String observations, List<Zone> zones) {
        this.categoryName = categoryName;
        this.totalZones = totalZones;
        this.percentageResolved = percentageResolved;
        this.observations = observations;
        this.zones = zones;
    }

    public Category(String categoryName, int totalZones, int percentageResolved, String observations) {
        this.categoryName = categoryName;
        this.totalZones = totalZones;
        this.percentageResolved = percentageResolved;
        this.observations = observations;
    }

    public Category() {
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTotalZones() {
        return totalZones;
    }

    public void setTotalZones(int totalZones) {
        this.totalZones = totalZones;
    }

    public int getPercentageResolved() {
        return percentageResolved;
    }

    public void setPercentageResolved(int percentageResolved) {
        this.percentageResolved = percentageResolved;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public void addZone(Zone zone) {
        if (this.zones == null) {
            this.zones = new ArrayList<>();
        }

        this.zones.add(zone);
    }
}
