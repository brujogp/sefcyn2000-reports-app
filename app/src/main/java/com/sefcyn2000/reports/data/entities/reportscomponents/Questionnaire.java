package com.sefcyn2000.reports.data.entities.reportscomponents;

import java.util.List;

/**
 * POJO para el almacenamiento de la informaciónde cada una de las hojas de cuestionario
 * de cada una de las preguntas.
 */
public class Questionnaire {
    // Objeto que almacena el nombre de la pregunta principalmente
    SimpleQuestion question;

    // La respuesta (sí o no).
    boolean affirmativeAnswer;

    // Las URI's de las fotos de cada apartado (para la respuetas, la causa raíz y el área de oportunidad).
    String answerQuestionPhotoUrl;
    String rootCausePhotoUrl;
    String opportunityAreaPhotoUrl;

    // Lista de secciones (si aplica) en donde se respondió positivamente la pregunta
    //  Esta clase es especial para los reportes, pues tiene campos que son usados a través de toda la interfaz para los reportes
    //  Uno de esos campos, es para introducir una URL de una imágen si es marcada esa zona como afirmativa para la pregunta
    List<Section> markedSectionsList;

    // Respuestas a los campos del formulario desde los catálogos
    String rootCause;
    String opportunityArea;
    String actionToClient;
    String actionToSupplier;

    // Observaciones para la zona.
    // Esto lo incluyo aquí, porque este objeto símboliza una "hoja" del cuestionario.
    String observations;

    public Questionnaire(
            SimpleQuestion question,
            boolean affirmativeAnswer,
            String answerQuestionPhotoUrl,
            String rootCausePhotoUrl,
            String opportunityAreaPhotoUrl,
            List<Section> markedSectionsList,
            String rootCause,
            String opportunityArea,
            String actionToClient,
            String actionToSupplier,
            String observations
    ) {
        this.question = question;
        this.affirmativeAnswer = affirmativeAnswer;
        this.answerQuestionPhotoUrl = answerQuestionPhotoUrl;
        this.rootCausePhotoUrl = rootCausePhotoUrl;
        this.opportunityAreaPhotoUrl = opportunityAreaPhotoUrl;
        this.markedSectionsList = markedSectionsList;
        this.rootCause = rootCause;
        this.opportunityArea = opportunityArea;
        this.actionToClient = actionToClient;
        this.actionToSupplier = actionToSupplier;
        this.observations = observations;
    }

    public Questionnaire() {
    }

    public SimpleQuestion getQuestion() {
        return question;
    }

    public void setQuestion(SimpleQuestion question) {
        this.question = question;
    }

    public boolean isAffirmativeAnswer() {
        return affirmativeAnswer;
    }

    public void setAffirmativeAnswer(boolean affirmativeAnswer) {
        this.affirmativeAnswer = affirmativeAnswer;
    }

    public String getAnswerQuestionPhotoUrl() {
        return answerQuestionPhotoUrl;
    }

    public void setAnswerQuestionPhotoUrl(String answerQuestionPhotoUrl) {
        this.answerQuestionPhotoUrl = answerQuestionPhotoUrl;
    }

    public String getRootCausePhotoUrl() {
        return rootCausePhotoUrl;
    }

    public void setRootCausePhotoUrl(String rootCausePhotoUrl) {
        this.rootCausePhotoUrl = rootCausePhotoUrl;
    }

    public String getOpportunityAreaPhotoUrl() {
        return opportunityAreaPhotoUrl;
    }

    public void setOpportunityAreaPhotoUrl(String opportunityAreaPhotoUrl) {
        this.opportunityAreaPhotoUrl = opportunityAreaPhotoUrl;
    }

    public List<Section> getMarkedSectionsList() {
        return markedSectionsList;
    }

    public void setMarkedSectionsList(List<Section> markedSectionsList) {
        this.markedSectionsList = markedSectionsList;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getOpportunityArea() {
        return opportunityArea;
    }

    public void setOpportunityArea(String opportunityArea) {
        this.opportunityArea = opportunityArea;
    }

    public String getActionToClient() {
        return actionToClient;
    }

    public void setActionToClient(String actionToClient) {
        this.actionToClient = actionToClient;
    }

    public String getActionToSupplier() {
        return actionToSupplier;
    }

    public void setActionToSupplier(String actionToSupplier) {
        this.actionToSupplier = actionToSupplier;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
