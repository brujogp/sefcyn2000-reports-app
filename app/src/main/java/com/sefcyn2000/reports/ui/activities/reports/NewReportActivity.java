package com.sefcyn2000.reports.ui.activities.reports;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.entities.Report;
import com.sefcyn2000.reports.data.entities.Template;
import com.sefcyn2000.reports.data.entities.templatecomponents.Category;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationFragmentViewModel;
import com.sefcyn2000.reports.data.viewmodels.DataViewModel;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationTemplateSelectedViewModel;
import com.sefcyn2000.reports.ui.fragments.reports.newreport.EquipmentsRegistrationFragment;
import com.sefcyn2000.reports.ui.fragments.reports.newreport.GeneralDataReportFragment;
import com.sefcyn2000.reports.ui.fragments.reports.newreport.ListPartsOfMapReportFragment;
import com.sefcyn2000.reports.ui.fragments.reports.newreport.floatingfragments.ParentQuestionFloatingFragment;
import com.sefcyn2000.reports.ui.fragments.templates.listtemplates.ExpandableListViewListTemplatesFragment;
import com.sefcyn2000.reports.utilities.helpers.HelperGenerateHtmlReport;
import com.sefcyn2000.reports.utilities.retrofit.NotificationBackend;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.sefcyn2000.reports.utilities.constans.PathTemplatesEnum.COMPLETES_TEMPLATES;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewReportActivity extends AppCompatActivity implements
        View.OnClickListener,
        ParentQuestionFloatingFragment.OnCompleteQuestionnaire,
        EquipmentsRegistrationFragment.OnAddOneMiniFormEquipment {
    private CommunicationFragmentViewModel communicationViewModel;
    private CommunicationTemplateSelectedViewModel communicationTemplateSelectedViewModel;

    MaterialButton materialButtonCancel;
    MaterialButton materialButtonNext;

    Toolbar toolbar;
    FragmentManager fragmentManager;

    private Map<String, String> clientIdTemplateIdSelected;
    private Report report;
    private TextView tvTemplateNameSelected;
    private TextView tvCurrentPhase;
    private TextView tvInstructionsStep;
    private DataViewModel dataViewModel;
    private Client clientSelected;
    private Template currentTemplate;
    private int totalZonesOfMap;
    private boolean isSecondVisit = false;
    private NotificationBackend notificationBackend;
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        this.toolbar = findViewById(R.id.toolbar_included);
        this.materialButtonCancel = findViewById(R.id.btn_report_cancel);
        this.materialButtonNext = findViewById(R.id.btn_report_next);
        this.tvInstructionsStep = findViewById(R.id.tv_instructions_new_report);

        this.tvTemplateNameSelected = findViewById(R.id.tv_template_name_selected);
        this.tvCurrentPhase = findViewById(R.id.tv_phase);

        this.materialButtonCancel.setOnClickListener(this);
        this.materialButtonNext.setOnClickListener(this);

        this.fragmentManager = getSupportFragmentManager();
        this.tvInstructionsStep.setText(getResources().getText(R.string.instruction_new_report_step_take_general_data));

        this.fragmentManager
                .beginTransaction()
                .replace(
                        R.id.fragmentContainerView_report_info,
                        GeneralDataReportFragment.class,
                        null,
                        GeneralDataReportFragment.class.toString())
                .commit();

        tvCurrentPhase.setText("Toma de datos generales");

        this.constraintLayout = findViewById(R.id.con_conteinare);
        this.bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        this.bottomSheetBehavior.setHideable(false);

        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        this.bottomSheetBehavior.setFitToContents(false);

        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle("Nuevo Reporte");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.toolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(this, ReportsActivity.class);
            startActivity(intent);
            finish();
        });

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);

        this.communicationViewModel = viewModelProvider.get(CommunicationFragmentViewModel.class);
        this.communicationTemplateSelectedViewModel = viewModelProvider.get(CommunicationTemplateSelectedViewModel.class);
        this.dataViewModel = viewModelProvider.get(DataViewModel.class);

        this.communicationViewModel.getReportObserver().observe(this, report -> {
            if (report != null) {
                this.report = report;
            }
        });

        // Evento disparado cuando se selecciona una plantilla
        // Suministra un mapa con clave = al ID del cliente y con valor = al ID de la plantilla seleccionada
        this.communicationTemplateSelectedViewModel.getTemplateSelectedObserver().observe(this, clientIdTemplateIdSelected -> {
            if (clientIdTemplateIdSelected != null) {
                this.clientIdTemplateIdSelected = clientIdTemplateIdSelected;

                for (Map.Entry<String, String> item : this.clientIdTemplateIdSelected.entrySet()) {

                    // Descargo info. del cliente
                    this.dataViewModel.getClientById(item.getKey()).observe(this, client -> {

                        if (client != null) {
                            this.clientSelected = client;

                            // Si se ha generado más de un reporte
                            if (this.clientSelected.getReportsCounter() >= 1) {
                                this.isSecondVisit = true;
                            }

                            templateSelected(item.getKey(), item.getValue());
                        }
                    });
                }
            }
        });

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl("https://sefcyn2000-1e8af.ue.r.appspot.com/")
                .build();

        this.notificationBackend = retrofit.create(NotificationBackend.class);
    }

    // Método disparado cuando se selecciona una plantilla
    // Este método muestra el siguiente fragment (el que enlista las categorías) y descarga la plantilla seleccionada
    private void templateSelected(String clientId, String templateId) {
        tvCurrentPhase.setText("Resolución del formulario");
        this.tvInstructionsStep.setText(getResources().getText(R.string.instruction_new_report_step_questionnaire));

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Leyendo plantilla");
        progressDialog.setMessage("Por favor espere");

        this.materialButtonCancel.setEnabled(false);
        progressDialog.show();

        if (this.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        // Una vez obtenidos los valores (la llave del mapa y el valor del mapa), los uso para generar un path
        // con el que puedo obtener la plantilla correcta
        this.dataViewModel.getTemplateById(
                COMPLETES_TEMPLATES.getPath(),
                clientId,
                templateId.trim()
        ).observe(this, template ->
        {
            if (template != null) {
                // TODO: Aquí se tendrá que hacer la comprobación para ver si la versión de la plantilla obtenida coinside
                //  con la versión de la plantilla contenida en el reporte reporte de la primera visita para la segunda visita

                // Obtengo la plantilla descargada y la almaceno a nivel de instancia
                this.currentTemplate = template;

                // Obtengo el total de zonas de todas las categorías
                for (Category category : this.currentTemplate.getCategories()) {
                    this.totalZonesOfMap += category.getZones().size();
                }

                // Registra el ID del cliente al cual el reporte estará vinculado
                this.report.setLinkedWithClient(clientId);

                // Registra el ID de la plantilla que se usará para el reporte
                // Esto solo será referencia, ya que se hará una copia de la plantilla
                this.report.setFromTemplate(templateId);

                // Crea del fragmente un listado de todas las partes de la plantilla.
                // Se le pasa la información que se ha recolectado del reporte y la plantilla descargada.
                ListPartsOfMapReportFragment listPartsOfMapReportFragment = new ListPartsOfMapReportFragment(
                        this,
                        this.report,
                        template,
                        progressDialog
                );

                this.tvTemplateNameSelected.setText(template.getName());

                this.fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView_report_info, listPartsOfMapReportFragment, ListPartsOfMapReportFragment.class.toString())
                        .commit();
            }
        });
    }

    // Método que comienza el proceso para generar un reporte
    // El primero paso es llamar al Helper para generar el reporte en formato PDF
    private void saveReport() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Persistiendo datos");
        progressDialog.create();
        progressDialog.show();

        // Hago que el reporte tenga el último valor para el cliente y le sumo 1
        this.report.setReportNum(this.clientSelected.getReportsCounter());

        // Asigno el mapa que está dentro de la plantilla que se seleccionó para la generación del reporte
        this.report.setTemplateMap(this.currentTemplate.getCategories());

        // Asigno el nombre de la unidad desde la plantilla
        this.report.setUnityName(this.currentTemplate.getName());

        // La fecha que se coloca dentro del reporte será diferente tanto en hora como incluso en día a la fecha de inicio
        this.report.setFinishedAt(new Timestamp(new Date()));

        // Creo una instancia del helper que genera un lienzo HTML para el reporte
        // HelperGenerateHtmlReport helperGenerateReport = new HelperGenerateHtmlReport(this, this);

        // Generador del reporte
        // helperGenerateReport.generateReport(this.report);

        // Guardo el reporte
        this.dataViewModel.saveReport(this.report).observe(this, isSaved -> {
            if (isSaved) {

                Toast.makeText(this, "Reporte guardado", Toast.LENGTH_LONG).show();

                notifyReport();

                progressDialog.hide();
                exitProcess();

            }
        });
    }

    private void notifyReport() {
        this.notificationBackend.notifyReportsFinished(this.report.getLinkedWithClient(), this.report.getReportId()).enqueue(
                new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 500) {
                            Toast.makeText(NewReportActivity.this, "Code: " + response.code() + ". ClientId: ".concat(report.getLinkedWithClient()).concat(", ReportId:  ").concat(report.getReportId()), Toast.LENGTH_LONG).show();
                            notifyReport();
                        } else {
                            Toast.makeText(NewReportActivity.this, "Code: " + response.code() + ". ClientId: ".concat(report.getLinkedWithClient()).concat(", ReportId:  ").concat(report.getReportId()), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                }
        );
    }

    void exitProcess() {
        startActivity(new Intent(this, ReportsActivity.class));
    }

    // Método que determina si en todas las zonas de cada categoría se contestaron las preguntas
    // Este método es llamado desde ParentQuestionFloatingFarmgment cuando todas las preguntas en la zona seleccionada
    // Es entonces, cuando se resta una zona del total de zonas
    @Override
    public void onCompleteQuestionnaire() {
        this.totalZonesOfMap--;

        if (this.totalZonesOfMap == 0) {
            this.materialButtonNext.setEnabled(true);
        }
    }

    // Método disparado cuando se añade y finaliza un miniform para el registro de equipos
    @Override
    public void onDoneMiniForm() {
        this.materialButtonNext.setEnabled(true);
    }

    // Evento disparado cuando se presiona el botón "Siguiente".
    // La primera vez que se dispara es después de terminar el rellenado de los campos de datos generales
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_report_next) {

            if (this.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            List<Fragment> fragments = this.fragmentManager.getFragments();

            for (Fragment f : fragments) {

                if (f.getTag().equals(ListPartsOfMapReportFragment.class.toString()) && f.isVisible()) {

                    // Creo el form para el registro de equipos
                    EquipmentsRegistrationFragment equipmentsRegistrationFragment = new EquipmentsRegistrationFragment(
                            this.report,
                            this.dataViewModel,
                            this
                    );

                    this.materialButtonNext.setEnabled(false);
                    this.materialButtonNext.setText(getResources().getString(R.string.btn_step_save));

                    this.fragmentManager
                            .beginTransaction()
                            .replace(
                                    R.id.fragmentContainerView_report_info,
                                    equipmentsRegistrationFragment,
                                    EquipmentsRegistrationFragment.class.toString())
                            .commit();
                    break;
                } else if (f.getTag().equals(EquipmentsRegistrationFragment.class.toString()) && f.isVisible()) {
                    saveReport();
                    break;
                } else if (f.getTag().equals(GeneralDataReportFragment.class.toString()) && f.isVisible()) {
                    tvCurrentPhase.setText("Selección de plantilla");

                    this.tvInstructionsStep.setText(getResources().getText(R.string.instruction_new_report_step_select_template));

                    this.materialButtonNext.setEnabled(false);
                    this.materialButtonCancel.setText(getResources().getText(R.string.btn_last_step));

                    this.fragmentManager
                            .beginTransaction()
                            .replace(
                                    R.id.fragmentContainerView_report_info,
                                    ExpandableListViewListTemplatesFragment.class,
                                    null,
                                    ExpandableListViewListTemplatesFragment.class.toString()
                            )
                            .commit();

                    break;
                }
            }
        }
    }
}
