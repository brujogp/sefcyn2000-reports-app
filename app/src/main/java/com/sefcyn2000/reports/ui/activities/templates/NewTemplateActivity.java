package com.sefcyn2000.reports.ui.activities.templates;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Template;
import com.sefcyn2000.reports.data.viewmodels.DataViewModel;
import com.sefcyn2000.reports.ui.fragments.templates.newtemplates.DefinitionFormMapFragment;
import com.sefcyn2000.reports.ui.fragments.templates.newtemplates.NewTemplateGeneralDataFragment;
import com.sefcyn2000.reports.utilities.constans.PathTemplatesEnum;

import static com.sefcyn2000.reports.ui.activities.templates.TemplatesActivity.KEY_CLIENT_TEMPLATE_CODE;
import static com.sefcyn2000.reports.ui.activities.templates.TemplatesActivity.KEY_TEMPLATE_CODE;
import static com.sefcyn2000.reports.ui.activities.templates.TemplatesActivity.SUFFIX_COMPLETE_TEMPLATE;
import static com.sefcyn2000.reports.ui.activities.templates.TemplatesActivity.SUFFIX_INCOMPLETE_TEMPLATE;
import static com.sefcyn2000.reports.utilities.constans.PathTemplatesEnum.COMPLETES_TEMPLATES;
import static com.sefcyn2000.reports.utilities.constans.PathTemplatesEnum.INCOMPLETE_TEMPLATES;

public class NewTemplateActivity extends AppCompatActivity implements View.OnClickListener {
    private DataViewModel dataViewModel;

    // Plantilla que será construida dentro del formulario
    Template template;

    Toolbar toolbar;

    FragmentManager fragmentManager;

    private Button btnCancel;
    private Button btnNext;

    ProgressDialog dialog;

    private NewTemplateGeneralDataFragment newTemplateGeneralDataFragment;
    private DefinitionFormMapFragment definitionTemplateFragment;
    private boolean isProcessCompleteForForm = false;

