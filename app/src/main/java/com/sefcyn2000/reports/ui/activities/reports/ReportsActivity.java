package com.sefcyn2000.reports.ui.activities.reports;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.viewmodels.DataViewModel;
import com.sefcyn2000.reports.ui.activities.MainActivity;
import com.sefcyn2000.reports.ui.fragments.reports.reportslist.ReportsListFragment;

public class ReportsActivity extends AppCompatActivity {

    private ReportsListFragment reportsListFragment;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Toolbar toolbar = findViewById(R.id.toolbar_included);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewReportActivity.class);
            startActivity(intent);
            finish();
        });

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);

        this.reportsListFragment = new ReportsListFragment(this, viewModelProvider.get(DataViewModel.class));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_reports_list, reportsListFragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}