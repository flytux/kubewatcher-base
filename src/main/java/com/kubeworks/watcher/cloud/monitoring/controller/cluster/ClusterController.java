package com.kubeworks.watcher.cloud.monitoring.controller.cluster;

import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.service.*;
import com.kubeworks.watcher.preference.service.PageConstants;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor(onConstructor_={@Autowired})
@RequestMapping(value="/monitoring/cluster", produces=MediaType.APPLICATION_JSON_VALUE)
public class ClusterController implements BaseController {

    private static final long JOB_MENU_ID = 1125;
    private static final long NODE_MENU_ID = 112;
    private static final long POD_MENU_ID  = 1121;
    private static final long EVENT_MENU_ID = 114;
    private static final long STORAGE_MENU_ID = 113;
    private static final long CRONJOB_MENU_ID = 1126;
    private static final long DEPLOYMENT_MENU_ID = 1122;
    private static final long DAEMON_SET_MENU_ID = 1123;
    private static final long STATEFUL_SET_MENU_ID = 1124;
    private static final long CLUSTER_OVERVIEW_MENU_ID = 111;
    private static final long CLUSTER_WORKLOADS_OVERVIEW_MENU_ID = 1120;

    private static final String WORKLOADS = "workloads";
    private static final String DEPLOYMENTS = "deployments";
    private static final String WORKLOADS_PREFIX = WORKLOADS + "/";

    private static final String VIEW_NODES = "nodes";
    private static final String VIEW_EVENTS = "events";
    private static final String VIEW_STORAGES = "storages";
    private static final String VIEW_JOBS = WORKLOADS_PREFIX + "jobs";
    private static final String VIEW_PODS = WORKLOADS_PREFIX + "pods";
    private static final String VIEW_CRONJOBS = WORKLOADS_PREFIX + "cronjobs";
    private static final String VIEW_WORKLOADS = WORKLOADS_PREFIX + WORKLOADS;
    private static final String VIEW_DAEMONSETS = WORKLOADS_PREFIX + "daemonsets";
    private static final String VIEW_DEPLOYMENTS = WORKLOADS_PREFIX + DEPLOYMENTS;
    private static final String VIEW_STATEFULSETS = WORKLOADS_PREFIX + "statefulsets";

    private final PageViewService pageViewService;
    private final MonitoringProperties monitoringProperties;

    private final PodService podService;
    private final NodeService nodeService;
    private final NamespaceService namespaceService;
    private final DeploymentService deploymentService;
    private final StatefulSetService statefulSetService;
    private final DaemonSetService daemonSetService;
    private final JobService jobService;
    private final CronJobService cronJobService;
    private final PersistentVolumeService persistentVolumeService;
    private final StorageService storageService;
    private final EventService eventService;

    @GetMapping(value="/overview", produces=MediaType.TEXT_HTML_VALUE)
    public String clusterOverview(final Model model) {

        model.addAttribute(Props.HOST, monitoringProperties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(CLUSTER_OVERVIEW_MENU_ID));

        return createViewName("overview");
    }

    @GetMapping(value="/nodes", produces=MediaType.TEXT_HTML_VALUE)
    public String nodes(final Model model) {

        model.addAttribute(Props.HOST, monitoringProperties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(NODE_MENU_ID));
        model.addAttribute(VIEW_NODES, nodeService.nodes());

        return createViewName(VIEW_NODES);
    }

