package com.sefcyn2000.reports.data.entities.reportscomponents;

import com.google.firebase.Timestamp;
import com.sefcyn2000.reports.data.entities.templatecomponents.Category;

import java.util.List;

/**
 * POJO que contiene los datos escenciales de una plantilla que fue usada para la creación de un reporte
 * Este tal vez no lo use por practicidad
 */
public class TemplateForReport {
    Timestamp createdAt;
    String address;
    String name;
    String notes;

    // TODO: Supongo que se necesitará un objecto con más campos
    // List<Category> categories;

    /**
     * Crea un nuevo Objecto rellenando todos lods campos
     *
     * @param createdAt  Representa la fecha en la que sera creada esta plantilla
     * @param categories La lista de categorias que componene la plantilla.
     */
    public TemplateForReport(Timestamp createdAt, String address, String name, String notes, List<Category> categories) {
        this.createdAt = createdAt;
        this.address = address;
        this.name = name;
        this.notes = notes;

        // this.categories = categories;
    }

    public TemplateForReport() {
    }
}
