package com.kubeworks.watcher.cloud.monitoring.controller.cluster;

import com.kubeworks.watcher.cloud.container.controller.config.ConfigRestController;
import com.kubeworks.watcher.cloud.monitoring.controller.MonitoringRestController;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.preference.service.PageConstants;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ClusterController {

    private static final long NODE_MENU_ID = 112;
    private static final long POD_MENU_ID  = 1121;
    private static final long DEPLOYMENT_MENU_ID = 1122;
    private static final long DAEMONSET_MENU_ID = 1123;
    private static final long STATEFULSET_MENU_ID = 1124;
    private static final long JOB_MENU_ID = 1125;
    private static final long CRONJOB_MENU_ID = 1126;
    private static final long STORAGE_MENU_ID = 113;
    private static final long EVENT_MENU_ID = 114;

    private final ClusterRestController clusterRestController;
    private final PageViewService pageViewService;
    private final MonitoringRestController monitoringRestController;
    private final MonitoringProperties monitoringProperties;
    private final ConfigRestController configRestController;

    @GetMapping(value = "/monitoring/cluster/overview", produces = MediaType.TEXT_HTML_VALUE)
    public String clusterOverview(Model model) {
        Map<String, Object> response = monitoringRestController.clusterOverview();
        model.addAllAttributes(response);
        return "monitoring/cluster/overview";
    }

    @GetMapping(value = "/monitoring/cluster/nodes", produces = MediaType.TEXT_HTML_VALUE)
    public String nodes(Model model) {

        List<NodeTable> nodes = clusterRestController.nodes();

        Map<String, Object> response = new HashMap<>();
        Page pageView = pageViewService.getPageView(NODE_MENU_ID);
        response.put("page", pageView);
        response.put("user", getUser());
        response.put("host", monitoringProperties.getDefaultPrometheusUrl());

        model.addAttribute("nodes", nodes);
        model.addAllAttributes(response);

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
        Map<String, Object> response = monitoringRestController.clusterWorkloadsOverview();
        model.addAllAttributes(response);
        return "monitoring/cluster/workloads/workloads";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/pods", produces = MediaType.APPLICATION_JSON_VALUE)
    public String pods(Model model) {

        List<PodTable> pods = clusterRestController.pods();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        Map<String, Object> response = new HashMap<>();
        Page pageView = pageViewService.getPageView(POD_MENU_ID);
        response.put("page", pageView);
        response.put("user", getUser());
        response.put("host", monitoringProperties.getDefaultPrometheusUrl());
        response.put("namespaces", namespaces);
        response.put("link", PageConstants.API_URL_BY_NAMESPACED_PODS);

        model.addAttribute("pods", pods);
        model.addAllAttributes(response);
        return "monitoring/cluster/workloads/pods";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/pods", produces = MediaType.APPLICATION_JSON_VALUE)
    public String pods(Model model, @PathVariable String namespace) {
        List<PodTable> pods = clusterRestController.pods(namespace);
        model.addAttribute("pods", pods);
        return "monitoring/cluster/workloads/pods :: contentList";
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
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("deployments", deployments);
        model.addAttribute("page", pageViewService.getPageView(DEPLOYMENT_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_DEPLOYMENTS);
        return "monitoring/cluster/workloads/deployments";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/deployments", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deployments(Model model, @PathVariable String namespace) {
        List<DeploymentTable> deployments = clusterRestController.deployments(namespace);
        model.addAttribute("deployments", deployments);
        return "monitoring/cluster/workloads/deployments :: contentList";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/deployments/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deployment(Model model, @PathVariable String namespace, @PathVariable String name) {
        DeploymentDescribe deployment = clusterRestController.deployment(namespace, name);
        model.addAttribute("deployment", deployment);
        return "monitoring/cluster/workloads/deployments :: modalContents";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/statefulsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public String statefulSets(Model model) {
        List<StatefulSetTable> statefulSets = clusterRestController.statefulSets();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("statefulSets", statefulSets);
        model.addAttribute("page", pageViewService.getPageView(STATEFULSET_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_STATEFULSETS);
        return "monitoring/cluster/workloads/statefulsets";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/statefulsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public String statefulSets(Model model, @PathVariable String namespace) {
        List<StatefulSetTable> statefulSets = clusterRestController.statefulSets(namespace);
        model.addAttribute("statefulSets", statefulSets);
        return "monitoring/cluster/workloads/statefulsets :: contentList";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/statefulsets/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String statefulSet(Model model, @PathVariable String namespace, @PathVariable String name) {
        StatefulSetDescribe statefulSetDescribe = clusterRestController.statefulSet(namespace, name);
        model.addAttribute("statefulSet", statefulSetDescribe);
        return "monitoring/cluster/workloads/statefulsets :: modalContents";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/daemonsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public String daemonSets(Model model) {
        List<DaemonSetTable> daemonSets = clusterRestController.daemonSets();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("daemonSets", daemonSets);
        model.addAttribute("page", pageViewService.getPageView(DAEMONSET_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_DAEMONSETS);
        return "monitoring/cluster/workloads/daemonsets";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/daemonsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public String daemonSets(Model model, @PathVariable String namespace) {
        List<DaemonSetTable> daemonSets = clusterRestController.daemonSets(namespace);
        model.addAttribute("daemonSets", daemonSets);
        return "monitoring/cluster/workloads/daemonsets :: contentList";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/daemonsets/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String daemonSet(Model model, @PathVariable String namespace, @PathVariable String name) {
        DaemonSetDescribe daemonSetDescribe = clusterRestController.daemonSet(namespace, name);
        model.addAttribute("daemonSet", daemonSetDescribe);
        return "monitoring/cluster/workloads/daemonsets :: modalContents";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String jobs(Model model) {
        List<JobTable> jobs = clusterRestController.jobs();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("jobs", jobs);
        model.addAttribute("page", pageViewService.getPageView(JOB_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_JOBS);
        return "monitoring/cluster/workloads/jobs";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String jobs(Model model, @PathVariable String namespace) {
        List<JobTable> jobs = clusterRestController.jobs(namespace);
        model.addAttribute("jobs", jobs);
        return "monitoring/cluster/workloads/jobs :: contentList";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/jobs/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String job(Model model, @PathVariable String namespace, @PathVariable String name) {
        JobDescribe jobDescribe = clusterRestController.job(namespace, name);
        model.addAttribute("job", jobDescribe);
        return "monitoring/cluster/workloads/jobs :: modalContents";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/cronjobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String cronJobs(Model model) {
        List<CronJobTable> cronJobs = clusterRestController.cronJobs();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("cronJobs", cronJobs);
        model.addAttribute("page", pageViewService.getPageView(CRONJOB_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_CRONJOBS);
        return "monitoring/cluster/workloads/cronjobs";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/cronjobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String cronJobs(Model model, @PathVariable String namespace) {
        List<CronJobTable> cronJobs = clusterRestController.cronJobs(namespace);
        model.addAttribute("cronJobs", cronJobs);
        return "monitoring/cluster/workloads/cronjobs :: contentList";
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/cronjobs/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String cronJob(Model model, @PathVariable String namespace, @PathVariable String name) {
        CronJobDescribe cronJobDescribe = clusterRestController.cronJob(namespace, name);
        model.addAttribute("cronJob", cronJobDescribe);
        return "monitoring/cluster/workloads/cronjobs :: modalContents";
    }

    @GetMapping(value = "/monitoring/cluster/storages", produces = MediaType.APPLICATION_JSON_VALUE)
    public String storages(Model model) {
        Map<String, Object> storages = clusterRestController.storages();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAllAttributes(storages);
        model.addAttribute("page", pageViewService.getPageView(STORAGE_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_PVC);
        return "monitoring/cluster/storages";
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/storages", produces = MediaType.APPLICATION_JSON_VALUE)
    public String storages(Model model, @PathVariable String namespace) {
        Map<String, Object> storages = clusterRestController.storages(namespace);
        model.addAllAttributes(storages);
        return "monitoring/cluster/storages :: contentList";
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/persistent-volume-claims/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String persistentVolumeClaim(Model model, @PathVariable String namespace, @PathVariable String name) {
        PersistentVolumeClaimDescribe persistentVolumeClaimDescribe = clusterRestController.persistentVolumeClaim(namespace, name);
        model.addAttribute("persistentVolumeClaim", persistentVolumeClaimDescribe);
        return "monitoring/cluster/storages :: pvcModalContents";
    }

    @GetMapping(value = "/monitoring/cluster/persistent-volumes/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String persistentVolume(Model model, @PathVariable String name) {
        PersistentVolumeDescribe persistentVolumeDescribe = clusterRestController.persistentVolume(name);
        model.addAttribute("persistentVolume", persistentVolumeDescribe);
        return "monitoring/cluster/storages :: pvModalContents";
    }

    @GetMapping(value = "/monitoring/cluster/storage-classes/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String storageClass(Model model, @PathVariable String name) {
        StorageClassDescribe storageClassDescribe = clusterRestController.storageClass(name);
        model.addAttribute("storageClass", storageClassDescribe);
        return "monitoring/cluster/storages :: storageModalContents";
    }

    @GetMapping(value = "/monitoring/cluster/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public String events(Model model) {
        List<EventTable> events = clusterRestController.events();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("events", events);
        model.addAttribute("page", pageViewService.getPageView(EVENT_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_EVENTS);
        return "monitoring/cluster/events";
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/events/contentList", produces = MediaType.APPLICATION_JSON_VALUE)
    public String events(Model model, @PathVariable String namespace, String contentList) {
        List<EventTable> events = clusterRestController.events(namespace, contentList);
        model.addAttribute("events", events);
        return "monitoring/cluster/events :: contentList";
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/events", produces = MediaType.TEXT_HTML_VALUE)
    public String events(Model model, @PathVariable String namespace) {
        List<EventTable> events = clusterRestController.events(namespace);
        model.addAttribute("events", events);
        return "monitoring/cluster/events :: listContents";
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/events/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String event(Model model, @PathVariable String namespace, @PathVariable String name) {
        EventDescribe event = clusterRestController.event(namespace, name);
        model.addAttribute("event", event);
        return "monitoring/cluster/events :: modalContents";
    }

    protected User getUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

}
