package com.sefcyn2000.reports.ui.fragments.reports.newreport.floatingfragments.questionslist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;
import com.sefcyn2000.reports.ui.adapters.reports.newreports.questions.ListQuestionsForSelectAdapter;

import java.util.List;

/**
 * <p>
 * Fragment que enlista todas las preguntas.
 * Este fragmente permite seleccionar las preguntas que aplican a la zona seleccionada
 * </p>
 * <p>
 * Después, se notificará al padre del fragment el conjunto de preguntas que se han seleccionado, a lo cual, se mostrará
 * la pantalla para seleccionar cada una de las preguntas y responder el formulario.
 * </p>
 */
public class SelectQuestionsFloatFragment extends Fragment implements ListQuestionsForSelectAdapter.SetCounterItemsSelected {
    private RecyclerView recyclerViewListQuestionsForSelect;
    private OnNextButtonClick onNextButtonClickImpl;
    private List<SimpleQuestion> simpleQuestionList;
    private ListQuestionsForSelectAdapter adapter;
    private int counterItemSelected;
    private MaterialButton btnNextToApplyQuestions;

    public SelectQuestionsFloatFragment(
            OnNextButtonClick onNextButtonClickImpl,
            List<SimpleQuestion> simpleQuestionList
    ) {
        this.onNextButtonClickImpl = onNextButtonClickImpl;
        this.simpleQuestionList = simpleQuestionList;
        this.counterItemSelected = 0;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_report_fragment_floating_list_questions_for_select, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        this.recyclerViewListQuestionsForSelect = view.findViewById(R.id.recycler_view_list_questions_for_select_fragment_floating);

        this.adapter = new ListQuestionsForSelectAdapter(
                getContext(),
                this.simpleQuestionList,
                this
        );

        this.btnNextToApplyQuestions = view.findViewById(R.id.btn_next_to_apply_questions);
        this.btnNextToApplyQuestions.setOnClickListener(v -> {
            onNextButtonClickImpl.onButtonNextOnQuestionsListForSelectClick();
        });

        this.recyclerViewListQuestionsForSelect.setAdapter(this.adapter);
        this.recyclerViewListQuestionsForSelect.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onTouchItem(boolean isChecked) {
        if (isChecked) {
            this.counterItemSelected++;
        } else {
            this.counterItemSelected--;
        }

        if (this.counterItemSelected > 0) {
            this.btnNextToApplyQuestions.setVisibility(View.VISIBLE);
        } else {
            this.btnNextToApplyQuestions.setVisibility(View.GONE);
        }
    }


    public interface OnNextButtonClick {
        void onButtonNextOnQuestionsListForSelectClick();
    }
}
