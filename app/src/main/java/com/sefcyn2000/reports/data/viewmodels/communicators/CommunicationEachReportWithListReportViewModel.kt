package com.sefcyn2000.reports.data.viewmodels.communicators

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sefcyn2000.reports.data.entities.Client

class CommunicationEachReportWithListReportViewModel : ViewModel() {
    private val mutableLivaData: MutableLiveData<Map<String, Int>> = MutableLiveData()

    fun subjectClickedReport(): MutableLiveData<Map<String, Int>> {
        return this.mutableLivaData
    }

    /**
     * Evento disparado cuando el usuario presiona el botón de compartir
     * @param dataForReportClicked Mapa en el que la llave es el cliente y el tipo int es el identificacdor del reporte
     */
    // TODO: Debería hacer que, para no tener varios métodos de este tipo, identificar la acción desde el mismo mapa
    fun setClickReport(dataForReportClicked: Map<String, Int>) {
        this.mutableLivaData.value = dataForReportClicked
    }
}