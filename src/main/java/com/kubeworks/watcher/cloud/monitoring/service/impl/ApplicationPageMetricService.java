package com.kubeworks.watcher.cloud.monitoring.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubeworks.watcher.cloud.monitoring.service.PageMetricService;
import com.kubeworks.watcher.data.entity.ChartQuery;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.data.entity.PageVariable;
import com.kubeworks.watcher.ecosystem.prometheus.service.ApplicationService;
import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_={@Autowired})
public class ApplicationPageMetricService implements PageMetricService<Page> {

    private final PageViewService pageViewService;
    private final ProxyApiService proxyApiService;
    private final ObjectMapper objectMapper;
    private final ApplicationService applicationService;

    @SneakyThrows
    @Override
    public Page pageMetrics(long menuId) {

        Page pageView = pageViewService.getPageView(menuId);
        List<PageVariable> variables = pageView.getVariables();

        Map<String, PageVariable> variableMap = variables.stream()
            .map(this::getValuesByVariable)
            .collect(Collectors.toMap(PageVariable::getName, Function.identity()));

        /* subGroup */
        pageView.getRows().forEach(row -> {
            List<PageRowPanel> panels = setSubGroup(row);
            panels = panels.stream()
                .map(panel -> generateRepeatPanel(variableMap, panel))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

            row.setPageRowPanels(panels);
        });

        return pageView;
    }

    private List<PageRowPanel> generateRepeatPanel(Map<String, PageVariable> variableMap, PageRowPanel panel) {
        PageVariable pageVariable = variableMap.get(panel.getRepeatVariable());
        if (StringUtils.isEmpty(panel.getRepeatVariable())
            || pageVariable == null
            || CollectionUtils.isEmpty(pageVariable.getValues())) {
            return Collections.singletonList(panel);
        }

        final Pattern varPattern = Pattern.compile("\\$" + pageVariable.getName());
        List<PageRowPanel> list = new ArrayList<>();
        List<?> values = pageVariable.getValues();
        for (int i = 0, valuesSize = values.size(); i < valuesSize; i++) {
            String value = (String) values.get(i);
            PageRowPanel pageRowPanel = copyPanel(panel, varPattern, value, i);
            if (pageRowPanel != null) {
                list.add(pageRowPanel);
            }
        }
        return list;
    }

    private PageRowPanel copyPanel(PageRowPanel panel, Pattern varPattern, String value, int index) {
        try {
            PageRowPanel copyPanel = objectMapper.readValue(objectMapper.writeValueAsString(panel), PageRowPanel.class);
            copyPanel.setTitle(value);

            convertApiQuery(copyPanel, varPattern, value, index * 10000L);
            return copyPanel;
        } catch (JsonProcessingException e) {
            log.error("failed panel copy // panelId={}, variable={}", panel.getPanelId(), value, e);
            return null;
        }
    }

    private void convertApiQuery(PageRowPanel panel, Pattern varPattern, String value, long virtualId) {
        panel.setPanelId(panel.getPanelId() + virtualId);
        List<ChartQuery> chartQueries = panel.getChartQueries();
        chartQueries.forEach(chartQuery -> {
            final String query = RegExUtils.replaceAll(chartQuery.getApiQuery(),
                varPattern, value);
            chartQuery.setApiQuery(query);
        });
        if (CollectionUtils.isNotEmpty(panel.getSubPanel())) {
            panel.getSubPanel().forEach(subPanel -> convertApiQuery(subPanel, varPattern, value, virtualId));
        }
    }


    private PageVariable getValuesByVariable(PageVariable variable) {
        String serviceNames = applicationService.getServiceNamesOfPromQL();
        variable.setApiQuery(RegExUtils.replaceAll(variable.getApiQuery(), Pattern.compile("\\$services"), serviceNames));
        List<String> values = proxyApiService.query(variable);
        variable.setValues(values);
        return variable;
    }
}
