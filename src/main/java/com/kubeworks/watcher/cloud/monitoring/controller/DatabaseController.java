package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.entity.PageRow;
import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.data.entity.PageVariable;
import com.kubeworks.watcher.data.vo.PageRowType;
import com.kubeworks.watcher.data.vo.VariableType;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import com.kubeworks.watcher.preference.service.PageViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Controller
public class DatabaseController {

    private static final long DATABASE_MENU_ID = 130;
    private static final long DATABASE_NODE_PANEL_ROW_ID = 17;
    private static final long DATABASE_PANEL_ROW_ID = 19;
    private final PageViewService pageViewService;
    private final ProxyApiService proxyApiService;

    @Autowired
    public DatabaseController(PageViewService pageViewService, ProxyApiService proxyApiService) {
        this.pageViewService = pageViewService;
        this.proxyApiService = proxyApiService;
    }

    @GetMapping(value = "/monitoring/database", produces = MediaType.TEXT_HTML_VALUE)
    public String database(Model model) {
        Page page = pageViewService.getPageView(DATABASE_MENU_ID);

        Map<String, PageVariable> templating = page.getVariables().stream()
            .filter(variable -> {
                variable.setRefIds(ExternalConstants.getTemplateVariables(variable.getSrc()));
                return VariableType.METRIC_LABEL_VALUES == variable.getType();
            })
            .map(variable -> {
                String src = variable.getSrc();
                Matcher matcher = ExternalConstants.GRAFANA_TEMPLATE_VARIABLE_PATTERN.matcher(src);
                if (matcher.find()) {
                    return variable;
                }
                List<String> values = proxyApiService.labelValuesQuery(variable.getSrc());

                variable.setValues(values);
                return variable;
            })
            .sorted(Comparator.comparing(PageVariable::getSort))
            .collect(Collectors.toMap(
                PageVariable::getName, pageVariable -> pageVariable, (v1, v2) -> v1, LinkedHashMap::new));

        PageVariable defaultVariable = templating.values().stream().map(pageVariable -> {
            List<?> values = pageVariable.getValues();
            if (CollectionUtils.isEmpty(values)) {
                return null;
            }
            return pageVariable;
        }).filter(Objects::nonNull).findFirst().orElse(null);

        Map<Long, PageRowPanel> nodePanelMap = getPanels(page, DATABASE_NODE_PANEL_ROW_ID, null);
        Map<Long, PageRowPanel> panelMap = getPanels(page, DATABASE_PANEL_ROW_ID, defaultVariable);

        model.addAttribute("defaultVariable", defaultVariable);
        model.addAttribute("templating", templating);
        model.addAttribute("nodePanelMap", nodePanelMap);
        model.addAttribute("panelMap", panelMap);
        return "monitoring/database/database";
    }


    public Map<Long, PageRowPanel> getPanels(Page page, long pageRowId, PageVariable defaultVariable) {
        return page.getRows().stream()
            .filter(pageRow -> pageRow.getPageRowId() == pageRowId
                && pageRow.getType() == PageRowType.PANEL)
            .map(PageRow::getPanels)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(PageRowPanel::getSort, pageRowPanel -> {

                if (defaultVariable != null) {
                    pageRowPanel.setSrc(pageRowPanel.getSrc() + "&var-" + defaultVariable.getName() + "=" + defaultVariable.getValues().get(0));
                }

                return pageRowPanel;
            }));
    }
}
