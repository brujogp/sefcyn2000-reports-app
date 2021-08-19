package com.sefcyn2000.reports.ui.adapters.reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.reportscomponents.Section;

import java.util.List;

/**
 * Adapter para mostrar la lista de secciones para cada una de las zonas que sí contienen secciones
 */
public class SectionsListAdapter extends RecyclerView.Adapter<SectionsListAdapter.ViewHolder> {
    private final Context context;
    List<Section> sectionsList;
    private final OnHitTakePhotoButton implOnClickTakePhotoButton;

    public SectionsListAdapter(
            Context context,
            List<Section> sectionsList,
            OnHitTakePhotoButton implOnClickTakePhotoButton
    ) {
        this.context = context;
        this.sectionsList = sectionsList;
        this.implOnClickTakePhotoButton = implOnClickTakePhotoButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.layout_report_section_item_on_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.sections.setText(this.sectionsList.get(position).getSectionName());

        holder.sections.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.linearLayoutButtonTakePhoto.setVisibility(View.VISIBLE);

                // TODO debido a que estoy usando la misma referencia a cada sección,
                //  cuando una imagen para cierta sección en una pregunta, a la siguiente pregunta, reescribo esa misma imagen
                //  si selecciono la misma sección
                holder.linearLayoutButtonTakePhoto.setOnClickListener(v -> {
                    this.implOnClickTakePhotoButton.positionOnHitPhotoButtonForSection(position, holder.ivPreview);
                });
            } else {
                holder.linearLayoutButtonTakePhoto.setVisibility(View.GONE);
                sectionsList.get(position).setUrlImage(null);
                holder.ivPreview.setImageURI(null);
                holder.ivPreview.setVisibility(View.GONE);

            }

            this.sectionsList.get(holder.getAdapterPosition()).setMarkedThisSection(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return this.sectionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPreview;
        MaterialCheckBox sections;
        LinearLayout linearLayoutButtonTakePhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.sections = itemView.findViewById(R.id.text_view_section_name);
            this.linearLayoutButtonTakePhoto = itemView.findViewById(R.id.linear_layout_button_take_photo_for_section);
            this.ivPreview = itemView.findViewById(R.id.iv_for_section_photo_preview);

        }
    }

    public void setSectionsList(List<Section> sectionsList) {
        this.sectionsList = sectionsList;
    }

    public List<Section> getSectionsList() {
        return sectionsList;
    }

    public interface OnHitTakePhotoButton {
        void positionOnHitPhotoButtonForSection(int position, ImageView ivPreview);
    }
}
