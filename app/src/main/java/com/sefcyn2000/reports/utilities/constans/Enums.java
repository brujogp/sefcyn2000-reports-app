package com.sefcyn2000.reports.utilities.constans;

public enum Enums {
    IMAGE_TYPE_PROFILE_CLIENT(1), IMAGE_TYPE_REPORT(2), SET_CLIENT(3);
    private int val;

    Enums(int val) {
        val = val;
    }

    public int getVal() {
        return val;
    }
}
