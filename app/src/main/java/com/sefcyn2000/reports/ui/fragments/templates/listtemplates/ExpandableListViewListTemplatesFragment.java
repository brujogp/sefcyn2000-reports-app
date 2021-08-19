package com.sefcyn2000.reports.ui.fragments.templates.listtemplates;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.entities.Template;
import com.sefcyn2000.reports.data.viewmodels.DataViewModel;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationTemplateSelectedViewModel;
import com.sefcyn2000.reports.ui.adapters.templates.ExpandableListTemplatesAdapter;
import com.sefcyn2000.reports.utilities.constans.PathTemplatesEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//TODO: Debido a que hay un error que desconozco el por qué se produce al
// momento de seleccionar una plantilla, usaré un RecyclerView dentro de otro
public class ExpandableListViewListTemplatesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ExpandableListTemplatesAdapter.TemplateClicked {
    private DataViewModel dataViewModel;
    private CommunicationTemplateSelectedViewModel newReportTemplateSelectedViewModel;

    private SwipeRefreshLayout swipeRefreshTemplates;
    private ExpandableListView expandableListViewTemplates;
    private ProgressBar pbTemplatesLoad;
    private ExpandableListTemplatesAdapter listTemplatesAdapter;

    private int clientsSize;
    private ArrayList<Client> clients;
    private ArrayList<String> groupsClients;
    private ArrayList<String> clientsName;
    private ArrayList<String> clientsCodes;
    private HashMap<String, List<Template>> templatesOfClients;
    private TextView textZeroTemplates;


    public ExpandableListViewListTemplatesFragment() {
        super(R.layout.fragment_template_list_templates);
        this.clientsCodes = new ArrayList<>();
        this.clientsName = new ArrayList<>();
        this.templatesOfClients = new HashMap<>();
        this.clients = new ArrayList<>();

        this.groupsClients = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        this.dataViewModel = viewModelProvider.get(DataViewModel.class);
        this.newReportTemplateSelectedViewModel = viewModelProvider.get(CommunicationTemplateSelectedViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        this.expandableListViewTemplates = view.findViewById(R.id.expandable_list_template);
        this.swipeRefreshTemplates = view.findViewById(R.id.swipeRefreshLayout_templates);
        this.swipeRefreshTemplates.setOnRefreshListener(this);
        this.pbTemplatesLoad = view.findViewById(R.id.pb_templates);
        this.swipeRefreshTemplates.setRefreshing(false);

        this.textZeroTemplates = view.findViewById(R.id.text_zero_templates);

        this.listTemplatesAdapter = new ExpandableListTemplatesAdapter(getActivity(), this.clientsName, this.templatesOfClients, this);
        this.expandableListViewTemplates.setAdapter(this.listTemplatesAdapter);

        setDataAdapter();
    }

    private void setDataAdapter() {
        this.clients = new ArrayList<>();
        this.groupsClients = new ArrayList<>();
        this.clientsName = new ArrayList<>();
        this.clientsCodes = new ArrayList<>();
        this.templatesOfClients = new HashMap<>();

        this.dataViewModel.getClients().observe(getActivity(), clients -> {
            // Primero obtengo los IDs y nombres de todos los clientes para hacer una
            // para hacer una busqueda primero de clientes y después, de sus respectivas plantillas
            if (clients != null && clients.size() > 0) {

                this.clientsSize = clients.size();

                for (Client client : clients) {
                    this.clientsName.add(client.getName());
                    this.clientsCodes.add(client.getCodeClient());
                    this.clients.add(client);
                }

                // Obtengo todas las plantillas COMPLETAS de cada uno de los clientes a partir del ID de cada cliente
                this.dataViewModel.getTemplates(PathTemplatesEnum.COMPLETES_TEMPLATES.getPath()).observe(getActivity(), templates -> {
                    if (templates != null) {
                        this.groupsClients = new ArrayList<>();

                        for (int i = 0; i < templates.size(); i++) {
                            for (int s = 0; s < clientsCodes.size(); s++) {
                                String templateCode = templates.get(i).get(0).getLinkedClientId();
                                if (templateCode.equals(this.clientsCodes.get(s))) {
                                    this.templatesOfClients.put(this.clientsName.get(s), templates.get(i));
                                    this.groupsClients.add(clientsName.get(s));
                                }
                            }
                        }
                    }

                    setDataForList();
                });
            }

            this.textZeroTemplates.setVisibility(View.VISIBLE);
            this.pbTemplatesLoad.setVisibility(View.GONE);

        });
    }

    private void setDataForList() {
        if (this.groupsClients != null && this.groupsClients.size() > 0) {

            this.listTemplatesAdapter.setGroupTittles(this.groupsClients);
            this.listTemplatesAdapter.setData(this.templatesOfClients);
            this.listTemplatesAdapter.notifyDataSetChanged();

            /*
            this.listTemplatesAdapter = new ExpandableListTemplatesAdapter(getActivity(), this.clientsName, this.templatesOfClients, this);
            this.expandableListViewTemplates.setAdapter(this.listTemplatesAdapter);
            */

            this.expandableListViewTemplates.setOnItemClickListener((adapterView, view, i, l) -> {
                Log.d("TEMPLATENAME", "Llamada");
            });

            this.pbTemplatesLoad.setVisibility(View.GONE);
            this.textZeroTemplates.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        if (this.swipeRefreshTemplates.isRefreshing()) {
            this.swipeRefreshTemplates.setRefreshing(false);
            setDataAdapter();
        }
    }

    @Override
    public void onClickTemplateClicked(String clientId, String templateId) {
        this.newReportTemplateSelectedViewModel.setTemplateSelected(clientId, templateId);
    }
}
