package com.kubeworks.watcher.cloud.monitoring.controller.cluster;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ClusterController {

    private final ClusterRestController clusterRestController;

    @GetMapping(value = "/monitoring/cluster/nodes", produces = MediaType.TEXT_HTML_VALUE)
    public String nodes(Model model) {
        List<NodeTable> nodes = clusterRestController.nodes();
        model.addAttribute("nodes", nodes);
        return "monitoring/cluster/nodes";
    }

    @GetMapping(value = "/monitoring/cluster/nodes/{nodeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String nodeModal(Model model, @PathVariable String nodeName) {
        NodeDescribe node = clusterRestController.node(nodeName);
        model.addAttribute("node", node);
        return "monitoring/cluster/nodes :: modalContents";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public String workloadsOverview(Model model) {
        // TODO no implementation
        return "monitoring/cluster/workloads/workloads";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/pods", produces = MediaType.APPLICATION_JSON_VALUE)
    public String pods(Model model) {
        List<PodTable> pods = clusterRestController.pods();
        model.addAttribute("pods", pods);
        return "monitoring/cluster/workloads/pods";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/pods/{podName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String pod(Model model, @PathVariable String namespace, @PathVariable String podName) {
        PodDescribe podDescribe = clusterRestController.pod(namespace, podName);
        model.addAttribute("pod", podDescribe);
        return "monitoring/cluster/workloads/pods :: modalContents";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/deployments", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deployments(Model model) {
        List<DeploymentTable> deployments = clusterRestController.deployments();
        model.addAttribute("deployments", deployments);
        return "monitoring/cluster/workloads/deployments";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/daemonsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public String daemonSets(Model model) {
        List<DaemonSetTable> daemonSets = clusterRestController.daemonSets();
        model.addAttribute("daemonSets", daemonSets);
        return "monitoring/cluster/workloads/daemonsets";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/statefulsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public String statefulSets(Model model) {
        List<StatefulSetTable> statefulSets = clusterRestController.statefulSets();
        model.addAttribute("statefulSets", statefulSets);
        return "monitoring/cluster/workloads/statefulsets";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String jobs(Model model) {
        List<JobTable> jobs = clusterRestController.jobs();
        model.addAttribute("jobs", jobs);
        return "monitoring/cluster/workloads/jobs";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/cronjobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String cronJobs(Model model) {
        List<CronJobTable> cronJobs = clusterRestController.cronJobs();
        model.addAttribute("cronJobs", cronJobs);
        return "monitoring/cluster/workloads/cronjobs";
    }

    @GetMapping(value = "/monitoring/cluster/storages", produces = MediaType.APPLICATION_JSON_VALUE)
    public String storages(Model model) {
        Map<String, Object> storages = clusterRestController.storages();
        model.addAllAttributes(storages);
        return "monitoring/cluster/storages";
    }

    @GetMapping(value = "/monitoring/cluster/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public String events(Model model) {
        List<EventTable> events = clusterRestController.events();
        model.addAttribute("events", events);
        return "monitoring/cluster/events";
    }

}
