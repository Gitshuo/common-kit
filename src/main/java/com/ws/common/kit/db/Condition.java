package com.ws.common.kit.db;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;

/**
 * @author wangshuo
 * @version 2018-03-23
 */
public class Condition implements Serializable {

    private static final long serialVersionUID = 8003504863457196456L;

    private static final Joiner AND_JOINER = Joiner.on(" and ").skipNulls();
    private static final Joiner DOT_JOINER = Joiner.on(",").skipNulls();

    private static final String IN = " in ";
    private static final String ID = " id ";

    private List<Expression> expressions;
    private List<Expression> inExpressions;
    private List<Sort> sorts;
    private RowBound rowBound;

    public Condition() {
        expressions = Lists.newArrayListWithExpectedSize(4);
        inExpressions = Lists.newArrayListWithExpectedSize(4);
        sorts = Lists.newArrayListWithExpectedSize(4);
        rowBound = new RowBound();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Condition id(Object id) {
        return new Condition().and(new Expression(ID, Symbol.EQUALS, id));
    }

    private Condition and(Expression expression) {
        expressions.add(expression);
        return this;
    }

    private Condition sortBy(Sort sort) {
        sorts.add(sort);
        return this;
    }

    private Condition limit(int limit) {
        rowBound.setLimit(limit);
        return this;

    }

    private Condition offset(int offset) {
        rowBound.setOffset(offset);
        return this;
    }

    private void incOffset(int inc) {
        int old = rowBound.getOffset();
        rowBound.setOffset(old + inc);
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Expression> getInExpressions() {
        return inExpressions;
    }

    public void setInExpressions(List<Expression> inExpressions) {
        this.inExpressions = inExpressions;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    public RowBound getRowBound() {
        return rowBound;
    }

    public void setRowBound(RowBound rowBound) {
        this.rowBound = rowBound;
    }

    @Override
    public String toString() {
        return "where " + AND_JOINER.join(expressions) + AND_JOINER.join(inExpressions) + " order by "
                + DOT_JOINER.join(sorts) + rowBound;
    }

    public static class Builder {
        private Condition condition;

        public Builder() {
            this.condition = new Condition();
        }

        /**
         * @see #equals(Object)
         */
        @Deprecated
        public Builder and(String key, Object value) {
            condition.and(new Expression(key, Symbol.EQUALS, value));
            return this;
        }

        public Builder equal(String key, Object value) {
            condition.and(new Expression(key, Symbol.EQUALS, value));
            return this;
        }

        public Builder id(Object value) {
            condition.and(new Expression(ID, Symbol.EQUALS, value));
            return this;
        }

        public Builder like(String key, String value) {
            condition.and(new Expression(key, Symbol.LIKE, "%" + value + "%"));
            return this;
        }

        public Builder prefix(String key, String value) {
            condition.and(new Expression(key, Symbol.LIKE, value + "%"));
            return this;
        }

        public Builder greater(String key, Object value) {
            condition.and(new Expression(key, Symbol.GREATER, value));
            return this;
        }

        public Builder greaterEquals(String key, Object value) {
            condition.and(new Expression(key, Symbol.GREATER_EQUALS, value));
            return this;
        }

        public Builder notEquals(String key, Object value) {
            condition.and(new Expression(key, Symbol.NOT_EQUALS, value));
            return this;
        }

        public Builder less(String key, Object value) {
            condition.and(new Expression(key, Symbol.LESS, value));
            return this;
        }

        public Builder lessEquals(String key, Object value) {
            condition.and(new Expression(key, Symbol.LESS_EQUALS, value));
            return this;
        }

        public Builder in(String key, List value) {
            condition.getInExpressions().add(new Expression(key, IN, value));
            return this;
        }

        public Builder sort(String filed, Sort.Order order) {
            condition.sortBy(Sort.of(filed, order));
            return this;
        }

        public Builder sort(String filed) {
            condition.sortBy(Sort.of(filed));
            return this;
        }

        public Builder sort(List<String> fields, Sort.Order order) {
            if (fields == null) {
                return this;
            }
            for (String filed : fields) {
                condition.sortBy(Sort.of(filed, order));
            }
            return this;
        }

        public Builder limit(int limit) {
            condition.limit(limit);
            return this;
        }

        public Builder offset(int offset) {
            condition.offset(offset);
            return this;
        }

        public Builder incOffset(int inc) {
            condition.incOffset(inc);
            return this;
        }

        public Condition build() {
            return condition;
        }

    }
}
