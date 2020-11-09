package com.kubeworks.watcher.cloud.monitoring.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class VmController {

    private final MonitoringRestController monitoringRestController;



    @GetMapping(value = "/monitoring/vm/overview", produces = MediaType.TEXT_HTML_VALUE)
    public String overview(Model model) {
        Map<String, Object> response = monitoringRestController.vmOverview();
        model.addAllAttributes(response);
        return "monitoring/vm/vm-overview";
    }

    @GetMapping(value = "/monitoring/vm/monitoring", produces = MediaType.TEXT_HTML_VALUE)
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
//        final AtomicReference<String> defaultValue = new AtomicReference<>("ALL");
//        Optional<PageVariable> firstVariable = page.getVariables().stream().findFirst();
//        if (firstVariable.isPresent()) {
//            List<?> values = firstVariable.get().getValues();
//            if (!values.isEmpty()) {
//                defaultValue.set((String) values.get(0));
//            }
//        }
//
//        Map<Long, PageRowPanel> panelMap = page.getRows().stream()
//            .map(PageRow::getPanels)
//            .flatMap(Collection::stream)
//            .collect(Collectors.toMap(PageRowPanel::getSort, pageRowPanel -> {
//                pageRowPanel.setSrc(pageRowPanel.getSrc() + "&var-Node=" + defaultValue.get());
//                return pageRowPanel;
//            }));
//
//        model.addAttribute("defaultNamespace", defaultValue);
//        model.addAttribute("templating", templating);
//        model.addAttribute("panelMap", panelMap);
        return "monitoring/vm/vm-monitoring";
    }

}
