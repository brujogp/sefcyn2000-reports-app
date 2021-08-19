package com.sefcyn2000.reports.ui.adapters.reports.listreports

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.sefcyn2000.reports.R
import com.sefcyn2000.reports.data.entities.Client
import com.sefcyn2000.reports.data.entities.Report
import com.sefcyn2000.reports.data.viewmodels.communicators.CommunicationEachReportWithListReportViewModel

class ReportsListAdapter(
    private var context: Context,
    private val communicationViewModel: CommunicationEachReportWithListReportViewModel
) :
    RecyclerView.Adapter<ReportsListAdapter.ReportsViewHolder>() {

    private var clients: List<Client>? = null
    private var mapClientsReports: HashMap<Client, List<Report>>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {
        return ReportsViewHolder(
            LayoutInflater
                .from(this.context)
                .inflate(R.layout.layout_report_item_list_clients_for_reports, parent, false)
        )
    }

    override fun onBindViewHolder(holderReports: ReportsViewHolder, position: Int) {
        this.clients?.let {
            holderReports.clientName.text = this.clients!![holderReports.adapterPosition].name
            holderReports.reportsCounter.text =
                this.clients!![holderReports.adapterPosition].reportsCounter.toString()

            Glide
                .with(this.context)
                .load(this.clients!![holderReports.adapterPosition].clientImageUrl)
                .centerCrop()
                .into(holderReports.clientImage)

            holderReports.containerSummary.setOnClickListener {


                if (holderReports.recyclerViewListReports.visibility == View.GONE) {

                    val autoTransition = AutoTransition()
                    autoTransition.duration = 150L

                    TransitionManager.beginDelayedTransition(
                        holderReports.cardView as ViewGroup,
                        autoTransition
                    )

                    val listReportsFromClient: List<Report>? =
                        this.mapClientsReports?.get(this.clients!![holderReports.adapterPosition])

                    configRecyclerViewReports(
                        holderReports.recyclerViewListReports,
                        this.clients!![holderReports.adapterPosition],
                        listReportsFromClient
                    )
                } else if (holderReports.recyclerViewListReports.visibility == View.VISIBLE) {
                    val autoTransition = AutoTransition()
                    autoTransition.duration = 20L

                    TransitionManager.beginDelayedTransition(
                        holderReports.cardView as ViewGroup,
                        autoTransition
                    )

                    holderReports.recyclerViewListReports.visibility = View.GONE
                }
            }
        }
    }

    private fun configRecyclerViewReports(
        recyclerViewListReports: RecyclerView,
        client: Client,
        listReportsFromClient: List<Report>?
    ) {
        val adapter =
            ListReportsAdapter(context, client, listReportsFromClient, this.communicationViewModel)
        recyclerViewListReports.visibility = View.VISIBLE
        recyclerViewListReports.adapter = adapter

        adapter.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return this.clients?.size ?: 0
    }

    fun setData(clients: List<Client>, mapClientsReports: HashMap<Client, List<Report>>) {
        this.clients = clients
        this.mapClientsReports = mapClientsReports
    }

    class ReportsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var cardView = view

        var recyclerViewListReports: RecyclerView =
            view.findViewById(R.id.recyclerViewListReports)

        var containerSummary: ConstraintLayout =
            view.findViewById(R.id.constrain_layout_summary_reports)

        var clientName: TextView =
            view.findViewById(R.id.clientName)

        var clientImage: ImageView =
            view.findViewById(R.id.clientImage)

        var reportsCounter: TextView =
            view.findViewById(R.id.reports_counter_for_client)

    }
}
