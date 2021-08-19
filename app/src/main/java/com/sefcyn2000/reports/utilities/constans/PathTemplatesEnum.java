package com.sefcyn2000.reports.utilities.constans;

public enum PathTemplatesEnum {
    INCOMPLETE_TEMPLATES("incomplete-templates"),
    COMPLETES_TEMPLATES("templates");

    private String path;

    public String getPath() {
        return path;
    }

    PathTemplatesEnum(String path) {
        this.path = path;
    }
}
