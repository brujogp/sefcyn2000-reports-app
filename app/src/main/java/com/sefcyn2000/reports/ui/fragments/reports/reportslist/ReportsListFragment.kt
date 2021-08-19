package com.sefcyn2000.reports.ui.fragments.reports.reportslist

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sefcyn2000.reports.R
import com.sefcyn2000.reports.data.entities.Client
import com.sefcyn2000.reports.data.entities.Report
import com.sefcyn2000.reports.data.viewmodels.DataViewModel
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationEachReportWithListReportViewModel
import com.sefcyn2000.reports.ui.adapters.reports.listreports.ListReportsAdapter
import com.sefcyn2000.reports.ui.adapters.reports.listreports.ReportsListAdapter
import com.sefcyn2000.reports.utilities.helpers.HelperGenerateHtmlReport
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.forEach

class ReportsListFragment(private var mContext: Context, private var dataViewModel: DataViewModel) :
    Fragment(R.layout.fragment_report_list_reports), HelperGenerateHtmlReport.OnCreateReport {

    private lateinit var swipeRefresh: SwipeRefreshLayout

    private var listClients: ArrayList<Client> = ArrayList()
    private var clientListReportsMap: HashMap<Client, List<Report>> =
        HashMap<Client, List<Report>>()

    private lateinit var communicationEachReportWithListReportViewModel: CommunicationEachReportWithListReportViewModel
    private lateinit var textZeroReports: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportsListAdapter

    private lateinit var progressDialog: ProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.communicationEachReportWithListReportViewModel =
            ViewModelProvider(requireActivity()).get(CommunicationEachReportWithListReportViewModel::class.java)

        this.recyclerView = view.findViewById(R.id.recyclerViewListClientsAndReports)
        this.progressBar = view.findViewById(R.id.progressBarReports)
        this.textZeroReports = view.findViewById(R.id.text_zero_reports)

        this.adapter = ReportsListAdapter(mContext, communicationEachReportWithListReportViewModel)

        this.recyclerView.adapter = this.adapter

        this.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        this.swipeRefresh = view.findViewById(R.id.swipeRefreshLayout_Reports)

        swipeRefresh.setOnRefreshListener {
            this.listClients = ArrayList()
            this.clientListReportsMap = HashMap()
            setDataOnRecyclerView()
        }

        this.progressDialog = ProgressDialog(requireContext())

        setDataOnRecyclerView()

        // Creo un observer para los botónes de cada uno de los reportes
        communicationEachReportWithListReportViewModel.subjectClickedReport()
            .observe(viewLifecycleOwner) {

                it?.let {
                    /*
                    this.progressDialog.setMessage("Generando reporte")
                    this.progressDialog.setCanceledOnTouchOutside(false)
                    this.progressDialog.create()
                    this.progressDialog.show()
                    */

                    for (entry in it.entries) {

                        val values: List<String> =
                            entry.key.split(ListReportsAdapter.Actions.SEPARATOR)

                        val clientId = values[0]
                        val reportIndex = Integer.parseInt(values[1])
                        val action: Int = entry.value

                        Log.d("TEST-T", "Client id: $clientId")
                        Log.d("TEST-T", "Index report: $reportIndex")
                        Log.d("TEST-T", "Action: $action")

                        /*
                        val clientSelected = this.clientListReportsMap[entry.key]
                        val report: Report = clientSelected!![entry.value]

                        val helperGenerateReport =
                            HelperGenerateHtmlReport(requireContext(), this)
                        helperGenerateReport.generateReport(report)
                        */
                    }
                }
            }
    }

    private fun setDataOnRecyclerView() {
        // Obtengo el mapa de clientes y reportes
        this.dataViewModel.reportsWithClients.observe(viewLifecycleOwner) {
            if (it?.size!! > 0) {

                // Obtengo solos los clientes y los añado a una lista
                it.entries.forEach { objClient ->
                    if (!listClients.contains(objClient.key)) {
                        listClients.add(objClient.key)
                    }
                }

                // Asigno el mapa a una variable miembro
                this.clientListReportsMap = it as HashMap<Client, List<Report>>
                progressBar.visibility = View.GONE

                this.adapter.setData(this.listClients, this.clientListReportsMap)
                this.adapter.notifyDataSetChanged()

                if (this.swipeRefresh.isRefreshing) {
                    this.swipeRefresh.isRefreshing = false
                }
            } else {
                textZeroReports.visibility = View.VISIBLE
                progressBar.visibility = View.GONE

                if (this.swipeRefresh.isRefreshing) {
                    this.swipeRefresh.isRefreshing = false
                }
            }
        }

    }

    override fun reportCreated() {
        this.progressDialog.hide()
        this.progressDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.communicationEachReportWithListReportViewModel.subjectClickedReport()
            .removeObservers(requireActivity())
    }
}