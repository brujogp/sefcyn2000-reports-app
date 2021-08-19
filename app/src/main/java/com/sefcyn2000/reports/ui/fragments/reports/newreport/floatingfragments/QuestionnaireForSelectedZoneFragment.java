package com.sefcyn2000.reports.ui.fragments.reports.newreport.floatingfragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.reportscomponents.Questionnaire;
import com.sefcyn2000.reports.data.entities.reportscomponents.Section;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone;
import com.sefcyn2000.reports.data.viewmodels.DataViewModel;
import com.sefcyn2000.reports.ui.adapters.reports.SectionsListAdapter;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que muestra una hoja de cuestionario.
 * Este fragment está alojado en el FragmentDialgo flotante
 */
public class QuestionnaireForSelectedZoneFragment extends Fragment implements SectionsListAdapter.OnHitTakePhotoButton {

    private final int TAG_TYPE_FOR_URI_SECTION = 100;
    private final int TAG_TYPE_FOR_URI_ANSWER = 1;
    private final int TAG_TYPE_FOR_URI_ROOT_CAUSES = 2;
    private final int TAG_TYPE_FOR_URI_OPPORTUNITY_AREAS = 3;

    private final OnDoneOneQuestionnairePage onDoneOneQuestionnairePage;

    // Pregunta que se contestará en este formulario
    private Questionnaire questionnaire;

    // Contador para los campos que han sido tocados
    private int fieldsFilledCounter = 0;

    private RadioGroup radioGroupAnswer;
    private List<Section> sections;

    private boolean answer;
    private final SimpleQuestion simpleQuestion;

    // Códigos de solicitud de las actividades de la cámara
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private static final int IMAGE_VIEW_HAD_CONTENT = 3;
    private final Zone zoneSelected;

    // Uri para las fotos seleccionadas
    private Uri uriImageAnswer;
    private Uri uriImageRootCause;
    private Uri uriImageOpportunityArea;

    // Images View para la preview de las imágenes seleccionadas
    private ImageView ivForAnswer;
    private ImageView ivForRootCause;
    private ImageView ivForOpportunityArea;

    // Componentes para el muestreo de la lista de secciones
    private SectionsListAdapter sectionsListAdapter;
    private RecyclerView recyclerViewSectionsList;
    private LinearLayout containerSectionsList;


    // Comunicación con la base de datos
    private DataViewModel dataViewModel;

    // TextView para los catálogos
    private AutoCompleteTextView autoCompleteRootCauses;
    private AutoCompleteTextView autoCompleteOpportunitiesAreas;
    private AutoCompleteTextView autoCompleteActionsForClient;
    private AutoCompleteTextView autoCompleteActionsForSupplier;

    // Las observaciones para la zona sobre esa pregunta
    private TextInputEditText tvObservations;

    // Lista de catálogos
    private List<String> listActionsClient;
    private List<String> listActionsSupplier;
    private List<String> listRootCauses;
    private List<String> listOpportunitiesAreas;

    // Botones para gatillar la cámara
    private LinearLayout linearButtonTakeImageAnswer;
    private LinearLayout linearButtonTakeImageCauseRoot;
    private LinearLayout linearButtonTakeImageOpportunityArea;

    // Gatilladores para la actividad de cámara
    private ActivityResultLauncher<Intent> photoActivityOpportunityAreaLauncher;
    private ActivityResultLauncher<Intent> photoActivityRootCauseLauncher;
    private ActivityResultLauncher<Intent> photoActivityAnswerLauncher;
    private ActivityResultLauncher<Intent> intentActivityResultLauncherForSection;

    private MaterialButton buttonSaveQuestionnairePage;
    private int tempIndexSectionSelected;
    private ImageView tempSectionImgPreview;

    public QuestionnaireForSelectedZoneFragment(
            SimpleQuestion simpleQuestion,
            Zone zone,
            OnDoneOneQuestionnairePage onDoneOneQuestionnairePage
    ) {
        this.questionnaire = new Questionnaire();

        // Si la zona tiene secciones, se crear nuevas secciones más complejas a partir de las secciones simples base
        if (zone.getSectionsName() != null && zone.getSectionsName().size() > 0) {
            this.sections = new ArrayList<>();

            for (com.sefcyn2000.reports.data.entities.templatecomponents.Section simpleSection : zone.getSectionsName()) {

                Section complexSection = new Section();
                complexSection.setSectionName(simpleSection.getNameSection());

                this.sections.add(complexSection);

            }
        }

        this.zoneSelected = zone;
        this.simpleQuestion = simpleQuestion;
        this.onDoneOneQuestionnairePage = onDoneOneQuestionnairePage;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // ().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // container..getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return inflater.inflate(R.layout.layout_report_fragment_floating_questionnaire_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // Creo una instancia del View Model Provider para obtener dos viewmodel
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);

