package com.kubeworks.watcher.cloud.usage;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class UsageController {

    private final UsageRestController usageRestController;

    @GetMapping(value = "/application/usage/usage-overview", produces = MediaType.TEXT_HTML_VALUE)
    public String usageOverview(Model model) {
        Map<String, Object> response = usageRestController.usageOverview();
        model.addAllAttributes(response);
        return "application/usage/usage-overview";
    }

    @GetMapping(value = "/application/usage/usage-overview/namespace/{namespace}", produces = MediaType.TEXT_HTML_VALUE)
    public String usageOverview(Model model, @PathVariable String namespace, @RequestParam LocalDate searchDate) {
        Map<String, Object> response = usageRestController.usageOverview(namespace, searchDate);
        model.addAllAttributes(response);
        return "application/usage/usage-overview :: contentList";
    }

}
