package com.kubeworks.watcher.cloud.container.controller.config;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ConfigMapDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ConfigMapTable;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ConfigController {

    private final ConfigRestController configRestController;


    @GetMapping(value = "/cluster/config/configmaps", produces = MediaType.TEXT_HTML_VALUE)
    public String nodes(Model model) {
        List<ConfigMapTable> configMaps = configRestController.configMaps();
        model.addAttribute("configMaps", configMaps);
        return "cluster/config/configmaps";
    }

    @GetMapping(value = "/cluster/config/configmaps/namespace/{namespace}/configmap/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String configMap(Model model, @PathVariable String namespace, @PathVariable String name) {
        ConfigMapDescribe configMapDescribe = configRestController.configMap(namespace, name);
        model.addAttribute("configMap", configMapDescribe);
        return "cluster/config/configmaps :: modalContents";
    }

}
