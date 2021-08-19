package com.sefcyn2000.reports.utilities.constans;

public enum TagTypeItemTemplateEnum {
    CONTAINER_SECTION("cs"), CONTAINER_ZONE("cz"), CONTAINER_CATEGORY("cc");

    private String tag;

    TagTypeItemTemplateEnum(String type) {
        this.tag = type;
    }

    public String getTag() {
        return tag;
    }
}
