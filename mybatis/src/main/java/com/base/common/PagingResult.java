package com.base.common;

import java.util.Collections;
import java.util.List;

public class PagingResult {

    private List<?> details = Collections.EMPTY_LIST;

    private long count;

    @SuppressWarnings("unchecked")
    public <T> List<T> getDetails() {
        return (List<T>)details;
    }

    public void setDetails(List<?> details) {
        this.details = details;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

}
