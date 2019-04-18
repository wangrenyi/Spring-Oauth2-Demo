package com.base.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PagingQuery {

    private Integer pageIndex;

    private Integer pageSize;

    /**
     * aa,asc|bb,desc
     */
    private String orderBy;

    private Map<String, String> criteria = new HashMap<String, String>();

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Map<String, String> getCriteria() {
        return criteria;
    }

    public void setCriteria(Map<String, String> criteria) {
        this.criteria = criteria;
    }

    public Object getCriterion(String key) {
        return criteria.get(key);
    }

    public void addCriterion(String key, String value) {
        criteria.put(key, value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
