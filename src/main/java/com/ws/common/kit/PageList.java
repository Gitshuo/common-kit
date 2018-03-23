package com.ws.common.kit;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.util.CollectionUtils;

/**
 * @author wangshuo
 * @version 2018-03-23
 */
public abstract class PageList<T> implements Iterable<T> {
    private int pageSize = 200;

    public PageList() {
    }

    public PageList(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {
            private ArrayList<T> arrayList = Lists.newArrayList();
            // 总计数（查询游标）
            private int index = 0;
            // arrayList中的计数
            private int cur = 0;

            @Override
            public boolean hasNext() {
                // 首次调用或已遍历完前arrayList
                if (index == 0 || cur >= arrayList.size()) {
                    List<T> resultList = query(index, pageSize);
                    if (CollectionUtils.isEmpty(resultList)) {
                        return false;
                    }
                    arrayList = Lists.newArrayList(resultList);
                    cur = 0;
                }
                return true;
            }

            @Override
            public T next() {
                index++;
                return arrayList.get(cur++);
            }

            @Override
            public void remove() {
                throw new NoSuchMethodError("don't support this method");
            }
        };
    }

    public abstract List<T> query(int offset, int limit);
}
