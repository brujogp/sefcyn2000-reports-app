package com.sefcyn2000.reports.data.viewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.entities.Report;
import com.sefcyn2000.reports.data.entities.Template;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;
import com.sefcyn2000.reports.data.repositories.Repository;

import java.util.List;
import java.util.Map;

public class DataViewModel extends ViewModel {
    private final Repository repository;

    public DataViewModel() {
        this.repository = new Repository();
    }

    /**
     * <p>Obtinene todos los clientes en una lista</p>
     */
    public LiveData<List<Client>> getClients() {
        return this.repository.getClients();
    }

    /**
     * <p>Crea un nuevo cliente.</p>
     *
     * @param client El nuevo cliente a crear
     */
    public LiveData<Boolean> createClient(Client client) {
        return this.repository.saveClient(client);
    }


    /**
     * <p>Obtiene el listado de tipos de clientes (comercial, residencial, etc).</p>
     *
     * @param values Especifíca el arreglo para el cliente
     */
    public LiveData<Map<String, String>> getClientTypes(String values) {
        return this.repository.getClientTypes(values);
    }

    /**
     * <p>Crea una nueva plantilla</p>
     *
     * @param typeProgress                   El tipo de progreso (si la plantilla está finalizada o no). Esto servirá para almacenar la plantilla en la ruta correcta
     * @param template                       El objeto a persisitir
     * @param isIncompleteToCompleteTemplate Una bandera que indica si la plantilla está pasando de ser incompleta a completa
     */
    public LiveData<Boolean> createTemplate(String typeProgress, Template template, boolean isIncompleteToCompleteTemplate) {
        return this.repository.saveTemplate(typeProgress, template, isIncompleteToCompleteTemplate);
    }

    /**
     * <p>Obtiene todas las plantillas</p>
     *
     * @param typeProgress Especifíca el tipo de progreso, ya que este método obtiene tanto plantillas completas e incompletas
     */
    public LiveData<List<List<Template>>> getTemplates(String typeProgress) {
        return this.repository.getTemplates(typeProgress);
    }

    /**
     * <p>Obtiene una sola plantilla mendiate el ID</p>
     *
     * @param typeProgress Especifíca el tipo de progreso, ya que este método obtiene tanto plantillas completas e incompletas
     * @param templateId   El ID de la plantilla a obtener
     * @param clientId     El ID del cliente en donde está almacenada la plantilla
     */
    public LiveData<Template> getTemplateById(String typeProgress, String clientId, String templateId) {
        return this.repository.getTemplateById(typeProgress, clientId, templateId);
    }

    /**
     * <p>Obtiene un solo cliente a través de su ID</p>
     *
     * @param clientId El ID del cliente a obtener
     */
    public LiveData<Client> getClientById(String clientId) {
        return this.repository.getClientById(clientId);
    }


    /**
     * <p>Obtiene la lista de preguntas</p>
     */
    public LiveData<List<SimpleQuestion>> getSimpleListQuestions() {
        return this.repository.getSimpleListQuestions();
    }


    /**
     * <p>Obtiene el listado de causas raíz</p>
     */
    public LiveData<List<String>> getRootCauses() {
        return this.repository.getRootCausesList();
    }


    /**
     * <p>Obtiene el listado de áreas de oportunidad</p>
     */
    public LiveData<List<String>> getOpportunityAreas() {
        return this.repository.getOpportunitiesAreasList();
    }


    /**
     * <p>Obtiene el listado de las acciones para el proveedor</p>
     */
    public LiveData<List<String>> getActionsForSupplier() {
        return this.repository.getActionsForSupplierList();
    }

    /**
     * <p>Obtiene el listado de las acciones para el cliente</p>
     */
    public LiveData<List<String>> getActionsForClient() {
        return this.repository.getActionsForClientList();
    }

    /**
     * <p>Guarda un reporte</p>
     *
     * @param report El objeto reporte a guardar. Éste ya tiene toda la info. necesaria para ser guardado correctamente
     */
    public LiveData<Boolean> saveReport(Report report) {
        return this.repository.saveReport(report);
    }

    /**
     * <p>Obtiene el listado de tipos de equipos</p>
     */
    public LiveData<List<String>> getEquipmentNameList() {
        return this.repository.getEquipmentNameList();
    }


    /**
     * <p>Obtiene el listado de las posibles acciones que se realizan en los equipos</p>
     */
    public LiveData<List<String>> getActionOnEquipment() {
        return this.repository.getActionsOnEquipmentsList();
    }


    /**
     * <p>Elimína un cliente a través de su ID</p>
     *
     * @param clientId El ID del cliente a eliminar
     */
    public LiveData<Boolean> deleteClientById(String clientId) {
        return repository.deleteClientById(clientId);
    }


    /**
     * <p>Elímina una plantilla. En este momento, solamente elimína plantillas en el área de plantillas en proceso</p>
     *
     * @param clientId    El ID del cliente que contiene la plantilla
     * @param templateId  El ID de la plantilla a eliminar
     * @param isCompleted Una bandera que señala si la plantilla está completa o incompleta
     */
    public LiveData<Boolean> deleteTemplate(String clientId, String templateId, boolean isCompleted) {
        return this.repository.deleteTemplate(clientId, templateId, isCompleted);
    }

    /**
     * <p>Método que recupera todos los reportes y los ordena a partir del cliente</p>
     *
     * @return Retorna un mapa con un objeto cliente como llave y una lista de reportes que pertenecen al cliente como valor
     */
    public LiveData<Map<Client, List<Report>>> getReportsWithClients() {
        return this.repository.getReportsWithClients();
    }
}
