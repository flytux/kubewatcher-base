package com.kubeworks.watcher.cloud.monitoring.controller.cluster;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NodeDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NodeTable;
import com.kubeworks.watcher.ecosystem.kubernetes.service.NodeService;
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
public class ClusterController {

    private final NodeService nodeService;
    private final ClusterRestController clusterRestController;

    @GetMapping(value = "/monitoring/cluster/nodes", produces = MediaType.TEXT_HTML_VALUE)
    public String nodes(Model model) {
        List<NodeTable> nodes = nodeService.nodes();
        model.addAttribute("nodes", nodes);
        return "monitoring/cluster/nodes";
    }

    @GetMapping(value = "/monitoring/cluster/nodes/{nodeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String nodeModal(Model model, @PathVariable String nodeName) {
        NodeDescribe node = clusterRestController.node(nodeName);
        model.addAttribute("node", node);
        return "monitoring/cluster/nodes :: modalContents";
    }

}
