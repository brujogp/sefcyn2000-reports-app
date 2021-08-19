package com.sefcyn2000.reports.ui.adapters.reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone;

import java.util.List;

public class ReportListZonesOnListCategoriesAdapter extends RecyclerView.Adapter<ReportListZonesOnListCategoriesAdapter.ZoneItemViewHolder> {
    private int indexCategoryParent;
    private Context context;
    private List<Zone> zones;
    private ClickZoneForReport clickZoneForReportImpl;


    public ReportListZonesOnListCategoriesAdapter(Context context) {
        this.context = context;


    }

    public void setConfig(ClickZoneForReport clickZoneForReportImpl, int indexCategoryParent) {
        this.clickZoneForReportImpl = clickZoneForReportImpl;
        this.indexCategoryParent = indexCategoryParent;
    }

    @NonNull
    @Override
    public ZoneItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.layout_report_zone_name_to_questionnaire, parent, false);
        return new ZoneItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneItemViewHolder holder, int position) {
        if (this.zones != null && this.zones.size() > 0) {
            holder.zoneName.setText(this.zones.get(position).getZoneName());

            if (this.zones.get(position).isTotalResolved()) {
                holder.zoneName.setOnClickListener(null);
                holder.indicatorCheck.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_check));
                holder.zoneName.setAlpha(.5f);
            } else {
                holder.zoneName.setOnClickListener(v -> {
                    this.clickZoneForReportImpl.onClickZone(v, this.indexCategoryParent, this.zones.get(position));
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.zones.size();
    }

    public void setListZones(List<Zone> zones) {
        this.zones = zones;
    }

    static class ZoneItemViewHolder extends RecyclerView.ViewHolder {
        TextView zoneName;
        ImageView indicatorCheck;

        public ZoneItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.zoneName = itemView.findViewById(R.id.text_view_zone_name);
            this.indicatorCheck = itemView.findViewById(R.id.indicator_check);
        }
    }

    public interface ClickZoneForReport {
        void onClickZone(View v, int indexCategory, Zone zone);
    }
}
