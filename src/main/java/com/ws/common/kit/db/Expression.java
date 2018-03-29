package com.ws.common.kit.db;

import com.google.common.base.Joiner;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author wangshuo
 * @version 2018-03-23
 */
public class Expression implements Serializable {

    private static final long serialVersionUID = -493260300244315078L;

    private static final Joiner DOT_JOINER = Joiner.on(",");

    private String field;
    private String symbol;
    private Object value;

    public Expression(String field, Symbol symbol, Object value) {
        this.field = field;
        this.symbol = symbol.getTag();
        this.value = value;
    }

    public Expression(String field, String symbol, Object value) {
        this.field = field;
        this.symbol = symbol;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (Objects.equals(symbol, "in")) {
            return field + symbol + "(" + DOT_JOINER.join((Iterable<?>) value) + ")";
        } else {
            return field + symbol + "\"" + value + "\"";
        }
    }
}
