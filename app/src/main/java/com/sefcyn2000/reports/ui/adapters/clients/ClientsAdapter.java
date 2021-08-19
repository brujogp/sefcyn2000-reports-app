package com.sefcyn2000.reports.ui.adapters.clients;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Client;

import java.util.List;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClientViewHolder> {
    private final Context context;
    private final IClickClient iClickClient;
    private List<Client> clients;

    public ClientsAdapter(List<Client> clients, Context context, IClickClient iClickClient) {
        this.clients = clients;
        this.context = context;
        this.iClickClient = iClickClient;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View clientView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_client, parent, false);
        return new ClientViewHolder(clientView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        holder.clientName.setText(this.clients.get(position).getName());

        holder.clientProfile.setOnClickListener(view -> this.iClickClient.onClickClient(position));

        holder.counterTemplates.setText(String.valueOf(this.clients.get(position).getTemplatesCounter()));
        holder.counterVisits.setText(String.valueOf(this.clients.get(position).getVisitorCounter()));
        holder.counterReports.setText(String.valueOf(this.clients.get(position).getReportsCounter()));

        Glide.
                with(this.context).
                load(this.clients.get(position).getClientImageUrl()).
                into(holder.clientProfile);


    }

    @Override
    public int getItemCount() {
        return this.clients.size();
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        ImageView clientProfile;
        TextView clientName;
        TextView counterTemplates;
        TextView counterVisits;
        TextView counterReports;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            this.clientName = itemView.findViewById(R.id.tv_client_name);
            this.clientProfile = itemView.findViewById(R.id.iv_image_profile);
            this.counterTemplates = itemView.findViewById(R.id.tv_counter_templates);
            this.counterVisits = itemView.findViewById(R.id.tv_counter_visits);
            this.counterReports = itemView.findViewById(R.id.tv_counter_reports);
        }
    }

    public interface IClickClient {
        void onClickClient(int view);
    }
}
