package com.propertyservice.Payload;



import java.util.List;

public class PaginationResponse<T> {

    private List<T> data;
    private int pageNo;
    private int pageSize;

    private boolean hasPrevious;
    private boolean hasNext;

    private Integer previousPage;
    private Integer nextPage;

    private int firstPage;
    private int lastPage;

    public PaginationResponse(List<T> data, int pageNo, int pageSize, int totalElements) {
        this.data = data;
        this.pageNo = pageNo;
        this.pageSize = pageSize;

        // First page always 0
        this.firstPage = 0;

        // Last page calculated based on total elements
        this.lastPage = (int) Math.ceil((double) totalElements / pageSize) - 1;
        if (this.lastPage < 0) this.lastPage = 0;

        // Previous
        this.hasPrevious = pageNo > 0;
        this.previousPage = hasPrevious ? pageNo - 1 : null;

        // Next
        this.hasNext = pageNo < lastPage;
        this.nextPage = hasNext ? pageNo + 1 : null;
    }

    public List<T> getData() {
        return data;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public Integer getPreviousPage() {
        return previousPage;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public int getLastPage() {
        return lastPage;
    }
}

