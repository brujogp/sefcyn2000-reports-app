package com.sefcyn2000.reports.ui.fragments.reports.newreport.floatingfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.reportscomponents.Questionnaire;
import com.sefcyn2000.reports.data.entities.reportscomponents.Zone;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;
import com.sefcyn2000.reports.ui.adapters.reports.ListCategoriesForQuestionnaireAdapter;
import com.sefcyn2000.reports.ui.fragments.reports.newreport.floatingfragments.questionslist.ListQuestionsFloatFragment;
import com.sefcyn2000.reports.ui.fragments.reports.newreport.floatingfragments.questionslist.SelectQuestionsFloatFragment;

import java.util.ArrayList;
import java.util.List;

public class ParentQuestionFloatingFragment extends DialogFragment implements ListQuestionsFloatFragment.OnClickQuestionSelected, QuestionnaireForSelectedZoneFragment.OnDoneOneQuestionnairePage, SelectQuestionsFloatFragment.OnNextButtonClick {
    private final int indexCategory;
    private final Zone zoneSelected;
    private final ListCategoriesForQuestionnaireAdapter adapter;
    private final OnCompleteQuestionnaire onCompleteQuestionnaireImpl;

    private TextView tvZoneNameSelected;

    List<SimpleQuestion> questionSimpleList;

    private ImageView ivDismissDialog;


    private ArrayList<Questionnaire> completeQuestions = new ArrayList<>();
    // private final ArrayList<SimpleQuestion> simpleQuestionList = new ArrayList<>();

    private TextView tvInstructionsAndQuestionSelected;

    private AlertDialog alertDialogB;

    public ParentQuestionFloatingFragment(
            List<SimpleQuestion> listQuestions,
            int indexCategory,
            Zone zoneSelected,
            ListCategoriesForQuestionnaireAdapter adapterCategories,
            ParentQuestionFloatingFragment.OnCompleteQuestionnaire onCompleteQuestionnaireImpl
    ) {
        this.zoneSelected = zoneSelected;
        this.questionSimpleList = listQuestions;
        this.indexCategory = indexCategory;
        this.adapter = adapterCategories;
        this.onCompleteQuestionnaireImpl = onCompleteQuestionnaireImpl;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);

        // d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Quita el título a el dialog
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(true);
        d.setCanceledOnTouchOutside(false);
        return d;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        this.completeQuestions = new ArrayList<>();

        return inflater.inflate(R.layout.layout_report_parent_float_list_questions_to_a_zone, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Forzo a que el alto y el ancho conisidan con el padre
        getDialog().getWindow().setLayout(
                width,
                height
        );

        this.tvZoneNameSelected = view.findViewById(R.id.text_view_zone_selected_name);
        this.tvZoneNameSelected.setText(this.zoneSelected.getZoneName());
        this.tvInstructionsAndQuestionSelected = view.findViewById(R.id.tv_zone_selected_questions_instruction);

        this.ivDismissDialog = view.findViewById(R.id.image_button_dismiss_dialog);

        // Cuando se presiona el botón con un tache del padre, se hace un proceso para daterminar si
        //  todas las preguntas selecciondas ya han sido respondidas
        this.ivDismissDialog.setOnClickListener(v -> {
            int isAnswered = 0;
            int questionSelected = 0;

            // Itero sobre la lista de preguntas y determino cuántas han sido seleccionadas
            for (SimpleQuestion s : this.questionSimpleList) {
                if (s.isApplied() && s.isAnswered()) {
                    isAnswered++;
                }

                if (s.isApplied()) {
                    questionSelected++;
                }
            }


            if (questionSelected > 0 && isAnswered == questionSelected) {
                // Reinicio los valores de las preguntas
                for (SimpleQuestion s : this.questionSimpleList) {
                    s.setAnswered(false);
                    s.setApplied(false);
                }

                this.zoneSelected.setTotalResolved(true);
                this.adapter.notifyDataSetChanged();
                this.onCompleteQuestionnaireImpl.onCompleteQuestionnaire();
                this.dismiss();

            } else {

                this.alertDialogB = new AlertDialog.Builder(getActivity())
                        .setTitle("Conteste todas las preguntas")
                        .setMessage("Por favor resuelva todas las preguntas para la zona actual")
                        .setPositiveButton("Aceptar", (dialog, which) -> {
                            dismissAlertDialog();
                        })
                        .create();

                this.alertDialogB.show();
            }
        });

        this.tvInstructionsAndQuestionSelected.setText("Seleccione una pregunta");

        // Muestro por Primera vez el listado de preguntas
        // Esta pantalla solamente se tendrá que mostrar una vez cada que se seleccione una zona
        showListQuestionsForSelect();
    }

    // Muestra el fragmento que enliasta las preguntas
    // Aquí se tendrá que seleccionar qué preguntas del listado de preguntas aplican
    private void showListQuestionsForSelect() {
        SelectQuestionsFloatFragment selectQuestionsFloatFragment = new SelectQuestionsFloatFragment(
                this,
                this.questionSimpleList
        );

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_report_question_answer_selected, selectQuestionsFloatFragment)
                .commit();
    }

    private void dismissAlertDialog() {
        this.alertDialogB.dismiss();
    }

    // Método que muestra un listado de preguntas filtradas que han sido seleccionadas anteriorme
    // Este método es parte de la interfaz para comunicar este fragment con ListQuestionsFloatFragment
    //  informándo que una pregunta ha sido seleccionada, lo cual despliega el fragment que muestra el
    //  formulario para la pregunta seleccionda
    @Override
    public void questionSelected(SimpleQuestion simpleQuestion) {
        this.tvInstructionsAndQuestionSelected.setText(simpleQuestion.getQuestion());
        showQuestionnaireForQuestionSelected(simpleQuestion);
    }

    // Muestra el fragment que contiene el formulario para una hoja de formulario
    private void showQuestionnaireForQuestionSelected(SimpleQuestion questionSelected) {
        if (this.zoneSelected != null) {
            QuestionnaireForSelectedZoneFragment questionnaireForSelectedZoneFragment = new QuestionnaireForSelectedZoneFragment(
                    questionSelected,
                    this.zoneSelected,
                    this
            );

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_report_question_answer_selected, questionnaireForSelectedZoneFragment)
                    .commit();
        }
    }

    @Override
    public void onDoneQuestionnairePage() {
        onButtonNextOnQuestionsListForSelectClick();
        this.tvInstructionsAndQuestionSelected.setText("Seleccione una pregunta");
    }


    // Método para mostrar el listado de preguntas que ya han sido seleccionadas y aplican a una zona
    // FIXME: Hay un error que hace que algunas veces, no se muestre una pregunta
    @Override
    public void onButtonNextOnQuestionsListForSelectClick() {
        ListQuestionsFloatFragment listQuestionsFloatFragment = new ListQuestionsFloatFragment(this.questionSimpleList, this);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_report_question_answer_selected, listQuestionsFloatFragment)
                .commit();
    }

    public interface OnCompleteQuestionnaire {
        void onCompleteQuestionnaire();
    }
}
