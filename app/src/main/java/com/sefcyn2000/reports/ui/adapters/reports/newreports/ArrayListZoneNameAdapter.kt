package com.sefcyn2000.reports.ui.adapters.reports.newreports

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone

class ArrayListZoneNameAdapter(
        context: Context,
        resource: Int,
        private val zoneList: List<Zone>
) : ArrayAdapter<Zone?>(context, resource, zoneList) {

    override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
    ): View {
        var v = convertView

        if (v != null) {
            (v as TextView).text = zoneList[position].zoneName
        } else {
            v = LayoutInflater.from(this.context).inflate(R.layout.simple_dropdown_item_1line, null, false)
        }

        return v!!
    }

    override fun getCount(): Int {
        return zoneList.size
    }

}