package com.ws.common.kit.db;

import java.io.Serializable;

/**
 * @author wangshuo
 * @version 2018-03-23
 */
public class Sort implements Serializable {

    private static final long serialVersionUID = -1238854156895906505L;

    public enum Order {
        ASC, DESC
    }

    private String name;
    private Order order;

    public static Sort of(String filed, Order order) {
        return new Sort(filed, order);
    }

    public static Sort of(String filed) {
        return new Sort(filed);
    }

    public Sort(String column) {
        this(column, Order.ASC);
    }

    public Sort(String name, Order order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return name + " " + order;
    }
}
