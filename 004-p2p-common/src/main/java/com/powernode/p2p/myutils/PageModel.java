package com.powernode.p2p.myutils;

import java.io.Serializable;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/12
 */
public class PageModel implements Serializable {

    private Integer currentPage;
    private Integer pageSize=9;
    private Integer totalRecordCounts;
    private Integer totalPages;

    @Override
    public String toString() {
        return "PageModel{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalRecordCounts=" + totalRecordCounts +
                ", totalPages=" + totalPages +
                '}';
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalRecordCounts() {
        return totalRecordCounts;
    }

    public void setTotalRecordCounts(Integer totalRecordCounts) {
        this.totalRecordCounts = totalRecordCounts;
    }

    public Integer getTotalPages() {
        return totalRecordCounts%pageSize==0?totalRecordCounts/pageSize:totalRecordCounts/pageSize+1;
    }
}
