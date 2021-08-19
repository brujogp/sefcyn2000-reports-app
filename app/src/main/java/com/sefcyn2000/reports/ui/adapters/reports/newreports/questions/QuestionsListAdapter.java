package com.sefcyn2000.reports.ui.adapters.reports.newreports.questions;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.reportscomponents.SimpleQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para en listar las preguntas que fueron seleccionadas anteriormente y que sí aplican a la zona seleccionada.
 */
public class QuestionsListAdapter extends RecyclerView.Adapter<QuestionsListAdapter.QuestionItemViewHolder> {

    private final List<SimpleQuestion> tempSimpleQuestionList;
    Context context;
    List<SimpleQuestion> simpleQuestionList;

    QuestionSelectedOnListAdapter questionSelectedOnListAdapter;

    public QuestionsListAdapter(Context context, List<SimpleQuestion> simpleListQuestions, QuestionSelectedOnListAdapter questionSelectedOnListAdapter) {
        this.tempSimpleQuestionList = new ArrayList<>();
        this.simpleQuestionList = simpleListQuestions;
        this.context = context;
        this.questionSelectedOnListAdapter = questionSelectedOnListAdapter;

        // Asigno en una lista temporal las preguntas que sí aplican a la zona actual
        for (SimpleQuestion s : this.simpleQuestionList) {
            if (s.isApplied()) {
                this.tempSimpleQuestionList.add(s);
            }
        }

    }

    @NonNull
    @Override
    public QuestionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.layout_report_item_list_question, parent, false);
        QuestionItemViewHolder viewHolder = new QuestionItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionItemViewHolder holder, int position) {
        /*
        if (this.simpleQuestionList.get(position).isApplied()) {
            holder.zoneName.setText(this.simpleQuestionList.get(position).getQuestion());


            holder.zoneName.setOnClickListener(v -> this.questionSelectedOnListAdapter.onClickQuestionSelected(position));

            if (this.simpleQuestionList.get(position).isAnswered()) {
                holder.indicatorResponse.setBackgroundColor(Color.GREEN);
            } else {
                holder.indicatorResponse.setBackgroundColor(Color.WHITE);
            }
        }
         */

        holder.zoneName.setText(this.tempSimpleQuestionList.get(position).getQuestion());

        // Obtengo el indice del objeto de la lista original de preguntas, desde la lista temporal de preguntas
        int realPosition = this.simpleQuestionList.indexOf(this.tempSimpleQuestionList.get(position));

        holder.zoneName.setOnClickListener(v -> this.questionSelectedOnListAdapter.onClickQuestionSelected(realPosition));


        if (this.simpleQuestionList.get(realPosition).isAnswered()) {
            holder.indicatorResponse.setBackgroundColor(Color.GREEN);
        } else {
            holder.indicatorResponse.setBackgroundColor(Color.WHITE);
        }
    }

    // Este método determina también el valor "position" de onBindViewHolder
    @Override
    public int getItemCount() {
        return this.tempSimpleQuestionList.size();
    }

    class QuestionItemViewHolder extends RecyclerView.ViewHolder {
        TextView zoneName;
        View indicatorResponse;

        public QuestionItemViewHolder(@NonNull View itemView) {
            super(itemView);

            this.zoneName = itemView.findViewById(R.id.text_view_zone_name_in_list_floating);
            this.indicatorResponse = itemView.findViewById(R.id.view_indicator_question_progress);
        }
    }

    public interface QuestionSelectedOnListAdapter {
        void onClickQuestionSelected(int indexQuestion);
    }
}

