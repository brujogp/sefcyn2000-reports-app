package com.sefcyn2000.reports.ui.fragments.reports.newreport;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Report;
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationFragmentViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralDataReportFragment extends Fragment {
    private Report report;
    private TextView tvCurrentDate;
    CommunicationFragmentViewModel communicationFragmentViewModel;
    private TextInputEditText tvReportName;
    private TextInputEditText tvReportTechnicalName;
    private TextInputEditText tvReportServiceConfirmedWith;
    private TextInputEditText tvReportReceivedBy;
    private MaterialCheckBox cbIsExtraVisit;

    public GeneralDataReportFragment() {
        super(R.layout.fragment_report_new_report_step_general_data);
        this.report = new Report();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        this.tvCurrentDate = view.findViewById(R.id.tv_current_date);
        this.communicationFragmentViewModel = new ViewModelProvider(getActivity()).get(CommunicationFragmentViewModel.class);

        this.tvReportName = view.findViewById(R.id.et_report_name);
        this.tvReportTechnicalName = view.findViewById(R.id.et_technical_name);
        this.tvReportServiceConfirmedWith = view.findViewById(R.id.et_service_confirmed_with);
        this.tvReportReceivedBy = view.findViewById(R.id.et_service_received_by);
        this.cbIsExtraVisit = view.findViewById(R.id.cb_is_extra_visit);

        //TODO: El día en el que se finalice el reporte,
        // puede que sea diferente al día en el que se comenzó. Entonces tengo que saber qué fecha quieren que aparezca en el reporte (si un día antes o después).
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.tvCurrentDate.setText(simpleDateFormat.format(new Date()));
    }

    private void getReportGeneralData() {
        this.report.setReportName(this.tvReportName.getText().toString());
        this.report.setTechnicianName(this.tvReportTechnicalName.getText().toString());
        this.report.setReceivedBy(this.tvReportReceivedBy.getText().toString());
        this.report.setConfirmedWith(this.tvReportServiceConfirmedWith.getText().toString());
        this.report.setExtraVisit(this.cbIsExtraVisit.isChecked());
        this.report.setCreatedAt(new Timestamp(new Date()));

        this.communicationFragmentViewModel.setReportLiveData(this.report);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getReportGeneralData();
    }

}
