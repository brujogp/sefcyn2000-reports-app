package com.sefcyn2000.reports.data.viewmodels.communicators;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.entities.Report;
import com.sefcyn2000.reports.data.entities.Template;

public class CommunicationFragmentViewModel extends ViewModel {
    private final MutableLiveData<Client> selectedItem = new MutableLiveData<Client>();
    private final MutableLiveData<Template> templateMutableLiveData = new MutableLiveData<Template>();

    private final MutableLiveData<Report> reportMutableLiveData = new MutableLiveData<>();

    public void selectItem(Client item) {
        selectedItem.setValue(item);
    }

    public LiveData<Client> getSelectedItem() {
        return selectedItem;
    }

    public void setTemplateObject(Template template) {
        this.templateMutableLiveData.setValue(template);
    }

    public LiveData<Template> getTemplateItemObserver() {
        return this.templateMutableLiveData;
    }

    public void setReportLiveData(Report report) {
        this.reportMutableLiveData.setValue(report);
    }

    public LiveData<Report> getReportObserver() {
        return this.reportMutableLiveData;
    }
}
