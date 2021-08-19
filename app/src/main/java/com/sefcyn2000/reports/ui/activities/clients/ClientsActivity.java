package com.sefcyn2000.reports.ui.activities.clients;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationFragmentViewModel;
import com.sefcyn2000.reports.ui.fragments.clients.ListClientsFragment;

public class ClientsActivity extends AppCompatActivity {
    Toolbar toolbar;
    CommunicationFragmentViewModel communicationFragmentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);
        setToolbar();

        FloatingActionButton newClientFab = findViewById(R.id.new_client);
        newClientFab.setOnClickListener(v -> {
            startActivity(new Intent(this, NewClientActivity.class));
            finish();
        });

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, ListClientsFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        communicationFragmentViewModel = new ViewModelProvider(this).get(CommunicationFragmentViewModel.class);

        communicationFragmentViewModel.getSelectedItem().observe(this, client -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog
                    .setTitle("¿Editar cliente?")
                    .setMessage("¿Quiere editar el cliente \"" + client.getName() + "\"?")
                    .setPositiveButton("Editar", (dialog, which) -> {
                        Intent intent = new Intent(this, NewClientActivity.class);

                        intent.putExtra("CLIENT-CODE", client.getCodeClient());

                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });
    }

    private void setToolbar() {
        this.toolbar = findViewById(R.id.toolbar_included);
        setSupportActionBar(this.toolbar);

        getSupportActionBar().setTitle(R.string.clients_action);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.communicationFragmentViewModel.getSelectedItem().removeObservers(this);
    }
}