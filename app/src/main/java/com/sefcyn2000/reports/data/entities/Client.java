package com.sefcyn2000.reports.data.entities;

import com.google.firebase.Timestamp;

public class Client {
    Timestamp registerDate;

    int visitorCounter;
    int templatesCounter;
    int reportsCounter;

    String notes;
    String phone;
    String codeClient;
    String name;
    String clientType;
    String clientImageUrl;

    public Client() {
    }

    public Client(
            String notes,
            String phone,
            Timestamp registerDate,
            String codeClient,
            String name,
            String clientType,
            String clientImageUrl,
            int visitorCounter,
            int templatesCounter,
            int reportsCounter
    ) {
        this.notes = notes;
        this.phone = phone;
        this.registerDate = registerDate;
        this.codeClient = codeClient;
        this.name = name;
        this.clientType = clientType;
        this.clientImageUrl = clientImageUrl;
        this.visitorCounter = visitorCounter;
        this.templatesCounter = templatesCounter;
        this.reportsCounter = reportsCounter;

    }

    public int getVisitorCounter() {
        return visitorCounter;
    }

    public void setVisitorCounter(int visitorCounter) {
        this.visitorCounter = visitorCounter;
    }

    public String getClientImageUrl() {
        return clientImageUrl;
    }

    public void setClientImageUrl(String clientImageUrl) {
        this.clientImageUrl = clientImageUrl;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Timestamp registerDate) {
        this.registerDate = registerDate;
    }

    public String getCodeClient() {
        return codeClient;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getReportsCounter() {
        return reportsCounter;
    }

    public void setReportsCounter(int reportsCounter) {
        this.reportsCounter = reportsCounter;
    }

    public int getTemplatesCounter() {
        return templatesCounter;
    }

    public void setTemplatesCounter(int templatesCounter) {
        this.templatesCounter = templatesCounter;
    }
}
