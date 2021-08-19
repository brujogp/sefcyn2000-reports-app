package com.sefcyn2000.reports.ui.adapters.reports.newreports.questions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;

import java.util.List;

/**
 * Adaptador para la pantalla que muestra la lista de preguntas en las cuales se tendrá que escoger cuáles preguntas
 * aplican y cuáles no
 */
public class ListQuestionsForSelectAdapter extends RecyclerView.Adapter<ListQuestionsForSelectAdapter.ViewHolder> {

    private Context context;
    private List<SimpleQuestion> simpleQuestionList;
    SetCounterItemsSelected onTouchItemImpl;

    public ListQuestionsForSelectAdapter(Context context, List<SimpleQuestion> simpleQuestions, SetCounterItemsSelected onTouchItemImpl) {
        this.context = context;
        this.simpleQuestionList = simpleQuestions;
        this.onTouchItemImpl = onTouchItemImpl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.layout_report_item_list_question_for_select, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.questionName.setText(this.simpleQuestionList.get(position).getQuestion());

        holder.checkboxQuestionSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.simpleQuestionList.get(position).setApplied(isChecked);
            this.onTouchItemImpl.onTouchItem(isChecked);
        });

    }

    @Override
    public int getItemCount() {
        return this.simpleQuestionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionName;
        private final CheckBox checkboxQuestionSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.questionName = itemView.findViewById(R.id.text_view_name_questions_list_for_select);
            this.checkboxQuestionSelect = itemView.findViewById(R.id.checkbox_questions_selected);
        }
    }

    public interface SetCounterItemsSelected {
        void onTouchItem(boolean isChecked);
    }
}
