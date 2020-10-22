package com.kubeworks.watcher.test;

import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.repository.PageRepository;
import com.kubeworks.watcher.ecosystem.grafana.dto.DashboardDetail;
import com.kubeworks.watcher.ecosystem.grafana.service.GrafanaSerivce;
import com.kubeworks.watcher.preference.service.PageViewService;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class PromChartRestController {

    private final GrafanaSerivce grafanaSerivce;
    private final PageViewService pageViewService;

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

}