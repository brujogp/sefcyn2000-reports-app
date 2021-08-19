package com.sefcyn2000.reports.data.daos.helperdaos;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;

public class HelperFirestoreDao {
    private final FirebaseFirestore db;

    public HelperFirestoreDao() {
        this.db = FirebaseFirestore.getInstance();
    }

    public LiveData<Map<String, String>> getStringsMap(String value) {
        MutableLiveData<Map<String, String>> mutableTypeClients = new MutableLiveData<>();
        Map<String, String> listTypesClient = new HashMap<>();

        this.db.collection("values").document(value).get().addOnCompleteListener(command -> {
            if (command.isSuccessful()) {
                for (Map.Entry<String, Object> item : command.getResult().getData().entrySet()) {
                    listTypesClient.put(item.getKey(), item.getValue().toString());
                }
                mutableTypeClients.setValue(listTypesClient);
            }
        });

        return mutableTypeClients;
    }

    public Observable<Boolean> updateValue(String pathToDocument, String documentName, String fieldName, Object data) {
        return Observable.create(emitter -> {
            db.collection(pathToDocument).document(documentName).update(fieldName, data).addOnSuccessListener(command -> {
                emitter.onNext(true);
            });
        });
    }
}
