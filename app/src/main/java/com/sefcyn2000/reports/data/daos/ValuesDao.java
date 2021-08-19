package com.sefcyn2000.reports.data.daos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;

import java.util.ArrayList;
import java.util.List;

public class ValuesDao {

    private final FirebaseFirestore db;

    public ValuesDao() {
        this.db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<SimpleQuestion>> getSimpleQuestionsList() {
        MutableLiveData<List<SimpleQuestion>> simpleListOfQuestionsMutableLiveData = new MutableLiveData<>();


        this.db.collection("values").document("questions").get().addOnCompleteListener(command -> {
            if (command.isSuccessful()) {
                List<SimpleQuestion> simpleQuestionList = new ArrayList<>();

                for (String q : (List<String>) command.getResult().get("questions")) {
                    SimpleQuestion simpleQuestion = new SimpleQuestion();
                    simpleQuestion.setQuestion(q);
                    simpleQuestionList.add(simpleQuestion);
                }

                simpleListOfQuestionsMutableLiveData.setValue(simpleQuestionList);
            }
        });
        return simpleListOfQuestionsMutableLiveData;
    }

    public LiveData<List<String>> getCatalogList(String pathDocument, String nameList) {
        MutableLiveData<List<String>> list = new MutableLiveData<>();

        this.db.collection("values").document(pathDocument).get().addOnSuccessListener(command -> {
            List<String> l = ((List<String>) command.get(nameList));
            list.setValue(l);
        });

        return list;
    }

}
