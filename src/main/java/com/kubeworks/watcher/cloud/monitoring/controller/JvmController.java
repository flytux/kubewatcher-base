package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import com.kubeworks.watcher.preference.service.PageViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class JvmController {

    private static final long OVERVIEW_MENU_ID = 120;
    private static final long DETAIL_MENU_ID = 121;

    private final PageViewService pageViewService;
    private final ProxyApiService proxyApiService;

    @Autowired
    public JvmController(PageViewService pageViewService, ProxyApiService proxyApiService) {
        this.pageViewService = pageViewService;
        this.proxyApiService = proxyApiService;
    }

    @GetMapping(value = "/monitoring/jvm/overview", produces = MediaType.TEXT_HTML_VALUE)
    public String overview(Model model) {
        Map<Long, PageRowPanel> panelMap = pageViewService.getPagePanels(OVERVIEW_MENU_ID);
        model.addAttribute("panelMap", panelMap);
        return "monitoring/jvm/overview";
    }

    @GetMapping(value = "/monitoring/jvm/application", produces = MediaType.TEXT_HTML_VALUE)
    public String detail(Model model) {
//        Page page = pageViewService.getPageView(DETAIL_MENU_ID);
//
//        Map<String, PageVariable> templating = page.getVariables().stream()
//            .filter(variable -> {
//                variable.setRefIds(ExternalConstants.getTemplateVariables(variable.getSrc()));
//                return VariableType.METRIC_LABEL_VALUES == variable.getType();
//            })
//            .map(variable -> {
//                String src = variable.getSrc();
//                Matcher matcher = ExternalConstants.GRAFANA_TEMPLATE_VARIABLE_PATTERN.matcher(src);
//                if (matcher.find()) {
//                    return variable;
//                }
//                List<String> values = proxyApiService.labelValuesQuery(variable.getSrc());
//                variable.setValues(values);
//                return variable;
//            })
//            .sorted(Comparator.comparing(PageVariable::getSort))
//            .collect(Collectors.toMap(
//                PageVariable::getName, pageVariable -> pageVariable, (v1, v2) -> v1, LinkedHashMap::new));
//
//
//
//        Map<Long, PageRowPanel> panelMap = page.getRows().stream()
//            .filter(pageRow -> pageRow.getType() == PageRowType.PANEL)
//            .map(PageRow::getPanels)
//            .flatMap(Collection::stream)
//            .collect(Collectors.toMap(PageRowPanel::getSort, pageRowPanel -> {
////                pageRowPanel.setSrc(pageRowPanel.getSrc() + "&var-Node=" + defaultValue);
//                return pageRowPanel;
//            }));
//
//        model.addAttribute("templating", templating);
//        model.addAttribute("panelMap", panelMap);
        return "monitoring/jvm/application";
    }
}
