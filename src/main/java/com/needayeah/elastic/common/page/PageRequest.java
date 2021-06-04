package com.needayeah.elastic.common.page;


import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class PageRequest {


    public static final PageRequest NO_PAGE = new PageRequest();
    public static final Integer DEFAULT_MAX_PAGE_SIZE = 10000;
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final Integer PAGE_SIZE_500 = 500;
    public static final Integer PAGE_SIZE_200 = 200;

    static {
        NO_PAGE.setPageSize(Integer.MAX_VALUE);
    }

    /**
     * 每页大小
     */
    protected int pageSize = 20;

    /**
     * 当前页
     */
    protected int pageNum = 1;

    /**
     * 记录开始位置
     */
    protected int pageFrom = 0;

    public static PageRequest of(int pageNum, int pageSize) {
        PageRequest page = new PageRequest();
        page.setPageSize(pageSize);
        page.setPageNum(pageNum);
        return page;
    }

    public int getPageSize() {
        return pageSize > DEFAULT_MAX_PAGE_SIZE ? DEFAULT_MAX_PAGE_SIZE : pageSize;
    }

    public int getPageFrom() {
        return (pageNum - 1) * pageSize;
    }

}