        this.dataViewModel = viewModelProvider.get(DataViewModel.class);

        this.radioGroupAnswer = view.findViewById(R.id.radioGroupAnswer);
        this.containerSectionsList = view.findViewById(R.id.linear_layout_elements_list_sections);

        this.buttonSaveQuestionnairePage = view.findViewById(R.id.button_save_questionnaire_page);

        this.buttonSaveQuestionnairePage.setOnClickListener(v -> {
            assignValues();
        });

        // Asigno las vistas de los catálogos a sus respectivos objetos
        this.autoCompleteRootCauses = view.findViewById(R.id.autocomplete_root_causes_list);
        this.autoCompleteOpportunitiesAreas = view.findViewById(R.id.autocomplete_areas_opportunities);
        this.autoCompleteActionsForClient = view.findViewById(R.id.autocomplete_actions_for_client);
        this.autoCompleteActionsForSupplier = view.findViewById(R.id.autocomplete_actions_for_supplier);

        // Asigno el cuadro para las observaciones a su respectivo objeto
        this.tvObservations = view.findViewById(R.id.tv_observations);

        this.linearButtonTakeImageAnswer = view.findViewById(R.id.btn_take_image_answer);
        this.linearButtonTakeImageCauseRoot = view.findViewById(R.id.btn_take_image_root_cause);
        this.linearButtonTakeImageOpportunityArea = view.findViewById(R.id.btn_take_image_opportunity_area);

        // Asigno los images view que servirán como un cuadro de preview para cada una de las fotografías
        this.ivForAnswer = view.findViewById(R.id.iv_for_answer);
        this.ivForRootCause = view.findViewById(R.id.iv_for_root_cause);
        this.ivForOpportunityArea = view.findViewById(R.id.iv_for_opportunity_area);

        // Asigno los contratos a los launches para que en cada uno de los eventos, los botones puedan lanzar cada uno de los contratos
        this.photoActivityAnswerLauncher = getActivityResultLauncherByTakePhoto(this.ivForAnswer, TAG_TYPE_FOR_URI_ANSWER);
        this.photoActivityRootCauseLauncher = getActivityResultLauncherByTakePhoto(this.ivForRootCause, TAG_TYPE_FOR_URI_ROOT_CAUSES);
        this.photoActivityOpportunityAreaLauncher = getActivityResultLauncherByTakePhoto(this.ivForOpportunityArea, TAG_TYPE_FOR_URI_OPPORTUNITY_AREAS);
        this.intentActivityResultLauncherForSection = getActivityResultLauncherByTakePhoto(this.tempSectionImgPreview, TAG_TYPE_FOR_URI_SECTION);

        // Avento que configura los callback de cada uno de los botones para seleccionar fotos
        setterTakeImagesEventClick();

        getCatalogs();
        setterEventsClickAutoComplete();

        if (this.radioGroupAnswer.getCheckedRadioButtonId() == R.id.radio_button_2) {
            this.containerSectionsList.setVisibility(View.GONE);

        }

