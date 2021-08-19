package com.sefcyn2000.reports.ui.adapters.reports.listreports

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sefcyn2000.reports.R
import com.sefcyn2000.reports.data.entities.Client
import com.sefcyn2000.reports.data.entities.Report
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationEachReportWithListReportViewModel
import java.text.SimpleDateFormat

class ListReportsAdapter(
    private val mContext: Context,
    private val clientSelected: Client,
    private var reports: List<Report>?,
    private var communicationViewModel: CommunicationEachReportWithListReportViewModel
) :
    RecyclerView.Adapter<ListReportsAdapter.ViewHolderReports>() {

    object Actions {
        const val DELETE_REPORT_ON_LIST = 42
        const val SHARE_REPORT_ON_LIST = 22
        const val SEPARATOR = "_"
        const val ZIP_REPORT_ON_LIST = 242
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderReports {
        return ViewHolderReports(
            LayoutInflater.from(this.mContext)
                .inflate(R.layout.layout_report_item_list_report, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolderReports, position: Int) {
        holder.unitName.text =
            this.reports?.get(position)?.unityName

        holder.technicNam.text =
            this.reports?.get(position)?.technicianName

        holder.receivedByName.text =
            this.reports?.get(position)?.receivedBy

        holder.numVisitCounter.text =
            (this.reports?.get(position)?.reportNum?.plus(1)).toString()

        holder.createdAt.text =
            SimpleDateFormat("dd/MM/yyyy").format(this.reports?.get(position)?.createdAt!!.toDate())

        holder.btnGeneratePdfAndShareReport.setOnClickListener {
            reportClicked(position, Actions.SHARE_REPORT_ON_LIST)
        }


        holder.btnGenerateZipReportAndShare.setOnClickListener {
            reportClicked(position, Actions.ZIP_REPORT_ON_LIST)
        }

        holder.btnGenerateDeleteReport.setOnClickListener {
            reportClicked(position, Actions.DELETE_REPORT_ON_LIST)
        }
    }

    private fun reportClicked(position: Int, action: Int) {
        // Mapa en el que se pone el código del cliente y la posición del reporte seleccionado en la llave
        // y la acción a realizar en el valor
        val clientReportPositionMap: HashMap<String, Int> = HashMap()

        clientReportPositionMap[this.clientSelected.codeClient + Actions.SEPARATOR + position] =
            action

        this.communicationViewModel.setClickReport(clientReportPositionMap)
    }

    override fun getItemCount(): Int {
        return this.reports?.size ?: 0
    }

    class ViewHolderReports(view: View) : RecyclerView.ViewHolder(view) {
        val unitName: TextView = view.findViewById(R.id.unitName)
        val technicNam: TextView = view.findViewById(R.id.technicName)
        val receivedByName: TextView = view.findViewById(R.id.receivedByName)
        val numVisitCounter: TextView = view.findViewById(R.id.numVisitCounter)
        val createdAt: TextView = view.findViewById(R.id.createdAt)

        val btnGeneratePdfAndShareReport: ImageButton =
            view.findViewById(R.id.btnGeneratePdfAndShareReport);

        val btnGenerateZipReportAndShare: ImageButton =
            view.findViewById(R.id.btnZipReport);

        val btnGenerateDeleteReport: ImageButton =
            view.findViewById(R.id.btnDeleteReport);
    }

}