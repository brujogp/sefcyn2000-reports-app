package com.sefcyn2000.reports.ui.activities.templates;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.viewmodels.DataViewModel;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationIncompleteTemplateSelectedViewModel;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationTemplateSelectedViewModel;
import com.sefcyn2000.reports.ui.activities.MainActivity;
import com.sefcyn2000.reports.ui.adapters.templates.FragmentTemplatesAdapter;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;

import static com.sefcyn2000.reports.ui.adapters.templates.SimpleListIncompleteTemplatesAdapter.DELETE_ACTION_ON_CLICK_BUTTON;
import static com.sefcyn2000.reports.ui.adapters.templates.SimpleListIncompleteTemplatesAdapter.EDIT_ACTION_ON_CLICK_BUTTON;

public class TemplatesActivity extends AppCompatActivity {
    public static final int NUMBER_TABS = 2;

    public static final String SUFFIX_COMPLETE_TEMPLATE = "_c";
    public static final String SUFFIX_INCOMPLETE_TEMPLATE = "_i";

    public static final String KEY_TEMPLATE_CODE = "TEMPLATE_CODE";
    public static final String KEY_CLIENT_TEMPLATE_CODE = "CLIENT_TEMPLATE_CODE";

    TabLayout tabLayout;
    ViewPager2 viewPagerTemplates;
    FragmentStateAdapter fragmentStateAdapter;
    Toolbar toolbar;
    List<String> titlesTab;

    private DataViewModel dataViewModel;

    private CommunicationTemplateSelectedViewModel communicationTemplateSelectedViewModel;
    private CommunicationIncompleteTemplateSelectedViewModel communicationIncompleteTemplateSelectedViewModel;

    public TemplatesActivity() {
        this.titlesTab = new ArrayList<>();
        this.titlesTab.add("Plantillas completas");
        this.titlesTab.add("Plantillas en proceso");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates);

        this.toolbar = findViewById(R.id.toolbar_included);
        this.tabLayout = findViewById(R.id.tab_layout);


        this.viewPagerTemplates = findViewById(R.id.view_pager_templates);
        this.fragmentStateAdapter = new FragmentTemplatesAdapter(this);
        this.viewPagerTemplates.setAdapter(this.fragmentStateAdapter);


        new TabLayoutMediator(this.tabLayout, this.viewPagerTemplates, (tab, position) -> {
            tab.setText(this.titlesTab.get(position));
        }).attach();

        setSupportActionBar(this.toolbar);

        getSupportActionBar().setTitle(R.string.templates_action);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);

        this.communicationIncompleteTemplateSelectedViewModel = viewModelProvider.get(CommunicationIncompleteTemplateSelectedViewModel.class);
        this.communicationTemplateSelectedViewModel = viewModelProvider.get(CommunicationTemplateSelectedViewModel.class);
        this.dataViewModel = viewModelProvider.get(DataViewModel.class);

        this.communicationIncompleteTemplateSelectedViewModel.getTemplateSelectedObserver().observe(this, clientTemplateMap -> {


            for (Map.Entry<String, String> map : clientTemplateMap.entrySet()) {

                if (map.getValue().endsWith(DELETE_ACTION_ON_CLICK_BUTTON)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle("¿Quiere eliminar la plantilla incompleta?")
                            .setPositiveButton("Sí", (dialog, which) -> {

                                this.dataViewModel.deleteTemplate(
                                        map.getKey(), map.getValue().replace(DELETE_ACTION_ON_CLICK_BUTTON, ""), false
                                ).observe(this, isDeleted -> {

                                    communicationIncompleteTemplateSelectedViewModel.isDeletedIncompleteTemplate(isDeleted);

                                });

                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();

                } else if (map.getValue().endsWith(EDIT_ACTION_ON_CLICK_BUTTON)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle("¿Quiere editar la plantilla?")
                            .setPositiveButton("Sí", (dialog, which) -> {

                                String clientId = map.getKey();
                                String templateId = map.getValue();

                                templateId = templateId.replace(EDIT_ACTION_ON_CLICK_BUTTON, "");

                                newTemplateForSelectTemplate(clientId, templateId);
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();


                }

            }
        });

        this.communicationTemplateSelectedViewModel.getTemplateSelectedObserver().observe(this, clientTemplateMap -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("¿Quiere editar la plantilla?")
                    .setMessage("Recuerde que los cambios que haga sobre una plantilla se verán reflejados en los reportes generados a partir de dicha plantilla.")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        String clientId = "";
                        String templateId = "";

                        for (Map.Entry<String, String> map : clientTemplateMap.entrySet()) {
                            clientId = map.getKey();
                            templateId = map.getValue() + SUFFIX_COMPLETE_TEMPLATE;
                        }

                        newTemplateForSelectTemplate(clientId, templateId);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });
    }

    public void newTemplate(View view) {
        Intent intent = new Intent(this, NewTemplateActivity.class);
        startActivity(intent);
        finish();
    }

    public void newTemplateForSelectTemplate(String clientId, String templateId) {
        if ((clientId != null && !clientId.isEmpty()) && (templateId != null && !templateId.isEmpty())) {

            Intent intent = new Intent(this, NewTemplateActivity.class);
            intent.putExtra(KEY_TEMPLATE_CODE, templateId);
            intent.putExtra(KEY_CLIENT_TEMPLATE_CODE, clientId);

            startActivity(intent);
            finish();
        }

    }
}