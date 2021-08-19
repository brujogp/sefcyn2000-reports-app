package com.sefcyn2000.reports.data.viewmodels.communicators;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class CommunicationIncompleteTemplateSelectedViewModel extends ViewModel {
    private final MutableLiveData<Map<String, String>> templateSelected = new MutableLiveData<>();
    private final MutableLiveData<Boolean> templateDeleted = new MutableLiveData<>();

    public void setTemplateSelected(String clientId, String templateId) {
        Map<String, String> infoSelected = new HashMap<>();

        infoSelected.put(clientId, templateId);

        this.templateSelected.setValue(infoSelected);
    }

    public LiveData<Map<String, String>> getTemplateSelectedObserver() {
        return this.templateSelected;
    }

    public void isDeletedIncompleteTemplate(Boolean isDeleted) {
        this.templateDeleted.setValue(isDeleted);
    }


    public LiveData<Boolean> getIsDeletedIncompleteTemplateObserver() {
        return this.templateDeleted;
    }
}
