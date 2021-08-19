package com.sefcyn2000.reports.data.viewmodels.communicators;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sefcyn2000.reports.data.entities.reportscomponents.Questionnaire;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone;

import java.util.HashMap;
import java.util.Map;

public class CommunicationFloatingReportQuestionnaireFragmentsViewModel extends ViewModel {
    private final MutableLiveData<Questionnaire> questionnairePageListFloatMutableLiveData = new MutableLiveData<>();
}