    private boolean isEditorModeActivate = false;
    private boolean isTemplateComplete;
    private boolean isIncompleteTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_template);

        this.toolbar = findViewById(R.id.toolbar_included);
        setSupportActionBar(this.toolbar);

        this.fragmentManager = getSupportFragmentManager();

        this.dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        // Hago una instancia de la plantilla que se generará a lo largo del formulario
        this.template = new Template();

        // Configura el texto y los eventos de los dos botones
        configButtons();

        if (getIntent() != null && getIntent().getExtras() != null) {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Recuperando datos");
            progressDialog.create();
            progressDialog.show();

            String templateCode = getIntent().getExtras().getString(KEY_TEMPLATE_CODE);
            String clientTemplateCode = getIntent().getExtras().getString(KEY_CLIENT_TEMPLATE_CODE);

            if (templateCode != null && clientTemplateCode != null) {

                this.isEditorModeActivate = true;

                if (templateCode.endsWith(SUFFIX_COMPLETE_TEMPLATE)) {
                    this.isTemplateComplete = true;
                    templateCode = templateCode.replace(SUFFIX_COMPLETE_TEMPLATE, "");
                    this.isIncompleteTemplate = false;
                } else {
                    this.isTemplateComplete = false;
                    templateCode = templateCode.replace(SUFFIX_INCOMPLETE_TEMPLATE, "");
                    this.isIncompleteTemplate = true;
                }

                String path = this.isTemplateComplete ? COMPLETES_TEMPLATES.getPath() : INCOMPLETE_TEMPLATES.getPath();

                this.dataViewModel.getTemplateById(path, clientTemplateCode, templateCode).observe(this, templ -> {
                    if (templ != null) {
                        this.template = templ;

                        // Creo las instancias de los fragmentos para mantener el progreso de cada uno de ellos
                        this.newTemplateGeneralDataFragment = new NewTemplateGeneralDataFragment(this.template);
                        this.definitionTemplateFragment = new DefinitionFormMapFragment(this.template);

                        // Muestro el fragmento para la toma de datos generales
                        showFirstStep();

                        progressDialog.hide();
                    }
                });
            }
        } else {

            // Creo las instancias de los fragmentos para mantener el progreso de cada uno de ellos
            this.newTemplateGeneralDataFragment = new NewTemplateGeneralDataFragment(this.template);
            this.definitionTemplateFragment = new DefinitionFormMapFragment(this.template);

            // Muestro el fragmento para la toma de datos generales
            showFirstStep();
        }

        this.dialog = new ProgressDialog(this);
        this.dialog.setMessage("Registrando");

        getSupportActionBar().setTitle(R.string.new_template_action);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Configuro el Toolbar para que pueda navegar a la anterior activity a través de su botón de retroceso
        this.toolbar.setNavigationOnClickListener(v -> exitProcess(true));
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Creo las instancias de los fragmentos para mantener el progreso de cada uno de ellos
        if (this.newTemplateGeneralDataFragment == null && this.definitionTemplateFragment == null && !this.isEditorModeActivate) {
            exitProcess(false);
        }

        this.isEditorModeActivate = false;
    }

    private void configButtons() {
        this.btnCancel = findViewById(R.id.btn_cancel);
        this.btnNext = findViewById(R.id.btn_next);

        this.btnNext.setOnClickListener(this);
        this.btnCancel.setOnClickListener(this);
    }

    void setStep(int id) {
        // Obtengo y recorro la lista de fragmentos que son visibles
        for (Fragment f : this.fragmentManager.getFragments()) {
            if (
                    (f.isVisible() && f.getTag().equals(NewTemplateGeneralDataFragment.class.toString())) &&
                            id == R.id.btn_next
            ) {
                // Verifico si el primero formulario es válido
                if (((VerifyFistStepForm) this.newTemplateGeneralDataFragment).isValidFirstFormStep()) {
                    showLastStep();
                }

            } else if (
                    (f.isVisible() && f.getTag().equals(DefinitionFormMapFragment.class.toString())) &&
                            id == R.id.btn_cancel
            ) {
                if (((VerifySecondStepForm) this.definitionTemplateFragment).isValidSecondFormStep()) {
                    ((VerifySecondStepForm) this.definitionTemplateFragment).getInfoFromDefinitionTemplateForm();
                    showFirstStep();
                }
            } else if (
                    (f.isVisible() && f.getTag().equals(DefinitionFormMapFragment.class.toString())) &&
                            id == R.id.btn_next
            ) {
                if (((VerifySecondStepForm) this.definitionTemplateFragment).isValidSecondFormStep()) {
                    saveTemplate();
                }

            } else if (
                    (f.isVisible() && f.getTag().equals(NewTemplateGeneralDataFragment.class.toString())) &&
                            id == R.id.btn_cancel
            ) {
                exitProcess(true);
            }
        }
    }

    private void showFirstStep() {
        this.btnCancel.setText(getResources().getString(R.string.btn_step_cancel));
        this.btnNext.setText(getResources().getString(R.string.btn_step_next));

        this.fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, this.newTemplateGeneralDataFragment, NewTemplateGeneralDataFragment.class.toString())
                .commit();
    }

    private void showLastStep() {
        this.btnCancel.setText(getResources().getString(R.string.btn_last_step));
        this.btnNext.setText(getResources().getString(R.string.btn_step_save));

        this.fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, this.definitionTemplateFragment, DefinitionFormMapFragment.class.toString())
                .setReorderingAllowed(true)
                .commit();
    }


    void saveTemplate() {

        if (((VerifySecondStepForm) this.definitionTemplateFragment).getInfoFromDefinitionTemplateForm()) {
            this.dialog.show();

            this.dataViewModel.createTemplate(COMPLETES_TEMPLATES.getPath(), this.template, this.isIncompleteTemplate).
                    observe(this, isSaved -> {
                        if (isSaved) {
                            this.isProcessCompleteForForm = true;

                            this.dialog.dismiss();
                            startActivity(new Intent(this, TemplatesActivity.class));
                            finish();
                        }
                    });
        } else {
            this.dialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Hay un error en el formulario")
                    .setMessage("Por favor revise el formulario e intente de nuevo.")
                    .setPositiveButton("Está bien", (dialog1, which) -> {
                        dialog1.dismiss();
                    })
                    .create()
                    .show();
        }

    }

    private void exitProcess(boolean checkVerifications) {
        if (checkVerifications) {
            if (((VerifyFistStepForm) this.newTemplateGeneralDataFragment).isFormEmpty()) {
                this.isProcessCompleteForForm = true;
                startActivity(new Intent(this, TemplatesActivity.class));
                finish();
            } else {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder
                        .setTitle("¿Salir del editor?")
                        .setMessage("Puede guardar la información de la plantilla para seguir después o puede descartar todos los cambios.")
                        .setNeutralButton("Guardar cambios", (dialog1, which) -> {
                            this.isProcessCompleteForForm = true;
                            saveOnIncompleteTemplates();

                            new AlertDialog.Builder(this)
                                    .setMessage("La plantilla aparecerá en la sección \"PLANTILLAS EN PROCESO\"")
                                    .setPositiveButton("Está bien", (dialog2, which1) -> {
                                        dialog2.dismiss();
                                        startActivity(new Intent(this, TemplatesActivity.class));
                                        finish();
                                    })
                                    .create()
                                    .show();
                        })
                        .setPositiveButton("Descartar cambios", (dialog1, which) -> {
                            this.isProcessCompleteForForm = true;
                            startActivity(new Intent(this, TemplatesActivity.class));
                            finish();
                        })
                        .setNegativeButton("Cancelar", (dialog1, which) -> dialog1.dismiss())
                        .create()
                        .show();
            }

        } else {
            this.isProcessCompleteForForm = true;
            startActivity(new Intent(this, TemplatesActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        setStep(view.getId());
    }

    @Override
    public void onBackPressed() {
        exitProcess(true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!this.isProcessCompleteForForm && (this.template.getLinkedClientId() != null && !this.template.getLinkedClientId().isEmpty())) {
            saveOnIncompleteTemplates();
        }
    }

    private void saveOnIncompleteTemplates() {
        ((VerifyFistStepForm) this.newTemplateGeneralDataFragment).isValidFirstFormStep();
        ((VerifySecondStepForm) this.definitionTemplateFragment).getInfoFromDefinitionTemplateForm();

        dataViewModel.createTemplate(PathTemplatesEnum.INCOMPLETE_TEMPLATES.getPath(), this.template, false).observe(this, isSaved -> {
        });

        this.newTemplateGeneralDataFragment = null;
        this.definitionTemplateFragment = null;
    }

    public interface VerifyFistStepForm {
        // Metodo que por dentro recoge datos
        boolean isValidFirstFormStep();

        boolean isFormEmpty();
    }

    public interface VerifySecondStepForm {
        boolean isValidSecondFormStep();

        // Metodo que por dentro recoge datos
        boolean getInfoFromDefinitionTemplateForm();
    }
}