        // Evento para los radioButtons
        this.radioGroupAnswer.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_button_2) {
                this.containerSectionsList.setVisibility(View.GONE);

                this.linearButtonTakeImageAnswer.setVisibility(View.VISIBLE);
                this.linearButtonTakeImageCauseRoot.setBackground(getResources().getDrawable(R.drawable.shape_button_take_photo));
                this.linearButtonTakeImageOpportunityArea.setBackground(getResources().getDrawable(R.drawable.shape_button_take_photo));

                this.answer = false;
            } else {
                this.answer = true;

                // Se muestra el listado de secciones (el contenedor y el contenido) solo si la lista de secciones es mayor a 0
                if (this.sections != null && this.sections.size() > 0) {


                    this.recyclerViewSectionsList = view.findViewById(R.id.recycler_view_sections_list);
                    this.sectionsListAdapter = new SectionsListAdapter(
                            getActivity(),
                            this.sections,
                            this
                    );

                    this.recyclerViewSectionsList.setAdapter(this.sectionsListAdapter);
                    this.recyclerViewSectionsList.setLayoutManager(new LinearLayoutManager(getActivity()));

                    this.containerSectionsList.setVisibility(View.VISIBLE);

                    this.linearButtonTakeImageAnswer.setVisibility(View.GONE);
                    this.linearButtonTakeImageCauseRoot.setBackground(getResources().getDrawable(R.drawable.shape_button_optional_take_photo));
                    this.linearButtonTakeImageOpportunityArea.setBackground(getResources().getDrawable(R.drawable.shape_button_optional_take_photo));
                }
            }
        });
    }

    private void setterTakeImagesEventClick() {
        this.linearButtonTakeImageAnswer.setOnClickListener(v -> {
            takePhoto(v.getId());
        });

        this.linearButtonTakeImageCauseRoot.setOnClickListener(v -> {
            takePhoto(v.getId());
        });

        this.linearButtonTakeImageOpportunityArea.setOnClickListener(v -> {
            takePhoto(v.getId());
        });
    }

    private void setterEventsClickAutoComplete() {
        this.autoCompleteRootCauses.setOnClickListener(v -> {
            this.fieldsFilledCounter++;

            this.dataViewModel.getRootCauses().observe(getActivity(), list -> {

                this.autoCompleteRootCauses.setAdapter(new ArrayAdapter<>(
                        getActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        list
                ));
            });

            checkResponsesAndSave();
        });

        this.autoCompleteOpportunitiesAreas.setOnClickListener(v -> {
            this.fieldsFilledCounter++;

            this.dataViewModel.getOpportunityAreas().observe(getActivity(), list -> {

                this.autoCompleteOpportunitiesAreas.setAdapter(new ArrayAdapter<>(
                        getActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        list
                ));
            });

            checkResponsesAndSave();
        });

        this.autoCompleteActionsForClient.setOnClickListener(v -> {
            this.fieldsFilledCounter++;

            this.dataViewModel.getActionsForClient().observe(getActivity(), list -> {

                this.autoCompleteActionsForClient.setAdapter(new ArrayAdapter<>(
                        getActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        list
                ));
            });

            checkResponsesAndSave();
        });

        this.autoCompleteActionsForSupplier.setOnClickListener(v -> {
            this.fieldsFilledCounter++;

            this.dataViewModel.getActionsForSupplier().observe(getActivity(), list -> {

                this.autoCompleteActionsForSupplier.setAdapter(new ArrayAdapter<>(
                        getActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        list
                ));
            });

            checkResponsesAndSave();
        });
    }

    // Descargo todos los catálogos  desde la base de datos y relaciono los datos con los campos que mostrarán sus respectivos catálogos
    // Este método solo lo llamo una vez para inicializar los campos
    void getCatalogs() {
        this.dataViewModel.getActionsForClient().observe(getActivity(), list -> {
            this.listActionsClient = list;

            this.autoCompleteActionsForClient.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    list
            ));
        });

        this.dataViewModel.getActionsForSupplier().observe(getActivity(), list -> {
            this.listActionsSupplier = list;

            this.autoCompleteActionsForSupplier.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    list
            ));
        });

        this.dataViewModel.getRootCauses().observe(getActivity(), list -> {
            this.listRootCauses = list;

            this.autoCompleteRootCauses.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    list
            ));
        });

        this.dataViewModel.getOpportunityAreas().observe(getActivity(), list -> {
            this.listOpportunitiesAreas = list;

            this.autoCompleteOpportunitiesAreas.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    list
            ));
        });
    }

    private void takePhoto(int codeButton) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            String[] arrayPermissions = new String[2];

            arrayPermissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            arrayPermissions[1] = Manifest.permission.CAMERA;

            getActivity().requestPermissions(arrayPermissions, REQUEST_CAMERA_PERMISSION);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent(codeButton);
            }

        }

        dispatchTakePictureIntent(codeButton);
    }

    private Uri dispatchTakePictureIntent(int codeButton/*, ImageView imgPreview*/) {
        Intent takePictureIntent = CropImage
                .activity()
                .setAspectRatio(1, 1)
                .setActivityTitle("Enfoque lo importante")
                .getIntent(getContext());

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            if (codeButton == R.id.btn_take_image_answer) {
                this.photoActivityAnswerLauncher.launch(takePictureIntent);
            } else if (codeButton == R.id.btn_take_image_root_cause) {
                this.photoActivityRootCauseLauncher.launch(takePictureIntent);
            } else if (codeButton == R.id.btn_take_image_opportunity_area) {
                this.photoActivityOpportunityAreaLauncher.launch(takePictureIntent);
            } else if (codeButton == TAG_TYPE_FOR_URI_SECTION) {
                this.intentActivityResultLauncherForSection.launch(takePictureIntent);
            }
        }

        return null;
    }


    private ActivityResultLauncher<Intent> getActivityResultLauncherByTakePhoto(ImageView ivForPreview, final int TAG_URL_FIELD) {
        ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            CropImage.ActivityResult crop = CropImage.getActivityResult(result.getData());

                            if (ivForPreview != null) {
                                ivForPreview.setVisibility(View.VISIBLE);
                                ivForPreview.setImageURI(crop.getUri());
                                ivForPreview.setTag(IMAGE_VIEW_HAD_CONTENT);
                            }

                            this.fieldsFilledCounter++;

                            if (TAG_URL_FIELD == TAG_TYPE_FOR_URI_ANSWER) {
                                this.uriImageAnswer = crop.getUri();
                            } else if (TAG_URL_FIELD == TAG_TYPE_FOR_URI_ROOT_CAUSES) {
                                this.uriImageRootCause = crop.getUri();
                            } else if (TAG_URL_FIELD == TAG_TYPE_FOR_URI_OPPORTUNITY_AREAS) {
                                this.uriImageOpportunityArea = crop.getUri();
                            } else if (TAG_URL_FIELD == TAG_TYPE_FOR_URI_SECTION) {

                                if (crop != null && crop.getUri() != null) {

                                    // Asigno la URI de la foto a la sección seleccionada
                                    Log.d("TEST-T", "Index de la sección: " + this.tempIndexSectionSelected);
                                    this.sections.get(this.tempIndexSectionSelected).setUrlImage(crop.getUri().toString());

                                    this.tempSectionImgPreview.setVisibility(View.VISIBLE);
                                    this.tempSectionImgPreview.setImageURI(crop.getUri());

                                }

                            }

                        }
                    }
                });

        return intentActivityResultLauncher;
    }

    void checkResponsesAndSave() {
        if (this.fieldsFilledCounter >= 7) {
            this.buttonSaveQuestionnairePage.setVisibility(View.VISIBLE);
        }
    }

    // Método que asigna los valores que se han introducido en cada una de las hojas de questionario
    //  (cada vez que se termina de rellenar cada una de las hojas de questionario).
    void assignValues() {
        // Setteo la pregunta
        this.questionnaire.setQuestion(this.simpleQuestion);

        // Obtengo la respuesta
        this.questionnaire.setAffirmativeAnswer(this.answer);

        // Obtengo la URi de la imágen de la respuesta
        if (this.uriImageAnswer != null) {
            this.questionnaire.setAnswerQuestionPhotoUrl(this.uriImageAnswer.toString());
        }

        // Obtengo la URi de la imágen de la causa raíz
        if (this.uriImageRootCause != null) {
            this.questionnaire.setRootCausePhotoUrl(this.uriImageRootCause.toString());
        }

        // Obtengo la URI de la imágen del área de oportunidad
        if (this.uriImageOpportunityArea != null) {
            this.questionnaire.setOpportunityAreaPhotoUrl(this.uriImageOpportunityArea.toString());
        }

        // Obtengo el listado de secciones (si aplica) y lo asigno a la zona y a la hoja del cuestionario
        if (this.zoneSelected.getSectionsName() != null && this.zoneSelected.getSectionsName().size() > 0 && this.answer) {
            this.questionnaire.setMarkedSectionsList(this.sectionsListAdapter.getSectionsList());
        }

        // Relleno todos los datos en formato texto
        this.questionnaire.setRootCause(this.autoCompleteRootCauses.getText().toString());
        this.questionnaire.setOpportunityArea(this.autoCompleteOpportunitiesAreas.getText().toString());
        this.questionnaire.setActionToSupplier(this.autoCompleteActionsForSupplier.getText().toString());
        this.questionnaire.setActionToClient(this.autoCompleteActionsForClient.getText().toString());

        // Obtengo las observaciones (si es que hay).
        this.questionnaire.setObservations(this.tvObservations.getText().toString().trim());

        // Añado la página del cuestionario al listado de páginas
        if (!(this.zoneSelected.getQuestionnairePageList().contains(questionnaire))) {
            this.zoneSelected.addQuestionnairePage(this.questionnaire);
        }

        // Marco la pregunta actual como respondida
        this.simpleQuestion.setAnswered(true);

        // Reinicio el contador por si acaso
        this.fieldsFilledCounter = 0;

        // Llamo al método del padre para informale que ya se terminó esta página del formulario
        this.onDoneOneQuestionnairePage.onDoneQuestionnairePage();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        this.dataViewModel.getActionsForClient().removeObservers(getActivity());
        this.dataViewModel.getActionsForSupplier().removeObservers(getActivity());
        this.dataViewModel.getRootCauses().removeObservers(getActivity());
        this.dataViewModel.getOpportunityAreas().removeObservers(getActivity());
    }

    // Método disparado cuando se presiona el botón para tomar una foto para cierta sección
    // Método disparado desde SectionListAdapter
    @Override
    public void positionOnHitPhotoButtonForSection(int positionSection, ImageView ivPreview) {

        // Para poder hacer referencia a la  sección seleccionada,
        // se guarda a nivel de instancia la posición de la sección
        this.tempIndexSectionSelected = positionSection;

        this.tempSectionImgPreview = ivPreview;
        takePhoto(TAG_TYPE_FOR_URI_SECTION);
    }


    public interface OnDoneOneQuestionnairePage {
        void onDoneQuestionnairePage();
    }
}
