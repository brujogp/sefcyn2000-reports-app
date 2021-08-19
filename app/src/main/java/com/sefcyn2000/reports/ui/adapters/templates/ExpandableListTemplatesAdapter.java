package com.sefcyn2000.reports.ui.adapters.templates;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Template;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter para el "filtrado" por clientes de las plantillas
 * Este Adapter es para el Fragment de las plantillas que ya están termiandas
 */
public class ExpandableListTemplatesAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private List<String> groupTittles;
    private HashMap<String, List<Template>> data;
    private final TemplateClicked templateClickedImpl;

    public ExpandableListTemplatesAdapter(Context context, List<String> groupTittles, HashMap<String, List<Template>> data, TemplateClicked templateClicked) {
        this.context = context;
        this.groupTittles = groupTittles;
        this.data = data;
        this.templateClickedImpl = templateClicked;
    }


    @Override
    public int getGroupCount() {
        return this.groupTittles.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.data.get(this.groupTittles.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.groupTittles.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childrenPosition) {
        return this.data.get(this.groupTittles.get(groupPosition)).get(childrenPosition);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        String nameGroup = (String) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.layout_tittle_client_templates, null);
        }
        TextView tittle = view.findViewById(R.id.tv_tittle_parent_group_templates);

        tittle.setText(nameGroup);

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childrenPosition, boolean b, View view, ViewGroup viewGroup) {
        Template childTemplate = (Template) getChild(groupPosition, childrenPosition);

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.layout_template_item_template, null);

            view.setOnClickListener(viewClicked -> {
                String clientId = ((TextView) viewClicked.findViewById(R.id.tv_client_id)).getText().toString();
                String templateId = ((TextView) viewClicked.findViewById(R.id.tv_template_id)).getText().toString();

                this.templateClickedImpl.onClickTemplateClicked(clientId, templateId);
            });
        }

        TextView tvName = view.findViewById(R.id.tv_field_name_template);
        TextView tvNotes = view.findViewById(R.id.tv_field_notes_template);
        TextView tvAddress = view.findViewById(R.id.tv_field_address_template);
        TextView tvCountUses = view.findViewById(R.id.tv_field_count_use_template);
        TextView tvCreatedAt = view.findViewById(R.id.tv_created_at);
        TextView tvClientId = view.findViewById(R.id.tv_client_id);
        TextView tvTemplateId = view.findViewById(R.id.tv_template_id);

        tvName.setText(childTemplate.getName());
        tvNotes.setText(childTemplate.getNotes());
        tvAddress.setText(childTemplate.getAddress());
        tvCountUses.setText(String.valueOf(childTemplate.getUsedCount()));
        Date date = childTemplate.getCreatedAt().toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        tvCreatedAt.setText(simpleDateFormat.format(date));
        tvTemplateId.setText(childTemplate.getTemplateId());
        tvClientId.setText(childTemplate.getLinkedClientId());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public HashMap<String, List<Template>> getData() {
        return data;
    }

    public void setData(HashMap<String, List<Template>> data) {
        this.data = data;
    }

    public void setGroupTittles(List<String> groupTittles) {
        this.groupTittles = groupTittles;
    }

    public interface TemplateClicked {
        void onClickTemplateClicked(String clienteId, String templateId);
    }
}
