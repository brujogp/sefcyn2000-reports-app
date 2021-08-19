package com.sefcyn2000.reports.ui.fragments.templates.listtemplates;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Template;
import com.sefcyn2000.reports.data.viewmodels.DataViewModel;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationIncompleteTemplateSelectedViewModel;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationTemplateSelectedViewModel;
import com.sefcyn2000.reports.ui.adapters.templates.SimpleListIncompleteTemplatesAdapter;
import com.sefcyn2000.reports.utilities.constans.PathTemplatesEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sefcyn2000.reports.ui.adapters.templates.SimpleListIncompleteTemplatesAdapter.DELETE_ACTION_ON_CLICK_BUTTON;
import static com.sefcyn2000.reports.ui.adapters.templates.SimpleListIncompleteTemplatesAdapter.EDIT_ACTION_ON_CLICK_BUTTON;

public class InProcessTemplatesListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SimpleListIncompleteTemplatesAdapter.TemplateClicked {
    private DataViewModel dataViewModel;
    private CommunicationIncompleteTemplateSelectedViewModel communicationIncompleteTemplateSelectedViewModel;

    private SwipeRefreshLayout swipeRefreshTemplates;
    private ProgressBar pbTemplatesLoad;
    private RecyclerView recyclerViewIncompleteTemplates;
    private SimpleListIncompleteTemplatesAdapter incompleteTemplatesAdapter;

    private List<Template> incompleteTemplatesList;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        this.dataViewModel = viewModelProvider.get(DataViewModel.class);
        this.communicationIncompleteTemplateSelectedViewModel = viewModelProvider.get(CommunicationIncompleteTemplateSelectedViewModel.class);

        this.communicationIncompleteTemplateSelectedViewModel.getIsDeletedIncompleteTemplateObserver().observe(getActivity(), isDeletedTemplate -> {
            if (isDeletedTemplate) {
                setDataAdapter();
            }
        });

        this.incompleteTemplatesList = new ArrayList<>();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_template_incomplete_template, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        this.recyclerViewIncompleteTemplates = view.findViewById(R.id.recycler_view_list_incomplete_templates);
        this.swipeRefreshTemplates = view.findViewById(R.id.swipeRefreshLayout_templates);
        this.swipeRefreshTemplates.setOnRefreshListener(this);
        this.pbTemplatesLoad = view.findViewById(R.id.pb_templates);
        this.swipeRefreshTemplates.setRefreshing(false);

        this.incompleteTemplatesAdapter = new SimpleListIncompleteTemplatesAdapter(getContext(), this.incompleteTemplatesList, this);
        this.recyclerViewIncompleteTemplates.setAdapter(this.incompleteTemplatesAdapter);
        this.recyclerViewIncompleteTemplates.setLayoutManager(new LinearLayoutManager(getActivity()));

        setDataAdapter();
    }

    private void setDataAdapter() {

        // Obtengo todas las plantillas INCOMPLETAS de cada uno de los clientes
        this.dataViewModel.getTemplates(PathTemplatesEnum.INCOMPLETE_TEMPLATES.getPath()).observe(getActivity(), templates -> {
            List<Template> temTemplatesList = new ArrayList<>();

            for (List<Template> listTemplates : templates) {
                for (Template t : listTemplates) {
                    if (t != null) {
                        temTemplatesList.add(t);
                    }
                }
            }

            this.incompleteTemplatesList = temTemplatesList;

            setDataForList();
        });
    }

    private void setDataForList() {
        this.incompleteTemplatesAdapter.setTemplates(this.incompleteTemplatesList);
        this.incompleteTemplatesAdapter.notifyDataSetChanged();

        this.pbTemplatesLoad.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        if (this.swipeRefreshTemplates.isRefreshing()) {
            this.swipeRefreshTemplates.setRefreshing(false);
            setDataAdapter();
        }
    }

    @Override
    public void onClickTemplateClicked(Template template, String actionType) {
        if (actionType.equals(DELETE_ACTION_ON_CLICK_BUTTON)) {
            String tagDeleteTemplate = DELETE_ACTION_ON_CLICK_BUTTON;

            this.communicationIncompleteTemplateSelectedViewModel
                    .setTemplateSelected(template.getLinkedClientId(), template.getTemplateId() + tagDeleteTemplate);
        } else if (actionType.equals(EDIT_ACTION_ON_CLICK_BUTTON)) {
            String tagEditTemplate = EDIT_ACTION_ON_CLICK_BUTTON;

            this.communicationIncompleteTemplateSelectedViewModel
                    .setTemplateSelected(template.getLinkedClientId(), template.getTemplateId() + tagEditTemplate);
        }
    }
}
