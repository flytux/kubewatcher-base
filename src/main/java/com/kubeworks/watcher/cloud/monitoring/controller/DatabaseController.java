package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.data.entity.*;
import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class DatabaseController {

    private final MonitoringRestController monitoringRestController;
    private final ProxyApiService proxyApiService;

    @GetMapping(value = "/monitoring/database", produces = MediaType.TEXT_HTML_VALUE)
    public String database(Model model) {
        Map<String, Object> response = monitoringRestController.database();

        List<String> hostList = proxyApiService.multiValuesQuery("count(namedprocess_namegroup_states{zone=\"external\", state=\"Running\", groupname=~\"oracle_.*\"}) by (instance)", "instance");

        Page page = (Page) response.get("page");
        List<PageRow> pageRows =  page.getRows();

        List<PageRowPanel> dbHostPanels = pageRows.get(0).getPageRowPanels();
        List dbPanels = new ArrayList();

        List<PageRowPanel> dbPanel = new ArrayList<PageRowPanel>();
        int cnt = 0;
        int dbCnt = 0;
        for (PageRowPanel pageRowPanel : dbHostPanels) {
            if (pageRowPanel.getFragmentName().equals("head-card-db-panel")) {

                List<ChartQuery> dbPanelQueries = pageRowPanel.getChartQueries();
                for (ChartQuery chartQuery : dbPanelQueries) {
                    if (hostList.size() <= dbCnt ) break;
                    chartQuery.setApiQuery(changeVariable(chartQuery.getApiQuery(), hostList.get(dbCnt)));
                }
                dbPanel.add(pageRowPanel);
                cnt++;
                //DB 로우 하위 판넬 (Status, CPU, Mem) 갯수 변경시 같이 변경해야됨...
                if (cnt % 3 == 0) {
                    dbPanels.add(dbPanel);
                    dbCnt++;
                    dbPanel = new ArrayList<PageRowPanel>();
                }
            }
        }

        model.addAttribute("dbPanels",dbPanels);
        model.addAllAttributes(response);

        return "monitoring/database/database";
    }

    private String changeVariable(String apiQuery, String host){
        String variablQuery = null;
        // DB 노드 UP/DOWN 체크를 위해 프로세스 익스포터 노드에 노드 익스포터 포트로 변경
        host = host.replace("9256", "9100");
        if (apiQuery.indexOf("$Host") > -1) {
            // $application 변수 교체
            variablQuery = apiQuery.replace("$Host",host);
        }
        return variablQuery;
    }

    public Map<Long, PageRowPanel> getPanels(Page page, long pageRowId, PageVariable defaultVariable) {

        return Collections.emptyMap();
    }
}
