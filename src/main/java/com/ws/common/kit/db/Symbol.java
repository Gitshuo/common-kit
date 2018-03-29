package com.ws.common.kit.db;

/**
 * @author wangshuo
 * @version 2018-03-23
 */
public enum Symbol {
    GREATER(" > "),
    EQUALS(" = "),
    NOT_EQUALS(" != "),
    LESS(" < "),
    GREATER_EQUALS(" >= "),
    LESS_EQUALS(" <= "),
    LIKE(" like ")
    ;

    private String tag;

    Symbol(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
