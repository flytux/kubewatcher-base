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

    public void setTotalLastPageNum(){
        if(totalPostCount == 0){
            this.totalLastPageNum = 1;
        } else {
            this.totalLastPageNum = (int) (Math.ceil((double) totalPostCount / postsPerPage));
        }
    }

    private Map<String, Object> getBlock(Integer currentPageNum, Boolean isFixed){

        Map<String, Object> result = new HashMap<>();

        if (pagesPerBlock % 2 ==0 && !isFixed){
//            result.put("pageError", "");
            return null;
//            throw new IllegalStateException("pagesPerBlock은 홀수만 가능합니다");
        }

        if (currentPageNum > totalLastPageNum && totalPostCount != 0 ){
//            result.put("pageError", "마지막 페이지는 " + totalLastPageNum + " 페이지 입니다.");
            return null;
//            throw  new IllegalStateException("currentPageNum가 총 페이지 개수(" + totalLastPageNum + ") 보다 큽니다");
        }

        Integer blockLastPageNum = totalLastPageNum;
        Integer blockFirstPageNum = 1;

        if(isFixed) {
            Integer mod = totalLastPageNum % pagesPerBlock;
            if(totalPostCount - mod >= currentPageNum){
                blockLastPageNum = (int) (Math.ceil((float)currentPageNum / pagesPerBlock) * pagesPerBlock);
                blockFirstPageNum = blockLastPageNum - (pagesPerBlock - 1);
            } else {
                blockFirstPageNum = (int) (Math.ceil((float)currentPageNum / pagesPerBlock) * pagesPerBlock) - (pagesPerBlock - 1);
            }
        } else {
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

        }

        List<Integer> pageList = new ArrayList<>();
        for(int i = 0, val = blockFirstPageNum; val <= blockLastPageNum; i++, val++){
            pageList.add(i, val);
        }

        result.put("isPrevExist", (int)currentPageNum > (int)pagesPerBlock);
        result.put("isNextExist", blockLastPageNum != 1 ? (int)blockLastPageNum != (int)totalLastPageNum : false);
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

    public Map<String, Object> getElasticBolock(Integer currentPageNum){
        return this.getBlock(currentPageNum, false);
    }

    public Map<String, Object> getFixedBlock(Integer currentPageNum){
        return this.getBlock(currentPageNum, true);
    }
}