    @GetMapping(value="/nodes/{nodeName}")
    public String nodeModal(final Model model, @PathVariable final String nodeName) {
        model.addAttribute("node", nodeService.nodeDescribe(nodeName));
        return createViewName(VIEW_NODES, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/workloads/overview")
    public String workloadsOverview(final Model model) {

        model.addAttribute(Props.HOST, monitoringProperties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(CLUSTER_WORKLOADS_OVERVIEW_MENU_ID));

        return createViewName(VIEW_WORKLOADS);
    }

    @GetMapping(value="/workloads/pods")
    public String pods(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.HOST, monitoringProperties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(POD_MENU_ID));
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_PODS);
        model.addAttribute("pods", podService.allNamespacePodTables());

        return createViewName(VIEW_PODS);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/pods")
    public String pods(final Model model, @PathVariable final String namespace) {
        model.addAttribute("pods", podService.podTables(namespace));
        return createViewName(VIEW_PODS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/pods/{podName}")
    public String pod(final Model model, @PathVariable final String namespace, @PathVariable final String podName) {
        model.addAttribute("pod", podService.pod(namespace, podName).orElse(null));
        return createViewName(VIEW_PODS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/workloads/deployments")
    public String deployments(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(DEPLOYMENT_MENU_ID));
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_DEPLOYMENTS);
        model.addAttribute(DEPLOYMENTS, deploymentService.allNamespaceDeploymentTables());

        return createViewName(VIEW_DEPLOYMENTS);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/deployments")
    public String deployments(final Model model, @PathVariable final String namespace) {
        model.addAttribute(DEPLOYMENTS, deploymentService.deployments(namespace));
        return createViewName(VIEW_DEPLOYMENTS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/deployments/{name}")
    public String deployment(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("deployment", deploymentService.deployment(namespace, name).orElse(null));
        return createViewName(VIEW_DEPLOYMENTS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/workloads/statefulsets")
    public String statefulSets(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(STATEFUL_SET_MENU_ID));
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_STATEFULSETS);
        model.addAttribute("statefulSets", statefulSetService.allNamespaceStatefulSetTables());

        return createViewName(VIEW_STATEFULSETS);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/statefulsets")
    public String statefulSets(final Model model, @PathVariable final String namespace) {
        model.addAttribute("statefulSets", statefulSetService.statefulSets(namespace));
        return createViewName(VIEW_STATEFULSETS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/statefulsets/{name}")
    public String statefulSet(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("statefulSet", statefulSetService.statefulSet(namespace, name).orElse(null));
        return createViewName(VIEW_STATEFULSETS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/workloads/daemonsets")
    public String daemonSets(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(DAEMON_SET_MENU_ID));
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_DAEMONSETS);
        model.addAttribute("daemonSets", daemonSetService.allNamespaceDaemonSetTables());

        return createViewName(VIEW_DAEMONSETS);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/daemonsets")
    public String daemonSets(final Model model, @PathVariable final String namespace) {
        model.addAttribute("daemonSets", daemonSetService.daemonSets(namespace));
        return createViewName(VIEW_DAEMONSETS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/daemonsets/{name}")
    public String daemonSet(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("daemonSet", daemonSetService.daemonSet(namespace, name).orElse(null));
        return createViewName(VIEW_DAEMONSETS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/workloads/jobs")
    public String jobs(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(JOB_MENU_ID));
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_JOBS);
        model.addAttribute("jobs", jobService.allNamespaceJobTables());

        return createViewName(VIEW_JOBS);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/jobs")
    public String jobs(final Model model, @PathVariable final String namespace) {
        model.addAttribute("jobs", jobService.jobs(namespace));
        return createViewName(VIEW_JOBS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/jobs/{name}")
    public String job(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("job", jobService.job(namespace, name).orElse(null));
        return createViewName(VIEW_JOBS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/workloads/cronjobs")
    public String cronJobs(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(CRONJOB_MENU_ID));
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_CRONJOBS);
        model.addAttribute("cronJobs", cronJobService.allNamespaceCronJobTables());

        return createViewName(VIEW_CRONJOBS);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/cronjobs")
    public String cronJobs(final Model model, @PathVariable final String namespace) {
        model.addAttribute("cronJobs", cronJobService.cronJobs(namespace));
        return createViewName(VIEW_CRONJOBS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/cronjobs/{name}")
    public String cronJob(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("cronJob", cronJobService.cronJob(namespace, name).orElse(null));
        return createViewName(VIEW_CRONJOBS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/storages")
    public String storages(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(STORAGE_MENU_ID));
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_PVC);
        model.addAttribute(VIEW_STORAGES, storageService.allStorageClassClaimTables());
        model.addAttribute("persistentVolumes", persistentVolumeService.allPersistentVolumeTables());
        model.addAttribute("persistentVolumeClaims", persistentVolumeService.allNamespacePersistentVolumeClaimTables());

        return createViewName(VIEW_STORAGES);
    }

    @GetMapping(value="/namespace/{namespace}/storages")
    public String storages(final Model model, @PathVariable final String namespace) {

        model.addAttribute(VIEW_STORAGES, storageService.allStorageClassClaimTables());
        model.addAttribute("persistentVolumes", persistentVolumeService.allPersistentVolumeTables());
        model.addAttribute("persistentVolumeClaims", persistentVolumeService.persistentVolumeClaims(namespace));

        return createViewName(VIEW_STORAGES, Props.CONTENT_LIST);
    }

    @GetMapping(value="/namespace/{namespace}/persistent-volume-claims/{name}")
    public String persistentVolumeClaim(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("persistentVolumeClaim", persistentVolumeService.persistentVolumeClaim(namespace, name).orElse(null));
        return createViewName(VIEW_STORAGES, " :: pvcModalContents");
    }

    @GetMapping(value="/persistent-volumes/{name}")
    public String persistentVolume(final Model model, @PathVariable final String name) {
        model.addAttribute("persistentVolume", persistentVolumeService.persistentVolume(name).orElse(null));
        return createViewName(VIEW_STORAGES, " :: pvModalContents");
    }

    @GetMapping(value="/storage-classes/{name}")
    public String storageClass(final Model model, @PathVariable final String name) {
        model.addAttribute("storageClass", storageService.storageClass(name).orElse(null));
        return createViewName(VIEW_STORAGES, " :: storageModalContents");
    }

    @GetMapping(value="/events")
    public String events(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(EVENT_MENU_ID));
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_EVENTS);
        model.addAttribute(VIEW_EVENTS, eventService.allNamespaceEventTables());

        return createViewName(VIEW_EVENTS);
    }

    @GetMapping(value="/namespace/{namespace}/events/contentList")
    public String eventsContentList(final Model model, @PathVariable final String namespace) {
        model.addAttribute(VIEW_EVENTS, eventService.events(namespace));
        return createViewName(VIEW_EVENTS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/namespace/{namespace}/events", produces=MediaType.TEXT_HTML_VALUE)
    public String events(final Model model, @PathVariable final String namespace) {
        model.addAttribute(VIEW_EVENTS, eventService.eventTable(null, namespace, null, null).map(V1EventTableList::createDataTableList).orElse(null));
        return createViewName(VIEW_EVENTS, " :: listContents");
    }

    @GetMapping(value="/namespace/{namespace}/events/{name}")
    public String event(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("event", eventService.event(namespace, name).orElse(null));
        return createViewName(VIEW_EVENTS, Props.MODAL_CONTENTS);
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "monitoring/cluster/";
    }
}
