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

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;
import com.sefcyn2000.reports.ui.adapters.reports.newreports.questions.QuestionsListAdapter;

import java.util.List;

/**
 * Fragmento que muestra el listado de preguntas que han sido seleccionadas
 * y que sí aplican a la zona seleccionada actual
 */
public class ListQuestionsFloatFragment extends Fragment implements QuestionsListAdapter.QuestionSelectedOnListAdapter {
    private final List<SimpleQuestion> questionsSimpleList;
    private final OnClickQuestionSelected onQuestionClickImpl;

    private RecyclerView recyclerViewListQuestions;
    private QuestionsListAdapter questionsListAdapter;

    public ListQuestionsFloatFragment(
            List<SimpleQuestion> questionSimpleList,
            ListQuestionsFloatFragment.OnClickQuestionSelected onClickQuestionSelected
    ) {
        this.questionsSimpleList = questionSimpleList;
        this.onQuestionClickImpl = onClickQuestionSelected;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_report_fragment_floating_list_questions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        this.recyclerViewListQuestions = view.findViewById(R.id.recycler_view_list_questions_fragment_floating);

        this.questionsListAdapter = new QuestionsListAdapter(getActivity(), this.questionsSimpleList, this);

        this.recyclerViewListQuestions.setAdapter(this.questionsListAdapter);
        this.recyclerViewListQuestions.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.recyclerViewListQuestions.setHasFixedSize(true);
    }

    @Override
    public void onClickQuestionSelected(int indexQuestion) {
        this.onQuestionClickImpl.questionSelected(this.questionsSimpleList.get(indexQuestion));
    }

    public interface OnClickQuestionSelected {
        void questionSelected(SimpleQuestion simpleQuestion);
    }
}
