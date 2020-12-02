package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.entity.PageRow;
import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.data.entity.PageVariable;
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

    @GetMapping(value = "/monitoring/database", produces = MediaType.TEXT_HTML_VALUE)
    public String database(Model model) {
        Map<String, Object> response = monitoringRestController.database();

        Page page = (Page) response.get("page");
        List<PageRow> pageRows =  page.getRows();

        List dbPanels = new ArrayList();
        pageRows.forEach(pageRow -> {
            List<PageRowPanel> panels = pageRow.getPageRowPanels();
            List<PageRowPanel> dbPanel = new ArrayList<PageRowPanel>();
            int cnt = 0;
            for(PageRowPanel pageRowPanel : panels){
                if (pageRowPanel.getFragmentName().equals("head-card-db-panel")){
                    dbPanel.add(pageRowPanel);

                    cnt++;
                    //DB 로우 하위 판넬 (Status, CPU, Mem) 갯수 변경시 같이 변경해야됨...
                    if (cnt % 3 == 0) {
                        dbPanels.add(dbPanel);
                        dbPanel = new ArrayList<PageRowPanel>();
                    }
                }
            }
        });

        model.addAttribute("dbPanels",dbPanels);
        model.addAllAttributes(response);

        return "monitoring/database/database";
    }


    public Map<Long, PageRowPanel> getPanels(Page page, long pageRowId, PageVariable defaultVariable) {

        return Collections.emptyMap();
    }
}
