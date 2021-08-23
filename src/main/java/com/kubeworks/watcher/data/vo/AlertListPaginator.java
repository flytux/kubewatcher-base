package com.kubeworks.watcher.data.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class AlertListPaginator {

    private Integer pagesPerBlock;
    private Integer postsPerPage;
    private Long totalPostCount;

    private Integer totalLastPageNum;

    public AlertListPaginator(Integer pagesPerBlock, Integer postsPerPage, Long totalPostCount){
        this.pagesPerBlock = pagesPerBlock;
        this.postsPerPage = postsPerPage;
        this.totalPostCount = totalPostCount;

        this.setTotalLastPageNum();
    }

    public void setPostsPerPage(Integer postsPerPage){
        this.postsPerPage = postsPerPage;
        this.setTotalLastPageNum();
    }

    public void setTotalPostCount(Long totalPostCount){
        this.totalPostCount = totalPostCount;
        this.setTotalLastPageNum();
    }

    private void setTotalLastPageNum() {
        if(totalPostCount == 0){
            this.totalLastPageNum = 1;
        } else {
            this.totalLastPageNum = (int) (Math.ceil((double) totalPostCount / postsPerPage));
        }
    }

    private Map<String, Object> getBlock(Integer currentPageNum) {

        Map<String, Object> result = new HashMap<>();

        if (pagesPerBlock % 2 == 0) {
            return null;
        }

        if (currentPageNum > totalLastPageNum && totalPostCount != 0 ){
            return null;
        }

        Integer blockLastPageNum = totalLastPageNum;
        Integer blockFirstPageNum = 1;

        Integer mid = pagesPerBlock / 2;
        if(currentPageNum <= pagesPerBlock){
            blockLastPageNum = pagesPerBlock;
        } else if(currentPageNum < totalLastPageNum - mid) {
            blockLastPageNum = currentPageNum + mid;
        }

        blockFirstPageNum = blockLastPageNum - (pagesPerBlock -1);

        if(totalLastPageNum < pagesPerBlock){
            blockLastPageNum = totalLastPageNum;
            blockFirstPageNum = 1;
        }

        List<Integer> pageList = new ArrayList<>();
        for(int i = 0, val = blockFirstPageNum; val <= blockLastPageNum; i++, val++){
            pageList.add(i, val);
        }

        result.put("isPrevExist", currentPageNum > pagesPerBlock);
        result.put("isNextExist", blockLastPageNum != 1 && !blockLastPageNum.equals(totalLastPageNum));
        result.put("totalListPageNum", totalLastPageNum);
        result.put("blockLastPageNum", blockLastPageNum);
        result.put("blockFirstPageNum", blockFirstPageNum);
        result.put("currentPageNum", currentPageNum);
        result.put("totalPostCount", totalPostCount);
        result.put("pagesPerBlock", pagesPerBlock);
        result.put("postsPerPage", postsPerPage);
        result.put("pageList", pageList);

        return result;
    }

    public Map<String, Object> getElasticBlock(Integer currentPageNum){
        return this.getBlock(currentPageNum);
    }
}
