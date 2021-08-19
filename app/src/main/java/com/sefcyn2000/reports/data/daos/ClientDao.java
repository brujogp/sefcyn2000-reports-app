package com.sefcyn2000.reports.data.daos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sefcyn2000.reports.data.daos.helperdaos.HelperFirestoreDao;
import com.sefcyn2000.reports.data.daos.helperdaos.HelperStorageDao;
import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.utilities.constans.Enums;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ClientDao {
    private final FirebaseFirestore db;
    private final HelperStorageDao storageDb;
    private final HelperFirestoreDao helperFirestore;

    public ClientDao() {
        this.db = FirebaseFirestore.getInstance();
        this.storageDb = new HelperStorageDao();
        this.helperFirestore = new HelperFirestoreDao();
    }

    public LiveData<Map<String, String>> getStringsFromDb(String value) {
        return this.helperFirestore.getStringsMap(value);
    }

    public LiveData<Client> getItemById(String id) {
        MutableLiveData<Client> clientMutableLiveData = new MutableLiveData<>();

        this.db.collection("clients").document(id).get().addOnCompleteListener(response -> {
            if (response.isSuccessful()) {
                Client client = response.getResult().toObject(Client.class);
                clientMutableLiveData.setValue(client);
            }
        });

        return clientMutableLiveData;
    }

    public LiveData<List<Client>> getItems() {
        MutableLiveData<List<Client>> clientsLiveData = new MutableLiveData<>();
        List<Client> clientList = new ArrayList<>();

        CollectionReference clients = this.db.collection("clients");
        clients.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    if (document != null) {
                        clientList.add(
                                new Client(
                                        document.get("notes").toString(),
                                        document.get("phone").toString(),
                                        new Timestamp(new Date()),
                                        document.getId(),
                                        document.get("name").toString(),
                                        document.get("clientType").toString(),
                                        document.get("clientImageUrl").toString(),
                                        ((Long) document.get("visitorCounter")).intValue(),
                                        ((Long) document.get("templatesCounter")).intValue(),
                                        ((Long) document.get("reportsCounter")).intValue()

                                ));
                    }

                }
                clientsLiveData.setValue(clientList);
            } else if (task.isCanceled()) {
                Log.d("FIRESTORE", "Cancelled");
            }
        });
        return clientsLiveData;
    }

    public LiveData<Boolean> updateItem(Client item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference clients = db.collection("clients");

        MutableLiveData<Boolean> isItemSavedLiveData = new MutableLiveData<>();

        String path = "clients/" + item.getCodeClient() + "/images/profile";

        storageDb.uploadImage(path, item.getClientImageUrl())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Uri>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(Uri uri) {
                        if (uri != null) {
                            item.setClientImageUrl(uri.toString());
                        }

                        clients.document(item.getCodeClient()).set(item).addOnSuccessListener(command -> {
                            isItemSavedLiveData.postValue(true);
                        });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return isItemSavedLiveData;
    }

    public LiveData<Boolean> deleteClient(String clientId) {
        MutableLiveData<Boolean> isDeletedClient = new MutableLiveData<>();
        CollectionReference collectionReference = this.db.collection("clients");

        String path = "/clients/" + clientId + "/images/profile";
        path = path.trim();

        Log.d("TEST-T", path);

        this.storageDb.deleteDirectory(path)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(aBoolean -> {

                    collectionReference.document(clientId).delete().onSuccessTask(unused -> {
                        isDeletedClient.postValue(true);

                        return null;
                    });

                });


        return isDeletedClient;
    }
}
