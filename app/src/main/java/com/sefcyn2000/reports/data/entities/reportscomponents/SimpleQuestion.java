package com.sefcyn2000.reports.data.entities.reportscomponents;

public class SimpleQuestion {
    // Enunciado de la pregunta
    String question;

    // Bandera que indica si la pregunta ya fue respondida
    boolean isAnswered = false;

    // Bandera que indica si la pregunta aplica o no para la zona seleccionada
    boolean isApplied;

    public SimpleQuestion(String question, boolean isAnswered, boolean isApplied) {
        this.question = question;
        this.isAnswered = isAnswered;
        this.isApplied = isApplied;
    }

    public SimpleQuestion() {
    }

    public boolean isApplied() {
        return isApplied;
    }

    public void setApplied(boolean applied) {
        isApplied = applied;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }
}
