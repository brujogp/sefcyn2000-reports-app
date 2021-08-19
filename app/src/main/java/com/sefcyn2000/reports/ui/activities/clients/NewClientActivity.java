package com.sefcyn2000.reports.ui.activities.clients;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.viewmodels.DataViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NewClientActivity extends AppCompatActivity implements TextWatcher {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;

    Toolbar toolbar;
    TextView tvDate;
    DataViewModel viewModel;

    TextInputEditText etNameClient;
    TextInputEditText etPhoneClient;
    TextInputEditText etNotesClient;
    ProgressDialog pd;
    AutoCompleteTextView acTypeClient;

    private ImagePicker imagePicker;
    private ImageView ivClientPhoto;

    private Uri uriImage;
    private boolean formDirty = false;

    private Client editedClient = null;
    private String codeClient = null;

    private boolean activeEditorMode = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        this.toolbar = findViewById(R.id.toolbar_included);
        setSupportActionBar(this.toolbar);

        this.etNameClient = findViewById(R.id.et_name);
        this.etPhoneClient = findViewById(R.id.et_phone);
        this.etNotesClient = findViewById(R.id.et_notes);
        this.acTypeClient = findViewById(R.id.ac_type_client);
        this.ivClientPhoto = findViewById(R.id.iv_client_image);
        this.tvDate = findViewById(R.id.tv_current_date);
        this.pd = new ProgressDialog(this);

        if (getIntent().getExtras() != null) {
            this.codeClient = getIntent().getExtras().getString("CLIENT-CODE");
            this.pd.setMessage("Obteniendo datos");
            this.pd.show();

            // Activo el modo editor
            this.activeEditorMode = true;
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setterEvents();

        LinearLayout btnTakePhoto = findViewById(R.id.btn_take_photo_include);

        btnTakePhoto.setOnClickListener(v -> {
            String[] p = new String[1];
            p[0] = Manifest.permission.CAMERA;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatcherTakePhotoIntent();
            } else {
                requestPermissions(p, REQUEST_CAMERA_PERMISSION);
            }
        });

        this.imagePicker = new ImagePicker(this, /* activity non null*/
                null, /* fragment nullable*/
                imageUri -> {/*on image picked */
                    if (imageUri != null) {
                        this.ivClientPhoto.setVisibility(View.VISIBLE);
                        this.uriImage = imageUri;
                        this.ivClientPhoto.setImageURI(imageUri);
                        this.formDirty = true;
                    }
                });


        this.toolbar.setNavigationOnClickListener(v -> {
            exitForm();
        });

        this.viewModel = new ViewModelProvider(this).get(DataViewModel.class);

        this.viewModel.getClientTypes("clientTypes").observe(this, types -> {
            this.acTypeClient.setAdapter(setterArrayAdapter(types));
        });

        if (this.codeClient != null) {
            this.viewModel.getClientById(this.codeClient).observe(this, client -> {
                this.editedClient = client;
                getSupportActionBar().setTitle(this.editedClient.getCodeClient());

                fillFields();
            });
        } else {
            getSupportActionBar().setTitle(R.string.new_clients_action);

            this.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            this.pd.setMessage("Registrando");
        }

    }

    // Método que rellena el formulario si se está editando algún cliente
    private void fillFields() {
        Date d = this.editedClient.getRegisterDate().toDate();
        this.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(d));

        this.etNameClient.setText(this.editedClient.getName());
        this.etPhoneClient.setText(this.editedClient.getPhone());
        this.acTypeClient.setText(this.editedClient.getClientType());
        this.etNotesClient.setText(this.editedClient.getNotes());

        if (this.editedClient.getClientImageUrl() != null && !(this.editedClient.getClientImageUrl().isEmpty())) {
            this.ivClientPhoto.setVisibility(View.VISIBLE);
            Glide.with(this).load(this.editedClient.getClientImageUrl()).into(this.ivClientPhoto);
        }

        this.pd.hide();
        this.pd.setMessage("Modificando");
    }

    private void setterEvents() {
        this.etNameClient.addTextChangedListener(this);
        this.etPhoneClient.addTextChangedListener(this);
        this.etNotesClient.addTextChangedListener(this);
        this.acTypeClient.addTextChangedListener(this);
    }

    void dispatcherTakePhotoIntent() {
        imagePicker.choosePicture(true /*show camera intents*/);
    }

    private String getCodeClient(Timestamp timestamp, String name) {

        return name.replace(" ", "").
                toUpperCase().
                substring(name.length() / 2).
                concat(String.valueOf(timestamp.toDate().getTime()).substring(3)).
                concat(String.valueOf(name.length()));
    }

    private ArrayAdapter<String> setterArrayAdapter(Map<String, String> types) {
        List<String> arrayTypeClient = new ArrayList<>();

        for (Map.Entry<String, String> item : types.entrySet()) {
            arrayTypeClient.add(item.getValue());
        }

        return new ArrayAdapter<>
                (
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        arrayTypeClient
                );
    }

    private boolean isValidForm() {
        boolean isValidForm = true;

        if (this.etNameClient.getText().toString().isEmpty()) {
            etNameClient.setError("Debe introducir el nombre del cliente");
            isValidForm = false;

        }

        if (this.etPhoneClient.getText().toString().isEmpty()) {
            this.etPhoneClient.setError("Debe introducir un número de contacto");
            isValidForm = false;

        }

        if (this.acTypeClient.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un tipo para el cliente", Toast.LENGTH_LONG).show();
            isValidForm = false;

        }

        if (this.ivClientPhoto.getVisibility() == View.GONE) {
            Toast.makeText(this, "Debe seleccionar o tomar una imagen para el cliente", Toast.LENGTH_LONG).show();
            isValidForm = false;

        }


        return isValidForm;
    }

    private void exitForm() {
        if ((!this.formDirty && this.uriImage == null)) {
            finishedProcess();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            if (this.activeEditorMode) {
                alertDialog
                        .setTitle("¿Salir del editor?")
                        .setMessage("Cualquier cambio que haya hecho en el cliente se descartará")
                        .setPositiveButton("Descartar cambios", (dialog, which) -> {
                            finishedProcess();
                        }).setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                }).create().show();

            } else {
                alertDialog
                        .setTitle("¿Salir del editor?")
                        .setMessage("Hay información en el formulario. Si sale, se perderá toda la información.")
                        .setPositiveButton("Salir", (dialog, which) -> {
                            finishedProcess();
                        }).setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                }).create().show();
            }
        }
    }

    private void finishedProcess() {
        pd.hide();
        startActivity(new Intent(this, ClientsActivity.class));
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.handlePermission(requestCode, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted. Continue the action or workflow
                // in your app.
                dispatcherTakePhotoIntent();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode, requestCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.codeClient == null) {
            getMenuInflater().inflate(R.menu.save_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.save_and_delete_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.save) {
            if (isValidForm()) {
                pd.show();

                if (this.editedClient != null) {

                    this.editedClient.setName(this.etNameClient.getText().toString());
                    this.editedClient.setPhone(this.etPhoneClient.getText().toString());
                    this.editedClient.setClientType(this.acTypeClient.getText().toString());
                    this.editedClient.setNotes(this.etNotesClient.getText().toString());

                    if (this.uriImage != null) {
                        this.editedClient.setClientImageUrl(this.uriImage.toString());
                    }

                    this.viewModel.createClient(this.editedClient).observe(this, isSaved -> {
                        if (isSaved) {
                            finishedProcess();
                        }
                    });

                } else {
                    String codeClient = getCodeClient(new Timestamp(new Date()), this.etNameClient.getText().toString());
                    Client client = new Client(
                            this.etNotesClient.getText().toString(),
                            this.etPhoneClient.getText().toString(),
                            new Timestamp(new Date()),
                            codeClient,
                            this.etNameClient.getText().toString(),
                            this.acTypeClient.getText().toString(),
                            this.uriImage.toString(),
                            0,
                            0,
                            0
                    );

                    this.viewModel.createClient(client).observe(this, isSaved -> {
                        if (isSaved) {
                            finishedProcess();
                        }
                    });
                }
            }
        } else if (menuItemId == R.id.delete) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog
                    .setTitle("¿Desea eliminar el cliente?")
                    .setMessage("Absolutamente toda la información asociada al cliente se eliminará (plantillas, reportes, imagenes, etc).")
                    .setPositiveButton("Eliminar cliente", (dialog, which) -> {
                        secondConfirmation();
                    }).setNegativeButton("Cancelar", (dialog, which) -> {
                dialog.dismiss();
            }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void secondConfirmation() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog
                .setTitle("¿Está completamente seguro en eliminar el cliente?")
                .setMessage("Esta acción no se podrá deshacer.")
                .setPositiveButton("Sí, eliminar cliente", (dialog, which) -> {

                    this.pd.setMessage("Eliminando");
                    this.pd.show();

                    this.viewModel.deleteClientById(this.codeClient).observe(this, isDeleted -> {
                        if (isDeleted) {
                            this.pd.hide();
                            Toast.makeText(this, "El cliente se ha eliminado", Toast.LENGTH_LONG).show();

                            finishedProcess();
                        }
                    });
                }).setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        }).create().show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (before != start) {
            this.formDirty = true;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onBackPressed() {
        exitForm();
    }
}