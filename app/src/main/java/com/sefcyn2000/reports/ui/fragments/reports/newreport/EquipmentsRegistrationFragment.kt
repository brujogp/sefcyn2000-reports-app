package com.sefcyn2000.reports.ui.fragments.reports.newreport

import android.app.Activity
import android.content.Intent
import com.sefcyn2000.reports.data.entities.Report
import com.sefcyn2000.reports.R
import android.widget.LinearLayout
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import org.xmlpull.v1.XmlPullParser
import android.util.Xml
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.sefcyn2000.reports.data.entities.reportscomponents.Equipment
import com.sefcyn2000.reports.data.viewmodels.DataViewModel
import com.sefcyn2000.reports.ui.views.FormItemEquipmentView
import com.theartofdev.edmodo.cropper.CropImage

class EquipmentsRegistrationFragment(
    private val report: Report,
    viewModelCatalog: DataViewModel,
    private val onAddOneMiniFormImpl: OnAddOneMiniFormEquipment
) :
    Fragment(R.layout.fragment_report_new_report_equipments_registration),
    FormItemEquipmentView.OnCompleteRegisterEquipment {
    lateinit var contractTakePhoto: ActivityResultLauncher<Intent>

    private var viewModel: DataViewModel = viewModelCatalog
    private var linearLayoutFormItemsContainer: LinearLayout? = null
    private var attributeSet: AttributeSet? = null

    private lateinit var intent: Intent

    private lateinit var tempEquipment: Equipment

    private lateinit var temImgView: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        linearLayoutFormItemsContainer = view.findViewById(R.id.form_item_equipment_view_container)

        this.intent = CropImage
            .activity()
            .setAspectRatio(1, 1)
            .setActivityTitle("Enfoque lo importante")
            .getIntent(requireContext());

        // Obtengo los atributos inherentes del XML de la vista personalizada
        // El error pasa porque estoy recuperando el Layout de la capa de layouts. Sin embargo, el método espera un recurso de la capa de recursos.
        //  De todos modos, esto no da problema: https://stackoverflow.com/questions/29331038/initializing-custom-view-in-android-studio-expected-resource-of-type-xml
        val pullParser: XmlPullParser =
            resources.getXml(R.layout.layout_item_form_equipments_registration)

        // Parseo el XML y lo convierto en un conjunto de atributos
        attributeSet = Xml.asAttributeSet(pullParser)

        // Añado el primer campo al contenedor
        linearLayoutFormItemsContainer!!.addView(
            createNewReport()
        )

        this.contractTakePhoto =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    if (it.data != null) {
                        val crop: CropImage.ActivityResult = CropImage.getActivityResult(it.data)

                        this.temImgView.setImageURI(crop.uri)
                        this.temImgView.visibility = View.VISIBLE
                        this.tempEquipment.urlImageEquipment = crop.uri.toString()
                    }
                }
            }
    }


    private fun createNewReport(): View {
        return FormItemEquipmentView(
            requireContext(),
            attributeSet,
            report.map,
            this.viewModel,
            this
        )
    }

    // Método gatillado cuando se termina de rellenar un miniform para el registro de equipos
    override fun onCompleteEquipment(equipment: Equipment) {
        var penultimateCheckOfDone: CheckBox? = null

        // Si existe, obtengo el penúltimo elemento
        if (linearLayoutFormItemsContainer!!.childCount >= 2) {
            penultimateCheckOfDone =
                (linearLayoutFormItemsContainer!!.getChildAt(linearLayoutFormItemsContainer!!.childCount - 2) as FormItemEquipmentView)
                    .findViewById(R.id.is_done_form)
        }

        // Obtengo el último elemento
        val lastCheckOfDone =
            (linearLayoutFormItemsContainer!!.getChildAt(linearLayoutFormItemsContainer!!.childCount - 1) as FormItemEquipmentView)
                .findViewById<CheckBox>(R.id.is_done_form)

        // Verifica si uno o más miniforms para el registro de equipos existen para crear un segundo o enesimo según sea el caso
        if (
            (linearLayoutFormItemsContainer!!.childCount >= 2 && penultimateCheckOfDone!!.isChecked && lastCheckOfDone.isChecked) ||
            ((lastCheckOfDone.isChecked))
        ) {
            // Checo si el ultimo elemento ya esta finado
            lastCheckOfDone.isChecked = true

            // En el caso de que ya esté rellenado, añado un nuevo miniform para otro reporte
            linearLayoutFormItemsContainer!!.addView(
                createNewReport()
            )

            // Encuentro la zona que se llama con el nombre y lo añado al listado de equipos
            // TODO Tengo que hacer que, por lo menos, cada una de las zonas tenga un ID único para que no exista ningún conflicto.
            this.report.map.forEach {
                for (z in it.zones) {
                    if (z.zoneName.equals(equipment.zoneName)) {
                        z.addEquipment(equipment)
                    }
                }
            }

            // Hago que si, por lo menos hay un miniform para registro de equipos añadido y completo, se active el botón guardar
            this.onAddOneMiniFormImpl.onDoneMiniForm()
        }
    }

    override fun onButtonTakePhotoClicked(equipment: Equipment, imgView: ImageView) {
        this.tempEquipment = equipment
        this.temImgView = imgView
        this.contractTakePhoto.launch(this.intent)
    }

    interface OnAddOneMiniFormEquipment {
        fun onDoneMiniForm();
    }
}