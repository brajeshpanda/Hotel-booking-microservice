package com.bookingservice.Payload;



import java.util.List;

public class PaginationResponse<T> {

    private List<T> content;
    private int pageNo;
    private int pageSize;
    private int totalElements;

    public PaginationResponse() {}

    public PaginationResponse(List<T> content, int pageNo, int pageSize, int totalElements) {
        this.content = content;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}

