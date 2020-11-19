package com.kubeworks.watcher.preference.service.impl;

import com.kubeworks.watcher.data.entity.ChartQuery;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.repository.ChartQueryRepository;
import com.kubeworks.watcher.data.vo.ChartQueryVO;
import com.kubeworks.watcher.preference.service.TestViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TestViewServiceImpl implements TestViewService {

    private final ChartQueryRepository chartQueryRepository;

    @Autowired
    public TestViewServiceImpl(ChartQueryRepository chartQueryRepository) {
        this.chartQueryRepository = chartQueryRepository;
    }
    /*
        Prometheus query 를 조회한다.
    */
    @Override
    public ChartQuery getQuery(long queryNo) {
        Optional<ChartQuery> query = chartQueryRepository.findById(queryNo);
        return query.get();
    }
    /*
        Prometheus query 를 수정한다.
    */
    public ChartQueryVO updateQuery(ChartQueryVO chartQueryVO) {
        Optional<ChartQuery> query = chartQueryRepository.findById(chartQueryVO.getCQueryId());
        log.info(">>>>> VO : "+chartQueryVO.getCQueryId());
        query.ifPresent(selectQuery -> {
            selectQuery.setApiQuery(chartQueryVO.getApiQuery());
            ChartQuery newQuery = chartQueryRepository.save(selectQuery);
        });
        return chartQueryVO;
    }
}
