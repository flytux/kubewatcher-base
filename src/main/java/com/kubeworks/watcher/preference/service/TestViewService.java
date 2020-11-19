package com.kubeworks.watcher.preference.service;

import com.kubeworks.watcher.data.entity.ChartQuery;
import com.kubeworks.watcher.data.vo.ChartQueryVO;

import java.util.List;
import java.util.Map;

public interface TestViewService {
    ChartQuery getQuery(long queryNo);
    ChartQueryVO updateQuery(ChartQueryVO chartQueryVO);
}
