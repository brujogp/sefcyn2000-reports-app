package com.sefcyn2000.reports.ui.views

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.sefcyn2000.reports.R
import com.sefcyn2000.reports.data.entities.reportscomponents.Category
import com.sefcyn2000.reports.data.entities.reportscomponents.Equipment
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone
import com.sefcyn2000.reports.data.viewmodels.DataViewModel
import com.sefcyn2000.reports.ui.fragments.reports.newreport.EquipmentsRegistrationFragment
import com.theartofdev.edmodo.cropper.CropImage
import kotlin.collections.ArrayList

class FormItemEquipmentView(
    context: Context,
    attrs: AttributeSet?,
    map: List<Category>,
    viewModel: DataViewModel,
    onCompleteImpl: OnCompleteRegisterEquipment
) : ConstraintLayout(context, attrs) {
    private var checkIsDone: CheckBox
    private var onCompleteImpl: OnCompleteRegisterEquipment
    private val map: List<Category>


    private var actionOnEquipmentList: List<String> = ArrayList()
    private var listEquipmentType: List<String> = ArrayList()
    private var zoneListName = ArrayList<String>();

    private val zoneList: List<Zone>

    private val tvCategoryName: TextView

    private val autoCompleteEquipmentName: AutoCompleteTextView
    private val autoCompleteEquipmentLocation: AutoCompleteTextView
    private val autoCompleteEquipmentAction: AutoCompleteTextView

    private val btnTakeImgEquipment: LinearLayout
    private val imgViewPreviewEquipment: ImageView

    private var equipment: Equipment = Equipment();

    init {

        val parentView = inflate(context, R.layout.layout_item_form_equipments_registration, this)
        this.onCompleteImpl = onCompleteImpl

        tvCategoryName = parentView.findViewById(R.id.tv_category_name_of_location)

        autoCompleteEquipmentName = parentView.findViewById(R.id.autocomplete_equipment_name)
        autoCompleteEquipmentAction = parentView.findViewById(R.id.autocomplete_action_on_equipment)

        autoCompleteEquipmentLocation =
            parentView.findViewById(R.id.autocomplete_equipment_location)

        checkIsDone = parentView.findViewById(R.id.is_done_form)

        btnTakeImgEquipment = parentView.findViewById(R.id.btn_take_image_equipment)
        imgViewPreviewEquipment = parentView.findViewById(R.id.img_preview_equipment)

        this.map = map

        // Una lista de cada una de las zonas
        zoneList = ArrayList()

        // Obtengo todas las zonas de cada una de las categorías
        for (c in map) {
            zoneList.addAll(c.zones)
        }

        // Obtengo el nombre de cada una de las zonas
        this.zoneList.forEach {
            this.zoneListName.add(it.zoneName)
        }

        // Obtengo el catálogo de la lista de equipos
        viewModel.equipmentNameList.observe((context as LifecycleOwner), {
            this.listEquipmentType = it

            this.autoCompleteEquipmentName.setAdapter(
                ArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    it
                )
            )

        })

        // Recupero el catálogo de acciones para los equipos
        viewModel.actionOnEquipment.observe((context as LifecycleOwner), {
            this.actionOnEquipmentList = it

            this.autoCompleteEquipmentAction.setAdapter(
                ArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    it
                )
            )
        })

        this.autoCompleteEquipmentLocation.setAdapter(
            ArrayAdapter(
                this.context,
                android.R.layout.simple_dropdown_item_1line,
                this.zoneListName
            )
        )

        setterEventOnAutoComplete()
    }

    private fun setterEventOnAutoComplete() {
        this.autoCompleteEquipmentName.setOnItemClickListener { parent, view, position, id ->
            run {
                checkImage()
                equipment.name = (view as TextView).text.toString()
                checkIfIsDone()
            }
        }

        this.autoCompleteEquipmentAction.setOnItemClickListener { parent, view, position, id ->
            run {
                checkImage()
                equipment.action = (view as TextView).text.toString()
                checkIfIsDone()
            }
        }

        this.autoCompleteEquipmentLocation.setOnItemClickListener { parent, view, position, id ->
            run {
                checkImage()
                val categoryNameSelected: String =
                    getCategoryName((view as TextView).text.toString())

                equipment.categoryName = categoryNameSelected
                equipment.zoneName = view.text.toString()

                tvCategoryName.text = "Categoría: $categoryNameSelected"
                checkIfIsDone()
            }
        }

        this.btnTakeImgEquipment.setOnClickListener {
            this.onCompleteImpl.onButtonTakePhotoClicked(equipment, this.imgViewPreviewEquipment)
        }
    }

    // TODO: Parche feo...
    private fun checkImage() {
        if (this.equipment.urlImageEquipment.isEmpty()) {
            AlertDialog.Builder(this.context)
                .setMessage("Seleccione una imagen")
                .setPositiveButton("Está bien") { dialog, which ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun checkIfIsDone() {
        if (
            equipment.name.isNotEmpty() &&
            equipment.categoryName.isNotEmpty() &&
            equipment.action.isNotEmpty() &&
            equipment.zoneName.isNotEmpty() &&
            equipment.urlImageEquipment.isNotEmpty()
        ) {
            this.checkIsDone.isChecked = true
            onCompleteImpl.onCompleteEquipment(equipment)
        }
    }

    private fun getCategoryName(zoneName: String): String {
        var categoryName: String? = null

        map.forEach {

            for (z in it.zones) {
                if (z.zoneName == zoneName) {

                    categoryName = it.categoryName

                }
            }

        }

        return categoryName!!
    }

    interface OnCompleteRegisterEquipment {
        fun onCompleteEquipment(equipment: Equipment)

        fun onButtonTakePhotoClicked(equipment: Equipment, imgView: ImageView)
    }
}