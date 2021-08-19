package com.sefcyn2000.reports.ui.adapters.templates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Template;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Adapter para el listado de las plantillas que no han sido terminadas.
 * Este Adapter es para la pestalla de las plantillas incompletas en el listado de plantillas
 */
public class SimpleListIncompleteTemplatesAdapter extends RecyclerView.Adapter<SimpleListIncompleteTemplatesAdapter.SimpleIncompleteTemplatesViewHolder> {

    private List<Template> templates;
    private TemplateClicked templateClicked;
    private Context context;

    public static String DELETE_ACTION_ON_CLICK_BUTTON = "_d";
    public static String EDIT_ACTION_ON_CLICK_BUTTON = "_e";

    public SimpleListIncompleteTemplatesAdapter(Context context, List<Template> templatesOfClients, TemplateClicked templateClickedImpl) {
        this.templates = templatesOfClients;
        this.templateClicked = templateClickedImpl;
        this.context = context;

    }

    @NonNull
    @Override
    public SimpleIncompleteTemplatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_template_item_incomplate_template, parent, false);
        return new SimpleIncompleteTemplatesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleIncompleteTemplatesViewHolder holder, int position) {
        holder.tvNameTemplate.setText(this.templates.get(position).getName());
        holder.tvNotesTemplate.setText(this.templates.get(position).getNotes());
        holder.tvAddressTemplate.setText(this.templates.get(position).getAddress());
        holder.tvClientName.setText(this.templates.get(position).getClientName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        
        String date = simpleDateFormat.format(this.templates.get(position).getCreatedAt().toDate());

        holder.tvCreatedAtTemplate.setText(date);

        holder.linearLayoutContainer.setOnClickListener(v -> {
            this.templateClicked.onClickTemplateClicked(this.templates.get(position), this.EDIT_ACTION_ON_CLICK_BUTTON);
        });

        holder.ibDeleteIncompleteTemplate.setOnClickListener(v -> {
            this.templateClicked.onClickTemplateClicked(this.templates.get(position), this.DELETE_ACTION_ON_CLICK_BUTTON);
        });
    }

    @Override
    public int getItemCount() {
        return this.templates.size();
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }

    public List<Template> getTemplates() {
        return this.templates;
    }

    public class SimpleIncompleteTemplatesViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton ibDeleteIncompleteTemplate;
        TextView tvNameTemplate;
        TextView tvNotesTemplate;
        TextView tvAddressTemplate;
        TextView tvCreatedAtTemplate;
        TextView tvClientName;
        ConstraintLayout linearLayoutContainer;

        public SimpleIncompleteTemplatesViewHolder(@NonNull View itemView) {
            super(itemView);

            this.linearLayoutContainer = itemView.findViewById(R.id.linear_container_item_incomplete_template);
            this.ibDeleteIncompleteTemplate = itemView.findViewById(R.id.btn_delete_incomplete_template_item);
            this.tvNameTemplate = this.itemView.findViewById(R.id.tv_field_name_template);
            this.tvNotesTemplate = this.itemView.findViewById(R.id.tv_field_notes_template);
            this.tvAddressTemplate = this.itemView.findViewById(R.id.tv_field_address_template);
            this.tvCreatedAtTemplate = this.itemView.findViewById(R.id.tv_created_at);
            this.tvClientName = this.itemView.findViewById(R.id.tv_field_client_name);
        }
    }

    public interface TemplateClicked {
        void onClickTemplateClicked(Template template, String actionType);
    }

}
