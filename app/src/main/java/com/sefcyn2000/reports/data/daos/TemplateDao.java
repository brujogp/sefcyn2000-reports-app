package com.sefcyn2000.reports.data.daos;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.entities.Template;
import com.sefcyn2000.reports.utilities.constans.PathTemplatesEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TemplateDao {
    private final FirebaseFirestore dbFirestore;
    Long templatesCounter;

    public TemplateDao() {
        this.dbFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Boolean> saveTemplate(String typeProgress, Template item, boolean isIncompleteToCompleteTemplate) {
        MutableLiveData<Boolean> booleanMutableLiveData = new MutableLiveData<>();

        // Si la plantilla está finalizada actualizo el contador de plantillas terminadas
        // Aquí actualizo el contador de plantillas completas solo si la plantilla ya esta finalizada y si no ha sido registrada anteriormente
        if (
                (
                        typeProgress.equals(PathTemplatesEnum.COMPLETES_TEMPLATES.getPath())
                                && item.getTemplateId() == null
                )
                        ||
                        (
                                typeProgress.equals(PathTemplatesEnum.COMPLETES_TEMPLATES.getPath())
                                        && isIncompleteToCompleteTemplate
                        )
        ) {
            this.dbFirestore.collection("clients").document(item.getLinkedClientId()).get().addOnSuccessListener(runnable -> {
                templatesCounter = runnable.getLong("templatesCounter");

                // Actualizo el contador de plantillas
                this.dbFirestore.collection("clients").document(item.getLinkedClientId())
                        .update("templatesCounter", templatesCounter + 1L).addOnCompleteListener(runnable3 -> {
                });

            });
        }


        // Si la plantilla ya tiene identificador, significa que fue recuperada
        // Aqui caera si la plantilla ya tiene identificador
        if (
            // Comprobacion innecesaria
                (
                        typeProgress.equals(PathTemplatesEnum.COMPLETES_TEMPLATES.getPath()) ||
                                typeProgress.equals(PathTemplatesEnum.INCOMPLETE_TEMPLATES.getPath())
                )
                        && item.getTemplateId() != null && !item.getTemplateId().isEmpty()
        ) {
            // Debido a que se está modificando la plantilla, actualizo la versión de la plantilla
            item.setTemplateVersion(item.getTemplateVersion() + 1);

            this.dbFirestore.collection("clients").document(item.getLinkedClientId())
                    .collection(typeProgress).document(item.getTemplateId()).set(item).addOnSuccessListener(command -> {

                // Si la plantilla está siendo tomada y guardada desde la sección de plantillas incompletas se eliminará
                if (isIncompleteToCompleteTemplate) {
                    this.dbFirestore
                            .collection("clients")
                            .document(
                                    item.getLinkedClientId() + "/" +
                                            PathTemplatesEnum.INCOMPLETE_TEMPLATES.getPath() + "/" +
                                            item.getTemplateId()
                            )
                            .delete()
                            .addOnSuccessListener(command1 -> {
                                // Después de que se creó o actualizó la plantilla, confirmo dicha acción
                                booleanMutableLiveData.setValue(true);
                            });


                } else {
                    // Después de que se creó o actualizó la plantilla, confirmo dicha acción
                    booleanMutableLiveData.setValue(true);
                }

            });
        } else {

            this.dbFirestore.collection("clients").document(item.getLinkedClientId())
                    .collection(typeProgress).add(item).addOnSuccessListener(command -> {
                String templateId = command.getId();
                String path = item.getLinkedClientId() + "/" + typeProgress + "/" + templateId;

                // Se se actualiza el campo "templateId" de la nueva plantilla
                this.dbFirestore
                        .collection("clients")
                        .document(path)
                        .update("templateId", templateId).addOnCompleteListener(runnable2 -> {

                    // Después de que se creó o actualizó la plantilla, confirmo dicha acción
                    booleanMutableLiveData.setValue(true);

                });

            });
        }

        return booleanMutableLiveData;
    }

    // Obtengo las plantillas de cada uno de los clientes para las pantallas que enlistan dichas plantillas
    public LiveData<List<List<Template>>> getTemplatesByClients(String typeProgress) {
        MutableLiveData<List<List<Template>>> listMutableLiveData = new MutableLiveData<>();

        List<List<Template>> templates = new ArrayList<>();
        List<String> clientsId = new ArrayList<>();
        List<Client> clients = new ArrayList<>();

        // Obtengo todos los clientes y relleno una lista con sus ID's
        this.dbFirestore.collection("clients").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    if (snapshot != null) {
                        clientsId.add(snapshot.getId());
                        clients.add(snapshot.toObject(Client.class));
                    }
                }

                for (int i = 0; clientsId.size() > i; i++) {
                    if (clients.get(i).getTemplatesCounter() > 0) {
                        this.dbFirestore.collection("clients/" +
                                clientsId.get(i) + "/" + typeProgress).get().addOnSuccessListener(runnable -> {

                            // Arreglo de plantillas
                            List<Template> templatesItemList = new ArrayList<>();

                            for (DocumentSnapshot template : runnable.getDocuments()) {
                                Template toObject = template.toObject(Template.class);
                                templatesItemList.add(toObject);
                            }

                            templates.add(templatesItemList);

                            listMutableLiveData.setValue(templates);

                        });
                    } else if (typeProgress.equals(PathTemplatesEnum.INCOMPLETE_TEMPLATES.getPath())) {
                        this.dbFirestore.collection("clients/" +
                                clientsId.get(i) + "/" + typeProgress).get().addOnSuccessListener(runnable -> {

                            // Arreglo de plantillas
                            List<Template> templatesItemList = new ArrayList<>();

                            for (DocumentSnapshot template : runnable.getDocuments()) {
                                Template toObject = template.toObject(Template.class);
                                templatesItemList.add(toObject);
                            }

                            templates.add(templatesItemList);


                            listMutableLiveData.setValue(templates);

                        });

                    }
                }
            }
        });

        return listMutableLiveData;
    }

    public LiveData<Template> getTemplateById(String typeProgress, String clientId, String templateId) {
        String path = clientId + "/" + typeProgress + "/" + templateId;
        MutableLiveData<Template> templateMutableLiveData = new MutableLiveData<>();


        this.dbFirestore.collection("clients").document(path).get().addOnSuccessListener(command -> {
            Template t = command.toObject(Template.class);

            if (t != null) {
                templateMutableLiveData.setValue(t);
            }
        });

        return templateMutableLiveData;
    }

    public LiveData<Boolean> deleteTemplate(String clientId, String templateId, boolean isCompleted) {
        MutableLiveData<Boolean> isDeletedMutableLiveData = new MutableLiveData<>();
        String path = clientId
                .concat("/")
                .concat(isCompleted ? PathTemplatesEnum.COMPLETES_TEMPLATES.getPath() : PathTemplatesEnum.INCOMPLETE_TEMPLATES.getPath())
                .concat("/")
                .concat(templateId);

        this.dbFirestore.collection("clients").document(path).delete().addOnSuccessListener(command -> {
            isDeletedMutableLiveData.setValue(true);
        });


        return isDeletedMutableLiveData;
    }
}
