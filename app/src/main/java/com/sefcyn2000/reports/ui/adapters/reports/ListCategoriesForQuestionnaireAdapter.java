package com.sefcyn2000.reports.ui.adapters.reports;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.textfield.TextInputEditText;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.reportscomponents.Category;
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone;

import java.util.List;

public class ListCategoriesForQuestionnaireAdapter extends RecyclerView.Adapter<ListCategoriesForQuestionnaireAdapter.CategoriesForQuestionnaireViewHolder> {
    private final Context context;
    private List<Category> map;
    private ReportListZonesOnListCategoriesAdapter.ClickZoneForReport clickZoneForReportImpl;

    public ListCategoriesForQuestionnaireAdapter(
            Context context,
            List<Category> map,
            ReportListZonesOnListCategoriesAdapter.ClickZoneForReport clickZoneForReportImpl
    ) {
        this.context = context;
        this.map = map;
        this.clickZoneForReportImpl = clickZoneForReportImpl;
    }

    @NonNull
    @Override
    public CategoriesForQuestionnaireViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.layout_report_category_list_item, parent, false);

        ConstraintLayout hiddenElements = view.findViewById(R.id.constrain_layout_rest_form);

        hiddenElements.setVisibility(View.GONE);
        ImageView buttonExpanded = view.findViewById(R.id.image_button_expandable_button);
        view.findViewById(R.id.material_card_report_category_item).setOnClickListener(v -> {

            // If the CardView is already expanded, set its visibility
            //  to gone and change the expand less icon to expand more.
            if (hiddenElements.getVisibility() == View.VISIBLE) {

                // The transition of the hiddenElements carried out
                // by the TransitionManager class.
                // Here we use an object of the AutoTransition
                // Class to create a default transition.
                AutoTransition autoTransition = new AutoTransition();
                autoTransition.setDuration(10L);

                TransitionManager.beginDelayedTransition((ConstraintLayout) view, autoTransition);

                hiddenElements.setVisibility(View.GONE);
                buttonExpanded.setImageResource(R.drawable.ic_expand_more);
            }

            // If the CardView is not expanded, set its visibility
            // to visible and change the expand more icon to expand less.
            else {

                AutoTransition autoTransition = new AutoTransition();
                autoTransition.setDuration(100L);

                TransitionManager.beginDelayedTransition((ConstraintLayout) view, autoTransition);
                hiddenElements.setVisibility(View.VISIBLE);

                buttonExpanded.setImageResource(R.drawable.ic_expand_less);
            }
        });

        return new CategoriesForQuestionnaireViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CategoriesForQuestionnaireViewHolder holder, int position) {
        holder.categoryName.setText(this.map.get(position).getCategoryName());
        holder.totalZones.setText(String.valueOf(map.get(position).getZones().size()));

        // Referencia para editar el porcentaje resuelto de la categoría desde dentro de la zona
        float c = 0;

        for (int i = 0; this.map.get(position).getZones().size() > i; i++) {
            if (this.map.get(position).getZones().get(i).isTotalResolved()) {
                c++;
            }
        }

        float totalZones = map.get(position).getZones().size();
        float result = c / totalZones * 100;

        holder.setPercentageResolvedCounter((int) result);
        holder.percentageResolved.setText(holder.getPercentageResolvedCounter() + "%");

        if (this.map.get(position).getZones() != null && this.map.get(position).getZones().size() > 0) {
            holder.setListZones(this.map.get(position).getZones());
            holder.configRecycler(position, this.clickZoneForReportImpl);
            holder.updateDataRecyclerView();
        }


        holder.observations.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                map.get(position).setObservations(s.toString());
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.map.size();
    }


    /**
     * View Holder
     */
    class CategoriesForQuestionnaireViewHolder extends RecyclerView.ViewHolder {
        int percentageResolvedCounter = 0;

        RecyclerView recyclerViewZonesList;
        TextView categoryName;
        TextView totalZones;
        TextView percentageResolved;

        TextInputEditText observations;

        List<Zone> zones;

        private ReportListZonesOnListCategoriesAdapter adapterListZones;

        public CategoriesForQuestionnaireViewHolder(@NonNull View itemView) {
            super(itemView);

            this.categoryName = itemView.findViewById(R.id.text_view_category_name);
            this.totalZones = itemView.findViewById(R.id.text_view_total_zones);
            this.percentageResolved = itemView.findViewById(R.id.text_view_percentage_resolved);
            this.recyclerViewZonesList = itemView.findViewById(R.id.recycler_view_zones);

            this.observations = itemView.findViewById(R.id.ed_observations);

            // Creo (de crear) el adaptador para la lista de zonas
            this.adapterListZones = new ReportListZonesOnListCategoriesAdapter(context);
        }

        public void configRecycler(int indexCategory, ReportListZonesOnListCategoriesAdapter.ClickZoneForReport clickZoneForReportImpl) {
            this.recyclerViewZonesList.setAdapter(this.adapterListZones);
            this.adapterListZones.setConfig(clickZoneForReportImpl, indexCategory);
            recyclerViewZonesList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        }

        public void setListZones(List<Zone> zones) {
            this.zones = zones;
        }

        public void updateDataRecyclerView() {
            if (this.zones != null && this.zones.size() > 0) {
                this.adapterListZones.setListZones(this.zones);
                this.adapterListZones.notifyDataSetChanged();
            }
        }

        public int getPercentageResolvedCounter() {
            return percentageResolvedCounter;
        }

        public void setPercentageResolvedCounter(int percentageResolvedCounter) {
            this.percentageResolvedCounter = percentageResolvedCounter;
        }
    }


}
