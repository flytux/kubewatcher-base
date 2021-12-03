package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.data.entity.ChartQuery;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import com.kubeworks.watcher.preference.service.PageViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping(value="/monitoring/database")
public class DatabaseController implements BaseController {

    private static final long DATABASE_MENU_ID = 130;
    private static final String EXPRESSION_PROPERTY = "kube.prometheus.expression.database.instance";
    private static final String DEFAULT_EXPRESSION = "count(namedprocess_namegroup_states{zone=\"external\", state=\"Running\", groupname=~\"oracle.*\"}) by (instance)";

    private final ProxyApiService proxyApiService;

    private final PageViewService pageViewService;
    private final MonitoringProperties properties;

    private final String prometheusExpression;

    @Autowired
    public DatabaseController(final Environment env,
            final ProxyApiService proxyApiService, final PageViewService pageViewService, final MonitoringProperties properties) {

        this.proxyApiService = proxyApiService;
        this.pageViewService = pageViewService;
        this.properties = properties;

        this.prometheusExpression = env.getProperty(EXPRESSION_PROPERTY, DEFAULT_EXPRESSION);
    }

    @GetMapping
    public String database(final Model model) {

        final Page page = pageViewService.getPageView(DATABASE_MENU_ID);

        List<String> hostList = proxyApiService.multiValuesQuery(prometheusExpression, "instance");

        List<PageRowPanel> dbHostPanels = page.getRows().get(0).getPageRowPanels();
        LinkedHashMap<String, List<PageRowPanel>> dbPanels = new LinkedHashMap<>();

        List<PageRowPanel> dbPanel = new ArrayList<>();
        int cnt = 0;
        int dbCnt = 0;
        for (PageRowPanel pageRowPanel : dbHostPanels) {
            if ("head-card-db-panel".equals(pageRowPanel.getFragmentName())) {

                List<ChartQuery> dbPanelQueries = pageRowPanel.getChartQueries();
                for (ChartQuery chartQuery : dbPanelQueries) {
                    if (hostList.size() <= dbCnt) { break; }
                    chartQuery.setApiQuery(changeVariable(chartQuery.getApiQuery(), hostList.get(dbCnt)));
                }

                dbPanel.add(pageRowPanel);
                cnt++;
                //DB 로우 하위 판넬 (Status, CPU, Mem) 갯수 변경시 같이 변경해야됨...
                if (cnt % 3 == 0) {
                    if (hostList.size() <= dbCnt) {
                        dbPanels.put("none", dbPanel);
                    } else {
                        dbPanels.put(hostList.get(dbCnt), dbPanel);
                    }
                    dbCnt++;
                    dbPanel = new ArrayList<>();
                }
            }
        }

        model.addAttribute("dbPanels", dbPanels);
        model.addAttribute(Props.HOST, properties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, page);

        return createViewName("database");
    }

    private String changeVariable(final String q, final String host) {

        // DB 노드 UP/DOWN 체크를 위해 프로세스 익스포터 노드에 노드 익스포터 포트로 변경
        // $application 변수 교체

        return q.contains("$Host") ? q.replace("$Host", host.replace("9256", "9100")) : q;
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "monitoring/database/";
    }
}
