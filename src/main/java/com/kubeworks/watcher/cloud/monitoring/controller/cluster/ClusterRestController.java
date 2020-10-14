package com.kubeworks.watcher.cloud.monitoring.controller.cluster;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NodeDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.service.NodeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path = "/api/v1")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ClusterRestController {

    private final NodeService nodeService;

    @GetMapping(value = "/monitoring/cluster/nodes/{nodeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public NodeDescribe node(@PathVariable String nodeName) {
        return nodeService.nodeDescribe(nodeName);
    }

}
