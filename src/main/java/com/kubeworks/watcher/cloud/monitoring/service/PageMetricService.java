package com.kubeworks.watcher.cloud.monitoring.service;

import com.kubeworks.watcher.data.entity.PageRow;
import com.kubeworks.watcher.data.entity.PageRowPanel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface PageMetricService<T> {

    T pageMetrics(long menuId);

    default List<PageRowPanel> setSubGroup(PageRow pageRow) {
        List<PageRowPanel> pageRowPanels = pageRow.getPageRowPanels();
        AtomicInteger subPanelsLastIndex = new AtomicInteger(-1);
        return IntStream.range(0, pageRowPanels.size()).distinct()
            .mapToObj(index -> {
                if (index <= subPanelsLastIndex.get()) {
                    return null;
                }

                PageRowPanel panel = pageRowPanels.get(index);
                if (StringUtils.equalsIgnoreCase(panel.getPanelType(), "PANEL_GROUP")) {
                    for (int i = index + 1; i < pageRowPanels.size(); i++) {
                        PageRowPanel pageRowPanel = pageRowPanels.get(i);
                        if (StringUtils.equalsIgnoreCase(pageRowPanel.getMainYn(), "N")) {
                            panel.addSubPanel(pageRowPanel);
                            subPanelsLastIndex.set(i);
                        } else {
                            break;
                        }
                    }
                }
                return panel;
            }).filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

}
