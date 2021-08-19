package com.sefcyn2000.reports.data.daos.helperdaos;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sefcyn2000.reports.data.entities.Report;
import com.sefcyn2000.reports.data.entities.reportscomponents.Category;
import com.sefcyn2000.reports.data.entities.reportscomponents.Questionnaire;
import com.sefcyn2000.reports.data.entities.reportscomponents.Section;
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HelperStorageDao {
    private final FirebaseStorage storeInstance;

    public HelperStorageDao() {
        this.storeInstance = FirebaseStorage.getInstance();
    }

    /**
     * <p>Método para subir imagenes del reporte</p>
     *
     * @param clientId El ID del cliente. Éste servirá para crear el primer tramo del path en Storage
     * @param reportId El ID del reporte, el cual servirá para crear el segundo tramo del path
     */
    public Observable<Uri> getObservable(String clientId, String reportId, String imgName, Uri imgUri) {
        return Observable.create(emitter -> {

            StorageReference storageReference = storeInstance.getReference();

            // Construyo la referencia a la imagen que será creada
            StorageReference pathOnStorage = storageReference.child(
                    "clients/" + clientId + "/images/reports/" + reportId + "/" + imgName
            );

            // Asigno el archivo que será subido
            UploadTask uploadTask = pathOnStorage.putFile(imgUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Recupero la URL generada para la imágen y la paso al flujo
                return pathOnStorage.getDownloadUrl();
            }).addOnSuccessListener(emitter::onNext);
        });
    }

    public Observable<Uri> uploadImage(String fullPath, String uriImage) {
        return Observable.create(emitter -> {
            StorageReference storageReference = storeInstance.getReference().child(fullPath);

            UploadTask uploadTask = storageReference.putFile(Uri.parse(uriImage));

            uploadTask.continueWithTask(task -> storageReference.getDownloadUrl()).addOnCompleteListener(task -> {
                if (task.getResult() == null) {
                    emitter.onNext(null);
                } else {
                    emitter.onNext(task.getResult());
                }
            });
        });
    }

    public Observable<Boolean> deleteDirectory(String path) {
        return Observable.create(emitter -> {
            this.storeInstance.getReference()
                    .child(path)
                    .delete()
                    .addOnSuccessListener(
                            unused -> emitter.onNext(true)
                    );
        });
    }
}

