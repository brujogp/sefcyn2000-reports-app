package com.sefcyn2000.reports.ui.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.ui.activities.clients.ClientsActivity;
import com.sefcyn2000.reports.ui.activities.reports.ReportsActivity;
import com.sefcyn2000.reports.ui.activities.templates.TemplatesActivity;
import com.sefcyn2000.reports.utilities.FirebaseMessagingClientHelper;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.toolbar = findViewById(R.id.toolbar_included);

        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle(R.string.home_screen_title);

        new FirebaseMessagingClientHelper().messaging();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tool_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idItemSelected = item.getItemId();
        int theme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (idItemSelected) {
            case R.id.callHelp:
                Toast.makeText(this, "Llamando", Toast.LENGTH_LONG).show();
                break;
            case R.id.toggleTheme:
                if (theme == Configuration.UI_MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (theme == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToReportsActivity(View view) {
        Intent goToReportActivity = new Intent(this, ReportsActivity.class);
        startActivity(goToReportActivity);
    }

    public void goToTemplateActivity(View view) {
        Intent templateIntent = new Intent(this, TemplatesActivity.class);
        startActivity(templateIntent);
    }

    public void goToClientsActivity(View view) {
        Intent clientIntent = new Intent(this, ClientsActivity.class);
        startActivity(clientIntent);
    }
}