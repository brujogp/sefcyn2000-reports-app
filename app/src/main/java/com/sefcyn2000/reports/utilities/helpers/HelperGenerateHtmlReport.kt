package com.sefcyn2000.reports.utilities.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.print.PrintAttributes
import android.print.PrintAttributes.Resolution
import androidx.core.content.FileProvider
import com.google.firebase.Timestamp
import com.sefcyn2000.reports.data.entities.Report
import com.sefcyn2000.reports.data.entities.reportscomponents.Category
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone
import com.uttampanchasara.pdfgenerator.CreatePdf
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import java.util.concurrent.Executors

class HelperGenerateHtmlReport(private val context: Context, impl: OnCreateReport) {
    private var report: Report? = null
    var onCreateReportImpl: OnCreateReport = impl

    private val STRING_STYLE_IMG_EQUIPMENT = "background-image: linear-gradient(\n" +
            "        to bottom,\n" +
            "        transparent,\n" +
            "        rgba(255, 255, 255, 0),\n" +
            "        rgba(255, 255, 255, .1),\n" +
            "        rgba(255, 255, 255, .6),\n" +
            "        rgba(255, 255, 255, .9),\n" +
            "        rgba(255, 255, 255, 1)\n" +
            "      ), url(\""

    private val STRING_STYLE_CHARACTERS_END = "\");"

