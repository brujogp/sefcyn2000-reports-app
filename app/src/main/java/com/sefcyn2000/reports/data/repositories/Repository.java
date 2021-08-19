package com.sefcyn2000.reports.data.repositories;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.sefcyn2000.reports.data.daos.ClientDao;
import com.sefcyn2000.reports.data.daos.ReportsDao;
import com.sefcyn2000.reports.data.daos.TemplateDao;
import com.sefcyn2000.reports.data.daos.ValuesDao;
import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.entities.Report;
import com.sefcyn2000.reports.data.entities.Template;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;

import java.util.List;
import java.util.Map;

public class Repository {
    private ClientDao clientDao;
    private TemplateDao templateDao;
    private ValuesDao valuesDao;
    private ReportsDao reportsDaoInstance;


    public Repository() {
    }

    private ClientDao getClientDaoInstance() {
        if (clientDao == null) {
            clientDao = new ClientDao();
        }
        return clientDao;
    }

    private TemplateDao getTemplateDaoInstance() {
        if (this.templateDao == null) {
            templateDao = new TemplateDao();
        }
        return templateDao;
    }

    private ValuesDao getValuesDaoInstance() {
        if (this.valuesDao == null) {
            this.valuesDao = new ValuesDao();
        }
        return this.valuesDao;
    }

    private ReportsDao getReportsDaoInstance() {
        if (this.reportsDaoInstance == null) {
            this.reportsDaoInstance = new ReportsDao();
        }

        return this.reportsDaoInstance;
    }

    public LiveData<List<Client>> getClients() {
        return getClientDaoInstance().getItems();
    }

    public LiveData<Boolean> saveClient(Client client) {
        return getClientDaoInstance().updateItem(client);
    }

    public LiveData<Map<String, String>> getClientTypes(String values) {
        return getClientDaoInstance().getStringsFromDb(values);
    }

    public LiveData<Boolean> saveTemplate(String typeProgress, Template template, boolean isIncompleteToCompleteTemplate) {
        return getTemplateDaoInstance().saveTemplate(typeProgress, template, isIncompleteToCompleteTemplate);
    }

    public LiveData<List<List<Template>>> getTemplates(String typeProgress) {
        return this.getTemplateDaoInstance().getTemplatesByClients(typeProgress);
    }

    public LiveData<Template> getTemplateById(String typeProgress, String clientId, String templateId) {
        return this.getTemplateDaoInstance().getTemplateById(typeProgress, clientId, templateId);
    }

    public LiveData<Client> getClientById(String clientId) {
        return this.getClientDaoInstance().getItemById(clientId);
    }

    public LiveData<List<SimpleQuestion>> getSimpleListQuestions() {
        return this.getValuesDaoInstance().getSimpleQuestionsList();
    }

    public LiveData<List<String>> getRootCausesList() {
        return this.getValuesDaoInstance().getCatalogList("rootCauses", "causes");
    }

    public LiveData<List<String>> getOpportunitiesAreasList() {
        return this.getValuesDaoInstance().getCatalogList("areaOfOpportunity", "areas");
    }

    public LiveData<List<String>> getActionsForSupplierList() {
        return this.getValuesDaoInstance().getCatalogList("actionsSupplier", "actions");
    }

    public LiveData<List<String>> getActionsForClientList() {
        return this.getValuesDaoInstance().getCatalogList("actionsClient", "actions");
    }

    public LiveData<List<String>> getEquipmentNameList() {
        return this.getValuesDaoInstance().getCatalogList("equipmentsName", "names");
    }

    public LiveData<List<String>> getActionsOnEquipmentsList() {
        return this.getValuesDaoInstance().getCatalogList("actionsOnEquipments", "actions");
    }

    public LiveData<Boolean> saveReport(Report report) {
        return this.getReportsDaoInstance().saveReport(report);
    }

    public LiveData<Boolean> deleteClientById(String clientId) {
        return getClientDaoInstance().deleteClient(clientId);
    }

    public LiveData<Boolean> deleteTemplate(String clientId, String templateId, boolean isCompleted) {
        return getTemplateDaoInstance().deleteTemplate(clientId, templateId, isCompleted);
    }

    public LiveData<Map<Client, List<Report>>> getReportsWithClients() {
        return getReportsDaoInstance().getReportsWithClients();
    }
}
