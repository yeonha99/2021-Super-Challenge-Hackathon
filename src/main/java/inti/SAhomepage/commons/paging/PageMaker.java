package inti.SAhomepage.commons.paging;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class PageMaker {
    private int totalCount;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    public int getTotalCount() {
        return totalCount;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public boolean isPrev() {
        return prev;
    }

    public void setPrev(boolean prev) {
        this.prev = prev;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public int getDisplayPageNum() {
        return displayPageNum;
    }

    public void setDisplayPageNum(int displayPageNum) {
        this.displayPageNum = displayPageNum;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    private int displayPageNum = 10;

    private Criteria criteria;

    public void setCriteria(Criteria criteria){
        this.criteria = criteria;
    }

    public void setTotalCount(int totalCount){
        this.totalCount = totalCount;
        calcData();
    }

    private void calcData(){
        endPage = (int)(Math.ceil(criteria.getPage()/(double)displayPageNum)*displayPageNum);

        startPage = (endPage-displayPageNum)+1;

        int tempEndPage = (int) (Math.ceil(totalCount/(double)criteria.getPerPageNum()));

        if(endPage > tempEndPage)
            endPage = tempEndPage;

        prev = startPage == 1 ? false:true;
        next = endPage* criteria.getPerPageNum() >= totalCount ? false : true;
    }

    public String makeQuery(int page){
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .queryParam("page", page)
                .queryParam("perPageNum", criteria.getPerPageNum())
                .build();
        return uriComponents.toUriString();
    }

    @Override
    public String toString(){
        return "PageMaker{" +
                "totalCount=" + totalCount +
                ", startPage=" + startPage +
                ", endPage=" + endPage +
                ", prev=" + prev +
                ", next=" + next +
                ", displayPageNum=" + displayPageNum +
                ", criteria=" + criteria +
                '}';
    }
}
