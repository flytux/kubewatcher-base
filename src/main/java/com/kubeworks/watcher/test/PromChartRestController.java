package com.kubeworks.watcher.test;

import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.entity.ChartQuery;
import com.kubeworks.watcher.data.vo.ChartQueryVO;
import com.kubeworks.watcher.data.repository.PageRepository;
import com.kubeworks.watcher.ecosystem.grafana.dto.DashboardDetail;
import com.kubeworks.watcher.ecosystem.grafana.service.GrafanaSerivce;
import com.kubeworks.watcher.preference.service.PageViewService;

import com.kubeworks.watcher.preference.service.TestViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class PromChartRestController {

    private final GrafanaSerivce grafanaSerivce;
    private final PageViewService pageViewService;
    private final TestViewService testViewService;

    @GetMapping(value = "/prom/chart/{uid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DashboardDetail grafanaDashboardPanels(@PathVariable String uid) {
        return grafanaSerivce.dashboard(uid);
    }


    /*
        차트로 구성된 대시보드의 페이지 구성 및 Prometheus query 정보를 JSON 타입으로 제공한다.
     */
    @GetMapping(value = "/prom/overview/{menuId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page getPageInfo(@PathVariable long menuId) {
        Page pageInfo = pageViewService.getPageInfo(menuId);

        return pageInfo;
    }

    /*
        Prometheus query 를 JSON 타입으로 제공한다.
     */
    @GetMapping(value = "/prom/query/{queryNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChartQuery getPromQuery(@PathVariable long queryNo) {
        ChartQuery query = testViewService.getQuery(queryNo);

        return query;
    }

    /*
    Prometheus query 를 Update 한다.
    */
    @RequestMapping("/prom/query/save/")
    public ChartQueryVO updatePromQuery(@ModelAttribute ChartQueryVO queryVo) {
        ChartQueryVO query = testViewService.updateQuery(queryVo);

        return query;
    }

}