package com.sefcyn2000.reports.data.daos;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sefcyn2000.reports.data.daos.helperdaos.HelperFirestoreDao;
import com.sefcyn2000.reports.data.daos.helperdaos.HelperStorageDao;
import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.entities.Report;
import com.sefcyn2000.reports.data.entities.reportscomponents.Category;
import com.sefcyn2000.reports.data.entities.reportscomponents.Equipment;
import com.sefcyn2000.reports.data.entities.reportscomponents.Questionnaire;
import com.sefcyn2000.reports.data.entities.reportscomponents.Section;
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ReportsDao {

    private final FirebaseFirestore db;
    private final HelperFirestoreDao helperFirestore;
    private final HelperStorageDao helperStorage;

    private volatile int totalImages = 0;

    private final MutableLiveData<Boolean> isReportSaved = new MutableLiveData<>();
    private Report report;

    public ReportsDao() {
        this.db = FirebaseFirestore.getInstance();
        this.helperStorage = new HelperStorageDao();
        this.helperFirestore = new HelperFirestoreDao();
    }

    public LiveData<Boolean> saveReport(Report report) {
        this.report = report;
        this.report.setReportId(getReportName(report));
        getTotalImages();

        for (Category c : report.getMap()) {
            for (Zone z : c.getZones()) {
                for (Questionnaire questionnaire : z.getQuestionnairePageList()) {
                    if (questionnaire.getAnswerQuestionPhotoUrl() != null && !(questionnaire.getAnswerQuestionPhotoUrl().isEmpty())) {


                        this.helperStorage.getObservable(
                                this.report.getLinkedWithClient(),
                                this.report.getReportId(),
                                getImageName(),
                                Uri.parse(questionnaire.getAnswerQuestionPhotoUrl())
                        )
                                .observeOn(Schedulers.io())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Observer<Uri>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@NonNull Uri uri) {
                                        questionnaire.setAnswerQuestionPhotoUrl(uri.toString());
                                        totalImages--;

                                        onCompleteImagesUpload();
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }

                    if (questionnaire.getRootCausePhotoUrl() != null && !(questionnaire.getRootCausePhotoUrl().isEmpty())) {
                        this.helperStorage.getObservable(
                                this.report.getLinkedWithClient(),
                                this.report.getReportId(),
                                getImageName(),
                                Uri.parse(questionnaire.getRootCausePhotoUrl())
                        )
                                .observeOn(Schedulers.io())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Observer<Uri>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@NonNull Uri uri) {
                                        questionnaire.setRootCausePhotoUrl(uri.toString());
                                        totalImages--;
                                        onCompleteImagesUpload();
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }

                    if (questionnaire.getOpportunityAreaPhotoUrl() != null && !(questionnaire.getOpportunityAreaPhotoUrl().isEmpty())) {

                        this.helperStorage.getObservable(
                                this.report.getLinkedWithClient(),
                                this.report.getReportId(),
                                getImageName(),
                                Uri.parse(questionnaire.getOpportunityAreaPhotoUrl())
                        )
                                .observeOn(Schedulers.io())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Observer<Uri>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@NonNull Uri uri) {
                                        questionnaire.setOpportunityAreaPhotoUrl(uri.toString());
                                        totalImages--;
                                        onCompleteImagesUpload();
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }

                    if (questionnaire.getMarkedSectionsList() != null) {
                        for (Section section : questionnaire.getMarkedSectionsList()) {
                            if (section.getUrlImage() != null && !(section.getUrlImage().isEmpty())) {

                                this.helperStorage.getObservable(
                                        this.report.getLinkedWithClient(),
                                        this.report.getReportId(),
                                        getImageName(),
                                        Uri.parse(section.getUrlImage())
                                )
                                        .observeOn(Schedulers.io())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new Observer<Uri>() {
                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onNext(@NonNull Uri uri) {
                                                section.setUrlImage(uri.toString());
                                                totalImages--;
                                                onCompleteImagesUpload();
                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {

                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });

                            }
                        }
                    }
                }

                // Aquí subiría las fotos de los equipos
                if (z.getEquipmentList() != null && z.getEquipmentList().size() > 0) {
                    for (Equipment e : z.getEquipmentList()) {

                        if (!e.getUrlImageEquipment().isEmpty()) {

                            this.helperStorage.getObservable(
                                    this.report.getLinkedWithClient(),
                                    this.report.getReportId(),
                                    getImageName(),
                                    Uri.parse(e.getUrlImageEquipment())
                            )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .subscribe(new Observer<Uri>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onNext(@NonNull Uri uri) {

                                            e.setUrlImageEquipment(uri.toString());
                                            totalImages--;
                                            onCompleteImagesUpload();
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }
                    }
                }
            }
        }

        return isReportSaved;
    }


    private String getImageName() {
        String name = "photo-" + Math.random() * 10000;
        name = name.replaceAll("\\.", "");

        return name;
    }

    private void getTotalImages() {
        for (Category c : report.getMap()) {
            for (Zone z : c.getZones()) {
                for (Questionnaire questionnaire : z.getQuestionnairePageList()) {
                    if (questionnaire.getAnswerQuestionPhotoUrl() != null) {
                        totalImages++;
                    }

                    if (questionnaire.getRootCausePhotoUrl() != null) {
                        totalImages++;
                    }

                    if (questionnaire.getOpportunityAreaPhotoUrl() != null) {
                        totalImages++;
                    }

                    if (questionnaire.getMarkedSectionsList() != null) {
                        for (Section section : questionnaire.getMarkedSectionsList()) {
                            if (section.getUrlImage() != null) {
                                totalImages++;
                            }
                        }
                    }
                }

                if (z.getEquipmentList() != null && z.getEquipmentList().size() > 0) {
                    for (Equipment e : z.getEquipmentList()) {
                        if (!e.getUrlImageEquipment().isEmpty()) {
                            totalImages++;
                        }
                    }
                }


            }
        }
    }

    private String getReportName(Report report) {
        String name;

        // Si es una visita extra, simplemente genera un número aleatorio
        if (report.isExtraVisit()) {
            name = "extra-" + Math.random() * 10000;
            name = name.replaceAll("\\.", "");
        } else {
            // Si es una visita dentro de una póliza,
            //  obtiene el número del reporte y lo concatena con un indicador que muestra si es la primera o segunda visita
            //  y después concatena la fecha
            int n = report.getReportNum();

            if ((n % 2) == 0) {
                name = n + "-0";
            } else {
                name = n + "-1";
            }
        }

        return name;
    }

    // Método gatillado cuando la subida de imágenes del reporte completo se ha termianado
    public void onCompleteImagesUpload() {
        if (totalImages == 0) {
            this.db.collection("clients")
                    .document(report.getLinkedWithClient() + "/reports/" + this.report.getReportId())
                    .set(report)
                    .addOnSuccessListener(command -> {
                        helperFirestore.updateValue(
                                "clients",
                                report.getLinkedWithClient(),
                                "reportsCounter",
                                (report.getReportNum() + 1)
                        )
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(new Observer<Boolean>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@NonNull Boolean aBoolean) {
                                        if (aBoolean) {
                                            isReportSaved.postValue(true);
                                        }
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    });
        }

    }

    public LiveData<Map<Client, List<Report>>> getReportsWithClients() {
        MutableLiveData<Map<Client, List<Report>>> mutableLiveData = new MutableLiveData<>();

        Map<Client, List<Report>> clientReportMap = new HashMap<>();

        this.db.collection("clients").get().addOnCompleteListener(getAllClientsTask -> {
            if (getAllClientsTask.isSuccessful()) {

                for (QueryDocumentSnapshot client : getAllClientsTask.getResult()) {
                    Client objClient = client.toObject(Client.class);

                    if (objClient.getReportsCounter() > 0) {

                        this.db.collection("clients")
                                .document(objClient.getCodeClient())
                                .collection("reports")
                                .get().addOnCompleteListener(getReportsTask -> {

                            if (getReportsTask.isSuccessful()) {

                                List<Report> reports = new ArrayList<>();

                                for (QueryDocumentSnapshot report : getReportsTask.getResult()) {
                                    if (report != null) {
                                        Report objReport = report.toObject(Report.class);
                                        reports.add(objReport);
                                    }
                                }

                                clientReportMap.put(objClient, reports);

                                mutableLiveData.postValue(clientReportMap);
                            }
                        });

                    }

                }
            }
        });

        return mutableLiveData;
    }
}
