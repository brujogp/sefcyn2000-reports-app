package com.sefcyn2000.reports.ui.fragments.reports.newreport;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.entities.Report;
import com.sefcyn2000.reports.data.entities.Template;
import com.sefcyn2000.reports.data.entities.reportscomponents.Category;
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;
import com.sefcyn2000.reports.ui.fragments.reports.newreport.floatingfragments.ParentQuestionFloatingFragment;
import com.sefcyn2000.reports.data.entities.reportscomponents.Section;
import com.sefcyn2000.reports.data.viewmodels.DataViewModel;
import com.sefcyn2000.reports.ui.adapters.reports.ListCategoriesForQuestionnaireAdapter;
import com.sefcyn2000.reports.ui.adapters.reports.ReportListZonesOnListCategoriesAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment que enlista las categorías y zonas
 * Este fragmento aparece justo después de haber seleccionado una plantilla, por lo tanto, este fragmento no es flotante.
 */
public class ListPartsOfMapReportFragment extends Fragment implements ReportListZonesOnListCategoriesAdapter.ClickZoneForReport {
    private final Report reportInProgress;
    private final Template template;
    private final ProgressDialog progressDialog;
    private final ParentQuestionFloatingFragment.OnCompleteQuestionnaire onCompleteQuestionnaireImpl;
    private ParentQuestionFloatingFragment floatingQuestionsFragment;
    private RecyclerView recyclerViewCategoriesList;
    private ListCategoriesForQuestionnaireAdapter adapter;

    // private CheckDiffReportsHelper checkDiffReportsHelper;

    private DataViewModel downloadDataViewModel;
    private Client client;
    private List<SimpleQuestion> simpleListQuestions;


    // TODO: En este momento tendría que ver si hay o no hay plantillas anteriores que hayan sido creadas a partir de esta plantilla
    // TODO: Tengo que hacer que la info. de la plantilla esté contenida correctamenten el reporte
    public ListPartsOfMapReportFragment(
            ParentQuestionFloatingFragment.OnCompleteQuestionnaire onCompleteQuestionnaireImpl,
            Report report,
            Template template,
            ProgressDialog progressDialog
    ) {
        super(R.layout.fragment_report_new_report_questionnaire);
        this.template = template;
        this.reportInProgress = report;
        this.progressDialog = progressDialog;
        this.onCompleteQuestionnaireImpl = onCompleteQuestionnaireImpl;

        // Helper para el procesamiento del los datos de las plantillas, de los reportes y del cliente
        // this.checkDiffReportsHelper = new CheckDiffReportsHelper(getActivity(), this.template.getLinkedClientId(), this.downloadDataViewModel);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        this.downloadDataViewModel = viewModelProvider.get(DataViewModel.class);

        // Descargo los datos del cliente al cual está ligada la plantilla seleccionada
        this.downloadDataViewModel.getClientById(this.template.getLinkedClientId()).observe(getActivity(), client -> {
            if (client != null) {
                this.client = client;
            }
        });

        // Descargo la lista de preguntas
        this.downloadDataViewModel.getSimpleListQuestions().observe(getActivity(), simpleListQuestions -> {
            if (simpleListQuestions != null) {
                this.simpleListQuestions = simpleListQuestions;
            }
        });

        List<com.sefcyn2000.reports.data.entities.templatecomponents.Category> simpleMap = this.template.getCategories();

        List<Category> mapForReport = new ArrayList<>();

        // Primer loop en donde recorro las categorías de la plantilla simple después de haberla descargado
        // Aquí obtengo todos los datos de la plantilla simple y los asigno al mapa del reporte
        for (int i = 0; simpleMap.size() > i; i++) {

            // Obtengo una categoría del mapa simple de categorías
            com.sefcyn2000.reports.data.entities.templatecomponents.Category simpleCategory = simpleMap.get(i);

            // Creo el objeto Category para el reporte
            Category category = new Category(simpleCategory.getNameCategory(), simpleCategory.getZones().size(), 0, null);

            // Setteo el nombre de la categoría
            category.setCategoryName(simpleCategory.getNameCategory());

            // Segundo loop en donde recorro las zonas de la categoría
            for (com.sefcyn2000.reports.data.entities.templatecomponents.Zone simpleZone : simpleCategory.getZones()) {

                // Creo una nueva zona para el reporte dándole un nombre
                Zone zone = new Zone(simpleZone.getNameZone());

                // Setteo el nombre de la zona
                zone.setZoneName(simpleZone.getNameZone());

                // Añado la zonas a la lista de zonas de la categoría que está siendo creada.
                category.addZone(zone);

                // Si tiene secciones la zona, obtengo las secciones
                if (simpleZone.getSections() != null && simpleZone.getSections().size() > 0) {

                    for (com.sefcyn2000.reports.data.entities.templatecomponents.Section simpleSection : simpleZone.getSections()) {

                        // Añado la sección simple que viene desde la plantilla simple en la lista de secciones de la zona.
                        // Esta sera usada posteriormente para crear una lista de secciones mas compleja
                        zone.addSection(simpleSection);
                    }
                }
            }

            // Añado la categoría recien configurada a la lista de categorías
            mapForReport.add(category);
        }

        this.reportInProgress.setMap(mapForReport);

        if (this.reportInProgress.getMap() != null) {
            this.adapter = new ListCategoriesForQuestionnaireAdapter(
                    getActivity(),
                    this.reportInProgress.getMap(),
                    this
            );
        }

        this.progressDialog.dismiss();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        this.recyclerViewCategoriesList = view.findViewById(R.id.recycler_view_categories_for_questionnaire_list);

        this.recyclerViewCategoriesList.setAdapter(this.adapter);
        this.recyclerViewCategoriesList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // TODO: Terminar el checado de las diferencias del reporte
    /*
    private void checkDiffReport() {
        CheckDiffReportsHelper checkDiffReportsHelper = new CheckDiffReportsHelper();

        if (this.client.getReportsCounter() == 0) {
            return;
        }

        if ((this.client.getReportsCounter() % 2) == 0) {

        } else if ((this.client.getReportsCounter() % 2) != 0) {

        }
    }
     */

    @Override
    public void onClickZone(View v, int indexCategory, Zone zone) {
        this.floatingQuestionsFragment = new ParentQuestionFloatingFragment(
                this.simpleListQuestions,
                indexCategory,
                zone,
                this.adapter,
                this.onCompleteQuestionnaireImpl
        );
        this.floatingQuestionsFragment.show(getChildFragmentManager(), null);
    }

}
