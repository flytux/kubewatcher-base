package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.data.entity.*;
import com.kubeworks.watcher.data.repository.PageRepository;
import com.kubeworks.watcher.ecosystem.prometheus.dto.PrometheusApiResponse;
import com.kubeworks.watcher.ecosystem.prometheus.service.PrometheusService;
import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ApplicationController {

    private final MonitoringRestController monitoringRestController;
    private final ProxyApiService proxyApiService;

    @GetMapping(value = "/monitoring/application/overview", produces = MediaType.TEXT_HTML_VALUE)
    public String application(Model model) {
        // 어플리케이션 모니터링 바인딩 변수 가져오기 (변수 쿼리는 임시로 하드코딩)
        // 1. application --> 2. Pod 변수 (application 명 + "-.*")
        List<String> appList = proxyApiService.multiValuesQuery("count(up{job=\"jmx-metrics\"}) by (application)", "application");
        Collections.sort(appList);

        Map<String, Object> response = monitoringRestController.application();

        Page page = (Page) response.get("page");
        List<PageRow> pageRows =  page.getRows();
        List appPanels = new ArrayList();

        // 어플리케이션 현황 로우...
        List<PageRowPanel> overViewPanels = pageRows.get(0).getPageRowPanels();
        for(PageRowPanel pageRowPanel : overViewPanels){
            List<ChartQuery> appPanelQuery = pageRowPanel.getChartQueries();
            appPanelQuery.forEach(chartQuery -> {
                for (int cnt1 = 0; cnt1 <appList.size() ; cnt1++) {
                    //어플리케이션별 현황 처리 해야됨...
                    chartQuery.setApiQuery(changeVariable(chartQuery.getApiQuery(), ".*", ".*" ));
                }
            });
        }

        //어플리케이션별 상세 로우 ...
        List<PageRowPanel> appDetailPanels = pageRows.get(1).getPageRowPanels();
        int appDetailPanelsSize = appDetailPanels.size();
        //TO-DO : 데이터 방식으로 ... 필요없는 판넬 지우는 부분
        for(int i = 9*appList.size()+1 ; i < appDetailPanelsSize; i++) {
            appDetailPanels.remove(appDetailPanels.size()-1);
        }
        int loopCnt = 0;
        int appCnt = 0;
        List<PageRowPanel> appPanel = new ArrayList<PageRowPanel>();
        for (PageRowPanel pageRowPanel : appDetailPanels) {
            List<ChartQuery> appPanelQueries = pageRowPanel.getChartQueries();
            if (pageRowPanel.getTitle().equals("Application")) {
                pageRowPanel.setTitle(appList.get(appCnt)); // 어플리케이션명을 판넬 타이틀로 부여 한글 논리명 처리 해야됨, 어플리케이션에 해당하는 POD 구분
            }
            if (pageRowPanel.getFragmentName().equals("head-card-app-panel")) {
                for (ChartQuery chartQuery : appPanelQueries) {
                    //chartQuery.setApiQuery(changeVariable(chartQuery.getApiQuery(), appList.get(appCnt), podList.get(appCnt) ));
                    chartQuery.setApiQuery(changeVariable(chartQuery.getApiQuery(), appList.get(appCnt), appList.get(appCnt)+"-.*" ));
                }
                appPanel.add(pageRowPanel);
                loopCnt++;
                //App 별 서브판넬 갯수 변경시 같이 변경해야됨...
                if (loopCnt % 9 == 0) {
                    appPanels.add(appPanel);
                    appPanel = new ArrayList<PageRowPanel>();
                    appCnt++;
                    if (appCnt > appList.size()) break;
                }
            }
        }
        model.addAttribute("appPanels",appPanels);
        model.addAllAttributes(response);

        return "monitoring/application/overview";
    }

    private String changeVariable(String apiQuery, String app, String pod){
        String variablQuery = null;
        if (apiQuery.indexOf("$application") > -1) {
            // $application 변수 교체
            variablQuery = apiQuery.replace("$application",app);
            System.out.println(">>>>>> application variable : "+variablQuery);
        } else {
            // $pod 변수 교체
            variablQuery = apiQuery.replace("$podName", pod);
            System.out.println(">>>>>> pod variable : "+variablQuery);
        }
        return variablQuery;
    }
}
