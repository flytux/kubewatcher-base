package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.entity.PageRow;
import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class VmController {

    private static final long OVERVIEW_MENU_ID = 140;
    private static final long DETAIL_MENU_ID = 141;

    private final PageViewService pageViewService;
    private final ProxyApiService proxyApiService;

    @Autowired
    public VmController(PageViewService pageViewService, ProxyApiService proxyApiService) {
        this.pageViewService = pageViewService;
        this.proxyApiService = proxyApiService;
    }


    @GetMapping(value = "/monitoring/vm/overview", produces = MediaType.TEXT_HTML_VALUE)
    public String overview(Model model) {
        Map<Long, PageRowPanel> panelMap = pageViewService.getPagePanels(OVERVIEW_MENU_ID);
        model.addAttribute("panelMap", panelMap);
        return "monitoring/vm/vm-overview-template";
    }

    @GetMapping(value = "/monitoring/vm/monitoring", produces = MediaType.TEXT_HTML_VALUE)
    public String detail(Model model) {
        Page page = pageViewService.getPageView(DETAIL_MENU_ID);
        List<List<String>> templating = page.getRows().stream().map(PageRow::getPanels).flatMap(Collection::stream)
            .filter(pageRowPanel -> StringUtils.equals(pageRowPanel.getDataType(), "labelValues"))
            .map(pageRowPanel -> proxyApiService.labelValuesQuery(pageRowPanel.getSrc()))
            .filter(values -> !values.isEmpty())
            .collect(Collectors.toList());

        String defaultValue = templating.get(0).get(0);

        Map<Long, PageRowPanel> panelMap = page.getRows().stream()
            .map(PageRow::getPanels)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(PageRowPanel::getSort, pageRowPanel -> {
                pageRowPanel.setSrc(pageRowPanel.getSrc() + "&var-Node=" + defaultValue);
                return pageRowPanel;
            }));

        model.addAttribute("defaultNamespace", defaultValue);
        model.addAttribute("templating", templating);
        model.addAttribute("panelMap", panelMap);
        return "monitoring/vm/vm-detail-template";
    }

}
