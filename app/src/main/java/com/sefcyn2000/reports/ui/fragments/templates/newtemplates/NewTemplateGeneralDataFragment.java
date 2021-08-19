package com.sefcyn2000.reports.ui.fragments.templates.newtemplates;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.entities.Template;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationFragmentViewModel;
import com.sefcyn2000.reports.ui.activities.templates.NewTemplateActivity;
import com.sefcyn2000.reports.ui.fragments.clients.ParentDialogFragmentClientsList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewTemplateGeneralDataFragment extends Fragment implements NewTemplateActivity.VerifyFistStepForm, TextWatcher {
    private CommunicationFragmentViewModel communicationViewModel;
    private TextView tvCreateAt;
    private AutoCompleteTextView acClientSelected;
    private ParentDialogFragmentClientsList dialog;

    private Client client;
    private TextInputEditText etTemplateName;
    private TextInputEditText etTemplateAddress;
    private TextInputEditText etTemplateNotes;

    final private Template template;
    private boolean dirtyForm = false;
    private Boolean isValidForm;
    private boolean isEditableModeActivate = false;

    public NewTemplateGeneralDataFragment(Template template) {
        super(R.layout.fragment_templates_general_data);
        this.template = template;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.communicationViewModel = new ViewModelProvider(getActivity()).get(CommunicationFragmentViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Muestro el diálogo flotante para la selección del cliente
        this.acClientSelected = getView().findViewById(R.id.ac_client_selected);

        // Si la plantilla ya tiene identificador, quiere decir que ya existia anteriormente, por lo tanta, se esta editando
        if (this.template.getTemplateId() != null && !this.template.getTemplateId().isEmpty()) {
            this.acClientSelected.setEnabled(false);
            this.isEditableModeActivate = true;
        } else {
            this.acClientSelected.setOnClickListener(v -> {
                this.dialog = ParentDialogFragmentClientsList.getInstance();
                this.dialog.show(getChildFragmentManager(), "");
            });

            this.communicationViewModel.getSelectedItem().observe(getViewLifecycleOwner(), clientSelected -> {
                if (clientSelected != null) {
                    this.client = clientSelected;
                    this.acClientSelected.setText(this.client.getName());
                    if (this.dialog != null) {

                        this.dialog.dismiss();
                        this.dirtyForm = true;

                    }

                    // Inicializo algunos datos de la plantilla
                    this.template.setCreatedAt(new Timestamp(new Date()));
                    this.template.setLinkedClientId(this.client.getCodeClient());
                    this.template.setClientName(this.client.getName());
                    this.template.setUsedCount(0L);
                    this.template.setReportsChildren(new ArrayList<>());
                }
            });
        }


        this.tvCreateAt = getView().findViewById(R.id.tv_current_date);
        this.tvCreateAt.setText(getCurrentTimeFormat());

        this.etTemplateName = getView().findViewById(R.id.et_template_name);
        this.etTemplateAddress = getView().findViewById(R.id.et_template_address);
        this.etTemplateNotes = getView().findViewById(R.id.et_template_notes);

        // Obtengo el cliente seleccionado desde el diálogo flotante

        setterEvents();
        fillFields();
    }

    private void fillFields() {
        if (this.isEditableModeActivate) {

            this.acClientSelected.setText(this.template.getClientName());

            if (this.template.getName() != null && !this.template.getName().isEmpty()) {
                this.etTemplateName.setText(this.template.getName());
            }

            if (this.template.getAddress() != null && !this.template.getAddress().isEmpty()) {
                this.etTemplateAddress.setText(this.template.getAddress());
            }

            if (this.template.getNotes() != null && !this.template.getNotes().isEmpty()) {
                this.etTemplateNotes.setText(this.template.getNotes());
            }
        }
    }

    private void setterEvents() {
        if (!this.isEditableModeActivate) {
            this.acClientSelected.addTextChangedListener(this);
        } else {
            this.dirtyForm = true;
        }

        this.etTemplateName.addTextChangedListener(this);
        this.etTemplateAddress.addTextChangedListener(this);
        this.etTemplateNotes.addTextChangedListener(this);

    }

    private String getCurrentTimeFormat() {
        String dateString = "";

        if (this.isEditableModeActivate) {
            dateString = new SimpleDateFormat("dd/MM/yyyy").format(this.template.getCreatedAt().toDate());
        } else {

            dateString = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        }


        return dateString;
    }


    private boolean getEtData() {
        this.isValidForm = true;

        String address = this.etTemplateAddress.getText().toString();
        String notes = this.etTemplateNotes.getText().toString();
        String name = this.etTemplateName.getText().toString();
        String clientName = this.acClientSelected.getText().toString();

        if (clientName.isEmpty()) {
            isValidForm = false;
            Toast.makeText(getContext(), "Debe seleccionar un cliente", Toast.LENGTH_LONG).show();
        }

        if (!name.isEmpty()) {
            this.template.setName(name);
        } else {
            isValidForm = false;
            this.etTemplateName.setError("Debe darle un nombre a la unidad o inmueble");
        }

        if (!address.isEmpty()) {
            this.template.setAddress(address);
        } else {
            isValidForm = false;
            this.etTemplateAddress.setError("Debe introducir una dirección");
        }

        if (!notes.isEmpty()) {
            this.template.setNotes(notes);
        }

        return this.isValidForm;
    }


    @Override
    public boolean isValidFirstFormStep() {
        return getEtData();
    }

    @Override
    public boolean isFormEmpty() {
        return !this.dirtyForm;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (before != start) {
            this.dirtyForm = true;
        }

        if (((AutoCompleteTextView) getView().findViewById(R.id.ac_client_selected)).getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Por favor seleccione un cliente")
                    .setPositiveButton("Está bien", (dialog1, which) -> {
                        dialog1.dismiss();
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
