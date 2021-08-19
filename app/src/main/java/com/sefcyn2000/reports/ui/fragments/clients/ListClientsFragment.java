package com.sefcyn2000.reports.ui.fragments.clients;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Client;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationFragmentViewModel;
import com.sefcyn2000.reports.data.viewmodels.DataViewModel;
import com.sefcyn2000.reports.ui.adapters.clients.ClientsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListClientsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ClientsAdapter.IClickClient {
    DataViewModel clientsViewModel;
    CommunicationFragmentViewModel communicationViewModel;
    TextView tvZeroClients;

    RecyclerView rvClients;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<Client> clients;
    private ClientsAdapter adapter;
    private ProgressBar pbClients;

    public ListClientsFragment() {
        super(R.layout.fragment_list_clients);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.swipeRefreshLayout = getView().findViewById(R.id.srl);
        this.rvClients = getView().findViewById(R.id.rv_clients);
        this.pbClients = getView().findViewById(R.id.pb_clients);

        this.tvZeroClients = getView().findViewById(R.id.text_zero_clients);

        this.clients = new ArrayList<>();
        this.adapter = new ClientsAdapter(this.clients, getActivity(), this);

        this.rvClients.setLayoutManager(new LinearLayoutManager(getContext()));
        this.rvClients.setAdapter(adapter);


        this.swipeRefreshLayout.setOnRefreshListener(this);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.color_primary_variant);

        this.clientsViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        this.communicationViewModel = new ViewModelProvider(getActivity()).get(CommunicationFragmentViewModel.class);

        getClients();
    }

    /*Copy*/
    @Override
    public void onRefresh() {
        getClients();
    }

    private void getClients() {
        this.clientsViewModel.getClients().observe(getViewLifecycleOwner(), clients -> {
            if (clients != null && clients.size() > 0) {
                this.pbClients.setVisibility(View.GONE);
                this.tvZeroClients.setVisibility(View.GONE);

                this.clients = clients;

                this.adapter.setClients(clients);
                this.adapter.notifyDataSetChanged();


                if (this.swipeRefreshLayout.isRefreshing()) {
                    this.swipeRefreshLayout.setRefreshing(false);
                }

            } else {
                this.pbClients.setVisibility(View.GONE);
                this.tvZeroClients.setVisibility(View.VISIBLE);
            }
        });

        this.clientsViewModel.getClients().removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onClickClient(int indexClientTouched) {
        this.communicationViewModel.selectItem(this.clients.get(indexClientTouched));
    }
}
