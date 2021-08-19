package com.sefcyn2000.reports.data.viewmodels.communicators;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sefcyn2000.reports.data.entities.Template;

import java.util.HashMap;
import java.util.Map;

public class CommunicationTemplateSelectedViewModel extends ViewModel {
    private final MutableLiveData<Map<String, String>> templateSelected = new MutableLiveData<>();

    public void setTemplateSelected(String clientId, String templateId) {
        Map<String, String> infoSelected = new HashMap<>();

        infoSelected.put(clientId, templateId);

        this.templateSelected.setValue(infoSelected);
    }

    public LiveData<Map<String, String>> getTemplateSelectedObserver() {
        return this.templateSelected;
    }
}