    fun generateReport(report: Report): File? {
        this.report = report

        // Creo un ejecutor para un segundo hilo
        val executorService = Executors.newSingleThreadExecutor()

        // Creo un Handler para la escucha del progreso del segundo hilo
        val handler = Handler(Looper.getMainLooper())

        executorService.execute {
            try {
                // Descargo la plantilla HTML
                val templateHtml =
                    Jsoup.connect("https://soyjoctan.com/clients/sefcyn2000/templates/classic/classic-template.html")
                        .get()

                // Posteo el progreso del hilo
                handler.post {

                    val html = printInfoOnHtml(templateHtml, report)

                    // Creo un archivo HTML
                    /*
                    File htmlReport = new File(context.getFilesDir().getPath(), report.getReportName().trim().replace(" ", "") + ".html");

                    try {

                        OutputStream outputStream = new FileOutputStream(htmlReport);
                        outputStream.write(html.getBytes());
                        outputStream.close();

                        Log.d("TEST-T", context.getFilesDir().getPath() + "/" + report.getReportName().trim().replace("", ""));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                     */

                    CreatePdf(context).setPageSize(defaultPrintAttrs.mediaSize!!)
                        .openPrintDialog(false)
                        .setContentBaseUrl(null)
                        .setPdfName(report.reportName.replace(" ", "-"))
                        .setContent(html)
                        .setFilePath(
                            this.context.filesDir.absolutePath + "/reports/"
                        )
                        .setCallbackListener(object : CreatePdf.PdfCallbackListener {
                            override fun onFailure(errorMsg: String) {}
                            override fun onSuccess(filePath: String) {
                                onCreateReportImpl.reportCreated()

                                val sendReportIntent = Intent()

                                val fileUri: Uri = FileProvider.getUriForFile(
                                    context,
                                    context.applicationContext.packageName + ".provider",
                                    File(
                                        filePath
                                    )
                                )

                                sendReportIntent.action = Intent.ACTION_SEND
                                sendReportIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                sendReportIntent.type = "application/pdf"
                                sendReportIntent.putExtra(
                                    Intent.EXTRA_STREAM,
                                    fileUri
                                )

                                context.startActivity(
                                    Intent.createChooser(
                                        sendReportIntent,
                                        "Compartiendo reporte"
                                    )
                                )

                            }
                        }).create()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun printInfoOnHtml(templateHtml: Document, report: Report): String {
        // Obtienes el mes actual
        var monthName: String? = null
        monthName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Obtengo el mes
            val mes = LocalDate.now().month

            // Obtienes el nombre del mes
            val tempMonthName =
                mes.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
            val firstLetter = tempMonthName[0].toString().toUpperCase()
            val anotherString = tempMonthName.replace(tempMonthName[0].toString(), "")
            firstLetter + anotherString + " del " + LocalDate.now().year
        } else {
            SimpleDateFormat("dd/MM/yyyy").format(Date())
        }

        templateHtml.getElementsByClass("date-header").first().appendText(monthName)

        // Imprimo el nombre de la unidad
        templateHtml.getElementsByClass("unit-name").first().appendText(report.unityName)

        // Se rellenan los campos de la primera visita si el número es par
        if (report.reportNum % 2 == 0) {
            templateHtml.getElementById("first-visit-date-info")
                .appendText(SimpleDateFormat("dd/MM/yyyy").format(report.finishedAt.toDate()))
            templateHtml.getElementById("first-visit-received-by-info")
                .appendText(report.receivedBy)
            templateHtml.getElementById("first-visit-confirmed-with-info")
                .appendText(report.confirmedWith)
            templateHtml.getElementById("first-visit-technical-name-info")
                .appendText(report.technicianName)
            templateHtml.getElementById("first-visit-schedule-info")
                .appendText(getSchedule(report.createdAt, report.finishedAt))
        } else {
            templateHtml.getElementById("second-visit-date-info")
                .appendText(SimpleDateFormat("dd/MM/yyyy").format(report.finishedAt.toDate()))
            templateHtml.getElementById("second-visit-received-by-info")
                .appendText(report.receivedBy)
            templateHtml.getElementById("second-visit-confirmed-with-info")
                .appendText(report.confirmedWith)
            templateHtml.getElementById("second-visit-technical-name-info")
                .appendText(report.technicianName)
            templateHtml.getElementById("second-visit-schedule-info")
                .appendText(getSchedule(report.createdAt, report.finishedAt))
        }

        // Hago que se genere la vista panorámica del mapa del inmueble (el alcance de servicio)
        generateOverViewerMap(templateHtml)

        // hago que se imprima el contenido del cuestionario
        printQuestionnaire(templateHtml)

        // Registro todos los equipos
        printEquipments(templateHtml)


        return templateHtml.html()
    }

    private fun printEquipments(templateHtml: Document) {

        val equipmentParentContainer =
            getElementWithClasses("div", listOf("equipments-parent-container"))

        equipmentParentContainer.appendChild(
            getElementWithClasses(
                "p",
                listOf("report-equipments-header")
            ).appendText("Equipos en comodato")
        )

        val equipmentsContainer = getElementWithClasses("div", listOf("equipments-container"))

        for (category in report!!.map) {
            for (zone in category.zones) {
                if (zone.equipmentList != null && zone.equipmentList.size > 0) {

                    for (equipment in zone.equipmentList) {
                        val equipmentItem =
                            getElementWithClasses("div", listOf("equipment", "box-shadow"))

                        val imgElement = getElementWithClasses("div", listOf("img-equipment"))
                        imgElement.attr(
                            "style",
                            STRING_STYLE_IMG_EQUIPMENT + equipment.urlImageEquipment + STRING_STYLE_CHARACTERS_END
                        )
                        equipmentItem.appendChild(imgElement)

                        equipmentItem.appendChild(
                            getElementWithClasses(
                                "p",
                                listOf("equipment-name", "text-bold")
                            ).appendText(equipment.name)
                        )

                        equipmentItem.let {
                            val equipmentActionContainer = getElementWithClasses(
                                "div",
                                listOf("equipment-action-container", "equipment-info-container")
                            )

                            equipmentActionContainer.appendChild(Element("p").appendText("Acción:"))
                            equipmentActionContainer.appendChild(
                                getElementWithClasses(
                                    "p",
                                    listOf("text-bold")
                                ).appendText(equipment.action)
                            )

                            it.appendChild(equipmentActionContainer)
                        }


                        equipmentItem.let {
                            val equipmentLocationContainer = getElementWithClasses(
                                "div",
                                listOf("equipment-location-container", "equipment-info-container")
                            )

                            equipmentLocationContainer.appendChild(Element("p").appendText("Úbicación:"))
                            equipmentLocationContainer.appendChild(
                                getElementWithClasses(
                                    "p",
                                    listOf("text-bold")
                                ).appendText(equipment.zoneName)
                            )

                            it.appendChild(equipmentLocationContainer)
                        }

                        equipmentsContainer.appendChild(equipmentItem)
                    }
                }
            }
        }

        equipmentParentContainer.appendChild(equipmentsContainer)

        templateHtml.appendChild(equipmentParentContainer)
    }


    // Genera la primera parte
    private fun generateOverViewerMap(html: Document) {
        // El contenedor de todas las categorías. Este contenedor será el mismo siepre
        val categoriesContainer = html.getElementsByClass("map-description-container").first()

        // Por alguna razón que desconozco, no puedo eliminar este elemento. Por lo tanto, solo lo escondo
        html.getElementById("category-for-design").addClass("hidden-element")

        // Itero sobre el mapa
        for (category in report!!.map) {
            // Creo el contenedor de toda la categoría
            val categoryContainer = Element("div").addClass("category-container")

            // Añado el elemento que tendrá que nombre de la categoría
            categoryContainer.appendChild(
                Element("p").addClass("category-name").appendText(category.categoryName)
            )

            // Creo el listado de las zonas
            val zonesList = Element("div").addClass("zones-list")

            for (zone in category.zones) {
                // Añado el elemento que tendrá que nombre de cada una de las zonas
                zonesList.appendChild(Element("p").addClass("zone-name").appendText(zone.zoneName))
            }

            // Añado el listado de las zonas
            categoryContainer.appendChild(zonesList)

            // Añado la categoría recien creada
            categoriesContainer.appendChild(categoryContainer)
        }
    }

    private fun getSchedule(createdAt: Timestamp, finishedAt: Timestamp): String {
        val firstSimpleDateFormat = SimpleDateFormat("HH:mm").format(createdAt.toDate())
        val secondSimpleDateFormat = SimpleDateFormat("HH:mm").format(finishedAt.toDate())
        return "$firstSimpleDateFormat - $secondSimpleDateFormat"
    }


    // Imprime el cuestionario por completo
    private fun printQuestionnaire(templateHtml: Document) {
        // Remuevo todas las clases de prueba
        for (e in templateHtml.getElementsByClass("for-design")) {
            e.addClass("hidden-element")
        }

        // Itero sobre el mapa
        for (category in report!!.map) {
            // Creo el contenedor de toda la categoría
            val categoryContainer =
                Element("div").addClass("report-category-container").addClass("container")

            // Obtengo la cabecerá de la categoría y la añado
            categoryContainer.appendChild(printCategoryHeader(category))

            // Recorro las zonas de la categoría para generar un report-zone-item
            for (z in category.zones) {

                // Creo un elemento zona por cada iteración y lo añado al contenedor de la categoría actual
                categoryContainer.appendChild(printZoneItem(z))

            }

            // Añado la nueva categoría completa al documento
            templateHtml.appendChild(categoryContainer)
        }

    }

    private fun printCategoryHeader(category: Category): Element {
        // Creo el contenedor de la tabla de los datos generales de la categoría
        val reportCategoryHeader = Element("div").addClass("report-category-header")

        val flagCounterVisit =
            Element("p").addClass("report-flag-count-visit").addClass("border")

        // Imprimo si la visita es la primera o segunda de la ronda de visitas
        if ((report!!.reportNum % 2) == 0) {
            flagCounterVisit.appendText("Visita inicial")
        } else {
            flagCounterVisit.appendText("Visita de seguimiento")
        }
        reportCategoryHeader.appendChild(flagCounterVisit)

        // Añado el elemento que tiene el nombre de la categoría
        reportCategoryHeader.appendChild(
            Element("p").addClass("report-category-name").appendText(category.categoryName)
        )

        reportCategoryHeader.appendChild(
            Element("p").appendText("Realizada con:").addClass("text-made-with").addClass("border")
        )

        reportCategoryHeader.appendChild(
            Element("p").appendText("Inicio de mes").addClass("text-start-month").addClass("border")
        )

        reportCategoryHeader.appendChild(
            Element("p").appendText("Seguimiento").addClass("text-next-visit")
                .addClass("border")
        )

        // Añado el nombre de con quién se hizo el primero recorrido
        // TODO: Después se cambiará de "star" a "start"
        reportCategoryHeader.appendChild(
            Element("p").appendText(report!!.confirmedWith).addClass("text-name-star-month")
                .addClass("border")
        )

        reportCategoryHeader.appendChild(
            Element("p").appendText(report!!.confirmedWith).addClass("text-name-next-visit")
                .addClass("border")
        )

        // Si hay observaciones para la categoría y si no están vacíos, los imprime
        category.observations?.let {
            if (it.isNotEmpty()) {
                // Añado el texto "Observaciones:"
                reportCategoryHeader.appendChild(
                    Element("p").addClass("text-observations-category").addClass("border")
                        .appendText("Observaciones:")
                )

                reportCategoryHeader.appendChild(
                    Element("p").addClass("text-observations-category-content").addClass("border")
                        .appendText(category.observations)
                )
            }
        }

        return reportCategoryHeader
    }

    fun printZoneItem(zone: Zone): Element {
        // Creo el contenedor de la zona
        val zoneItemContainer = Element("div").addClass("report-zone-item")

        // Creo el contenedor para el nombre de la zona y las observaciones
        val zoneNameContainer = Element("div").addClass("report-zone-name-item")

        // Añado el nombre
        zoneNameContainer.appendChild(
            Element("p").addClass("text-zone-name-item").appendText(zone.zoneName)
        )

        // Añado el contenedor del nombre de la zona
        zoneItemContainer.appendChild(zoneNameContainer)

        // Añado la primera hoja de cuestionario
        zoneItemContainer.appendChild(printQuestionnaireSheets(zone))

        return zoneItemContainer
    }

    private fun printQuestionnaireSheets(zone: Zone): Element {
        // Creo el contenedor de las hojas de cuestionario
        val questionnaireContainerSheets = Element("div").addClass("container-questionnaire-sheets")

        for (questionnaire in zone.questionnairePageList) {
            // Creo para cada una de las hojas de cuestionario
            val questionnaireSheetItem = Element("div").addClass("report-sheet-item")

            // Añado la pregunta
            questionnaireSheetItem.appendChild(
                Element("p").addClass("question").appendText(questionnaire.question.question)
            )

            // Creo el elemento para la respuesta y sus elementos
            questionnaireSheetItem.appendChild(
                getElementWithThreeRows(
                    listOf(
                        "report-question-container",
                        "grid-1-3-for-headers"
                    ),
                    "Respuesta:",
                    if (questionnaire.isAffirmativeAnswer) "Sí" else "No",
                    questionnaire.answerQuestionPhotoUrl
                )
            )

            // Si no es null el listado de secciones marcadas
            questionnaire.markedSectionsList?.let {

                // Creo el contenedor para las secciones si es que existen
                val sectionsContainer = Element("div").addClass("report-sections-container")

                // Añado el texto
                sectionsContainer.appendChild(
                    Element("p").addClass("text-where").appendText("¿En dónde?")
                )

                if (it.size > 0) {
                    for (section in it) {
                        if (section.isMarkedThisSection) {
                            // Creo el contenedor de la sección
                            val sectionContainer = Element("div").addClass("section")

                            // Añado el nombre de la sección
                            sectionContainer.appendChild(
                                getElementWithClasses(
                                    "p",
                                    listOf("section-name", "text-bold")
                                ).appendText(section.sectionName)
                            )

                            // Añado la imágen de la sección
                            sectionContainer.appendChild(
                                getElementWithClasses(
                                    "img",
                                    listOf("img-square", "img-section")
                                ).attr("src", section.urlImage)
                            )

                            // Añado el contenedor de una sección al contenedor completo de secciones
                            sectionsContainer.appendChild(sectionContainer)
                        }
                    }
                }

                // Añado el contenedor de secciones a la hoja de cuestionario
                questionnaireSheetItem.appendChild(sectionsContainer)
            }

            // Creo el contenedor para el área de oportunidad y sus elementos
            questionnaireSheetItem.appendChild(
                getElementWithThreeRows(
                    listOf(
                        "report-opportunity-area-container",
                        "grid-1-3-for-headers"
                    ),
                    "Área de oportunidad",
                    questionnaire.opportunityArea,
                    questionnaire.opportunityAreaPhotoUrl
                )
            )

            // Creo el contenedor para la causa raíz y sus elementos
            questionnaireSheetItem.appendChild(
                getElementWithThreeRows(
                    listOf(
                        "report-root-cause-container",
                        "grid-1-3-for-headers"
                    ),
                    "Causa raíz",
                    questionnaire.rootCause,
                    questionnaire.rootCausePhotoUrl
                )
            )

            // Creo el contenedor para "action of supplier" y "action of client"
            val containerForActions =
                Element("div").addClass("report-action-for-client-and-supplier-container")

            // Si la lista de secciones no es nula y tiene un elemento o más
            questionnaire.markedSectionsList?.let {
                if (it.size > 0) {
                    containerForActions.addClass("new-line-on-grid")
                }
            }

            containerForActions.appendChild(
                generateContainerForAction(
                    listOf(
                        "action-for-supplier-container"
                    ),
                    "Acción para el proveedor:",
                    questionnaire.actionToSupplier,
                    listOf("action-for-supplier-text", "m-b--5"),
                    listOf("action-for-supplier", "text-bold")
                )
            )

            containerForActions.appendChild(
                generateContainerForAction(
                    listOf(
                        "action-for-client-container",
                        "m-t-1"
                    ),
                    "Acción para el cliente:",
                    questionnaire.actionToClient,
                    listOf("action-for-client-text", "m-b--5"),
                    listOf("action-for-client", "text-bold")
                )
            )
            questionnaireSheetItem.appendChild(containerForActions)

            if (questionnaire.observations.isNotEmpty()) {
                questionnaireSheetItem.appendChild(
                    generateContainerForAction(
                        listOf("container-observations-for-questionnaire-sheet"),
                        "Observaciones:",
                        questionnaire.observations,
                        listOf("text-bold"),
                        listOf("m-t-1")
                    )
                )
            }

            // Añado la hoja generada al contenedor
            questionnaireContainerSheets.appendChild(questionnaireSheetItem)
        }

        return questionnaireContainerSheets
    }

    private fun generateContainerForAction(
        containerClasses: List<String>,

        tittle: String,
        value: String?,

        firstElementClasses: List<String>,
        secondElementClasses: List<String>
    ): Element {
        val container = getElementWithClasses("div", containerClasses)

        container.appendChild(getElementWithClasses("p", firstElementClasses).appendText(tittle))
        container.appendChild(getElementWithClasses("p", secondElementClasses).appendText(value))

        return container
    }

    /**
     * <p>Este método solamente se usa para crear los primeros 3 elementos de cada una de las hojas de cuestionario:</p>
     */
    private fun getElementWithThreeRows(
        containerClasses: List<String>,
        tittle: String,
        textValue: String,
        uri: String?,
    ): Element {
        // Creo el contenedor de la sección
        val container = getElementWithClasses(
            "div",
            containerClasses
        )

        // Añado el primer elemento de la hoja de cuestionario (la pregunta, la respuésta y su imagen)
        container.apply {
            // Pongo el título
            this.appendChild(Element("p").appendText(tittle))

            // Imprimo el valor para el título
            this.appendChild(
                getElementWithClasses(
                    "p",
                    listOf("text-bold", "m-t-1")
                ).appendText(textValue)
            )

            // Imprimo la imágen del elemento
            uri?.let {
                if (uri.isNotEmpty()) {
                    this.appendChild(
                        getElementWithClasses(
                            "img",
                            listOf("img-square", "m-t-1")
                        ).attr("src", uri)
                    )
                }
            }
        }

        return container
    }

    /**
     * <p>Método helper que construye un Element de cierto tipo con ciertas clases</p>
     * @param elementTag El tag para el elemento
     * @param classesName La lista de clases que serán añadidas al elemento
     *
     */
    private fun getElementWithClasses(elementTag: String, classesName: List<String>?): Element {
        val e = Element(elementTag)

        classesName?.let {
            for (className in it) {
                e.addClass(className)
            }
        }

        return e
    }


    // 11690, 8270
    private val defaultPrintAttrs: PrintAttributes
        get() {
            val customSize = PrintAttributes.MediaSize("COIL", "COIL", 8270, 11690)

            return PrintAttributes.Builder()
                .setMediaSize(customSize)
                .setResolution(Resolution("RESOLUTION_ID", "RESOLUTION_ID", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()
        }

    interface OnCreateReport {
        fun reportCreated()
    }
}