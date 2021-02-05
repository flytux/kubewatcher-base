package com.kubeworks.watcher.cloud.monitoring.controller.cluster;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.base.MetricResponseData;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.ContainerMetrics;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.service.*;
import com.kubeworks.watcher.preference.service.PageViewService;
import io.kubernetes.client.openapi.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.*;

@RestController()
@RequestMapping(path = "/api/v1")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ClusterRestController {

    private static final long NODE_MENU_ID = 112;
    private static final long POD_MENU_ID = 1121;
    private static final long DEPLOYMENT_MENU_ID = 1122;
    private static final long DAEMONSET_MENU_ID = 1123;
    private static final long STATEFULSET_MENU_ID = 1124;
    private static final long STORAGE_MENU_ID = 113;

    private final NodeService nodeService;
    private final PodService podService;
    private final DeploymentService deploymentService;
    private final DaemonSetService daemonSetService;
    private final StatefulSetService statefulSetService;
    private final JobService jobService;
    private final CronJobService cronJobService;
    private final PersistentVolumeService persistentVolumeService;
    private final StorageService storageService;
    private final EventService eventService;
    private final ComponentStatusService componentStatusService;

    private final PageViewService pageViewService;

    private final SpringTemplateEngine springTemplateEngine;
    private final MonitoringProperties monitoringProperties;

    private final PodLogsService podLogsService;

    @GetMapping(value = "/monitoring/cluster/nodes", produces = MediaType.TEXT_HTML_VALUE)
    public List<NodeTable> nodes() {
        return nodeService.nodes();
    }

    @GetMapping(value = "/monitoring/cluster/nodes/{nodeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public NodeDescribe node(@PathVariable String nodeName) {
        return nodeService.nodeDescribe(nodeName);
    }

    @GetMapping(value = "/monitoring/cluster/nodes/{nodeName}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> nodeMetrics(@PathVariable String nodeName) {

        NodeDescribe nodeDescribe = nodeService.nodeDescribe(nodeName);

        Map<String, Object> node = new HashMap<>();
        node.put("node", nodeDescribe);
        Page pageView = pageViewService.getPageView(NODE_MENU_ID);
        node.put("page", pageView);

        String nodeDescribeHtml = springTemplateEngine.process("monitoring/cluster/nodes",
            Collections.singleton("modalContents"), new Context(Locale.KOREA, node));

        node.put("describe", nodeDescribeHtml);
        node.put("user", getUser());
        node.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return node;
    }

    @GetMapping(value = "/monitoring/cluster/nodes/{nodeName}/pods/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<MetricResponseData> nodePodCapacityMetric(@PathVariable String nodeName) {
        return nodeService.podMetricByNode(nodeName);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/pods", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PodTable> pods() {
        return podService.allNamespacePodTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/pods", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PodTable> pods(@PathVariable String namespace) {
        return podService.podTables(namespace);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/pods/{podName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PodDescribe pod(@PathVariable String namespace, @PathVariable String podName) {
        Optional<PodDescribe> podDescribeOptional = podService.pod(namespace, podName);
        return podDescribeOptional.orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/pods/{podName}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> podMetrics(@PathVariable String namespace, @PathVariable String podName) {

        PodDescribe podDescribe = podService.pod(namespace, podName).orElse(null);;

        Map<String, Object> pod = new HashMap<>();
        pod.put("pod", podDescribe);
        Page pageView = pageViewService.getPageView(POD_MENU_ID);
        pod.put("page", pageView);

        String podDescribeHtml = springTemplateEngine.process("monitoring/cluster/workloads/pods",
            Collections.singleton("modalContents"), new Context(Locale.KOREA, pod));

        pod.put("describe", podDescribeHtml);
        pod.put("user", getUser());
        pod.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return pod;
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/pods/{podName}/{container}/log", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> podLog(@PathVariable String namespace, @PathVariable String podName,
                                     @PathVariable String container, @RequestParam String sinceTime) throws ApiException {

        Map<String, Object> pod = new HashMap<>();
        Map<String, String> log;
        if(container.equals("default")){
            List<String> containerList = podService.containers(namespace, podName);
            log = podLogsService.getPodLog(podName, namespace, containerList.get(0), "");
            pod.put("containerList", containerList);
        } else {
            log = podLogsService.getPodLog(podName, namespace, container, sinceTime);
        }
        pod.put("podLog", log);
        return pod;
    }

    @GetMapping(value = "/monitoring/cluster/workloads/deployments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DeploymentTable> deployments() {
        return deploymentService.allNamespaceDeploymentTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/deployments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DeploymentTable> deployments(@PathVariable String namespace) {
        return deploymentService.deployments(namespace);
    }


    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/deployments/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeploymentDescribe deployment(@PathVariable String namespace, @PathVariable String name) {
        Optional<DeploymentDescribe> DeploymentDescribeOptional = deploymentService.deployment(namespace, name);
        return DeploymentDescribeOptional.orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/deployments/{name}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> deploymentMetrics(@PathVariable String namespace, @PathVariable String name) {

        DeploymentDescribe deploymentDescribe = deploymentService.deployment(namespace, name).orElse(null);

        Map<String, Object> deployment = new HashMap<>();
        deployment.put("deployment", deploymentDescribe);
        Page pageView = pageViewService.getPageView(DEPLOYMENT_MENU_ID);
        deployment.put("page", pageView);

        String deploymentDescribeHtml = springTemplateEngine.process("monitoring/cluster/workloads/deployments",
            Collections.singleton("modalContents"), new Context(Locale.KOREA, deployment));

        deployment.put("describe", deploymentDescribeHtml);
        deployment.put("user", getUser());
        deployment.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return deployment;
    }

    @GetMapping(value = "/monitoring/cluster/workloads/statefulsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StatefulSetTable> statefulSets() {
        return statefulSetService.allNamespaceStatefulSetTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/statefulsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StatefulSetTable> statefulSets(@PathVariable String namespace) {
        return statefulSetService.statefulSets(namespace);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/statefulsets/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatefulSetDescribe statefulSet(@PathVariable String namespace, @PathVariable String name) {
        return statefulSetService.statefulSet(namespace, name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/statefulsets/{name}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> statefulSetMetrics(@PathVariable String namespace, @PathVariable String name) {

        StatefulSetDescribe statefulSetDescribe = statefulSetService.statefulSet(namespace, name).orElse(null);

        Map<String, Object> statefulSet = new HashMap<>();
        statefulSet.put("statefulSet", statefulSetDescribe);
        Page pageView = pageViewService.getPageView(STATEFULSET_MENU_ID);
        statefulSet.put("page", pageView);

        String statefulSetDescribeHtml = springTemplateEngine.process("monitoring/cluster/workloads/statefulsets",
            Collections.singleton("modalContents"), new Context(Locale.KOREA, statefulSet));

        statefulSet.put("describe", statefulSetDescribeHtml);
        statefulSet.put("user", getUser());
        statefulSet.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return statefulSet;
    }

    @GetMapping(value = "/monitoring/cluster/workloads/daemonsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DaemonSetTable> daemonSets() {
        return daemonSetService.allNamespaceDaemonSetTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/daemonsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DaemonSetTable> daemonSets(@PathVariable String namespace) {
        return daemonSetService.daemonSets(namespace);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/daemonsets/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DaemonSetDescribe daemonSet(@PathVariable String namespace, @PathVariable String name) {
        return daemonSetService.daemonSet(namespace, name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/daemonsets/{name}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> daemonSetMetrics(@PathVariable String namespace, @PathVariable String name) {

        DaemonSetDescribe daemonSetDescribe = daemonSetService.daemonSet(namespace, name).orElse(null);

        Map<String, Object> daemonSet = new HashMap<>();
        daemonSet.put("daemonSet", daemonSetDescribe);
        Page pageView = pageViewService.getPageView(DAEMONSET_MENU_ID);
        daemonSet.put("page", pageView);

        String daemonSetDescribeHtml = springTemplateEngine.process("monitoring/cluster/workloads/daemonsets",
            Collections.singleton("modalContents"), new Context(Locale.KOREA, daemonSet));

        daemonSet.put("describe", daemonSetDescribeHtml);
        daemonSet.put("user", getUser());
        daemonSet.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return daemonSet;
    }

    @GetMapping(value = "/monitoring/cluster/workloads/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JobTable> jobs() {
        return jobService.allNamespaceJobTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JobTable> jobs(@PathVariable String namespace) {
        return jobService.jobs(namespace);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/jobs/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JobDescribe job(@PathVariable String namespace, @PathVariable String name) {
        return jobService.job(namespace, name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/cronjobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CronJobTable> cronJobs() {
        return cronJobService.allNamespaceCronJobTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/cronjobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CronJobTable> cronJobs(@PathVariable String namespace) {
        return cronJobService.cronJobs(namespace);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/cronjobs/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CronJobDescribe cronJob(@PathVariable String namespace, @PathVariable String name) {
        return cronJobService.cronJob(namespace, name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/storages", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> storages() {
        List<PersistentVolumeClaimTable> persistentVolumeClaimTables = persistentVolumeClaims();
        List<PersistentVolumeTable> persistentVolumeTables = persistentVolumes();
        List<StorageClassTable> storageClassTables = storageClasses();

        Map<String, Object> response = new HashMap<>(3);
        response.put("persistentVolumeClaims", persistentVolumeClaimTables);
        response.put("persistentVolumes", persistentVolumeTables);
        response.put("storages", storageClassTables);
        return response;
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/storages", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> storages(@PathVariable String namespace) {
        List<PersistentVolumeClaimTable> persistentVolumeClaimTables = persistentVolumeService.persistentVolumeClaims(namespace);
        List<PersistentVolumeTable> persistentVolumeTables = persistentVolumes();
        List<StorageClassTable> storageClassTables = storageClasses();

        Map<String, Object> response = new HashMap<>(3);
        response.put("persistentVolumeClaims", persistentVolumeClaimTables);
        response.put("persistentVolumes", persistentVolumeTables);
        response.put("storages", storageClassTables);
        return response;
    }

    @GetMapping(value = "/monitoring/cluster/persistent-volumes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersistentVolumeTable> persistentVolumes() {
        return persistentVolumeService.allPersistentVolumeTables();
    }

    @GetMapping(value = "/monitoring/cluster/persistent-volumes/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersistentVolumeDescribe persistentVolume(@PathVariable String name) {
        return persistentVolumeService.persistentVolume(name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/persistent-volume-claims", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersistentVolumeClaimTable> persistentVolumeClaims() {
        return persistentVolumeService.allNamespacePersistentVolumeClaimTables();
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/persistent-volume-claims/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersistentVolumeClaimDescribe persistentVolumeClaim(@PathVariable String namespace, @PathVariable String name) {
        return persistentVolumeService.persistentVolumeClaim(namespace, name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/persistent-volume-claims/{name}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> PersistentVolumeClaimMetrics(@PathVariable String namespace, @PathVariable String name) {

        PersistentVolumeClaimDescribe persistentVolumeClaimDescribe = persistentVolumeService.persistentVolumeClaim(namespace, name).orElse(null);

        Map<String, Object> persistentVolumeClaim = new HashMap<>();
        persistentVolumeClaim.put("persistentVolumeClaim", persistentVolumeClaimDescribe);
        Page pageView = pageViewService.getPageView(STORAGE_MENU_ID);
        persistentVolumeClaim.put("page", pageView);

        String persistentVolumeClaimDescribeHtml = springTemplateEngine.process("monitoring/cluster/storages",
            Collections.singleton("pvcModalContents"), new Context(Locale.KOREA, persistentVolumeClaim));

        persistentVolumeClaim.put("describe", persistentVolumeClaimDescribeHtml);
        persistentVolumeClaim.put("user", getUser());
        persistentVolumeClaim.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return persistentVolumeClaim;
    }

    @GetMapping(value = "/monitoring/cluster/storage-classes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StorageClassTable> storageClasses() {
        return storageService.allStorageClassClaimTables();
    }

    @GetMapping(value = "/monitoring/cluster/storage-classes/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StorageClassDescribe storageClass(@PathVariable String name) {
        return storageService.storageClass(name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventTable> events() {
        return eventService.allNamespaceEventTables();
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/events/contentList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventTable> events(@PathVariable String namespace, String contentList) {
        return eventService.events(namespace);
    }

    @GetMapping(value = "/monitoring/cluster/events/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<MetricResponseData> eventCount() {
        ApiResponse<MetricResponseData> response = new ApiResponse<>();
        try {
            List<EventTable> eventTables = eventService.allNamespaceEventTables();
            List<Object> results = Arrays.asList(System.currentTimeMillis() / 1000, eventTables.size());
            response.setSuccess(true);
            response.setMessage("");
            response.setData(new MetricResponseData(Collections.singletonList(MetricResponseData.MetricResult.builder().value(results).build())));
        } catch (Exception e) {
            response.setSuccess(false);
        }
        return response;
    }

    @GetMapping(value = "/monitoring/cluster/component/status/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<MetricResponseData> eventCount(@PathVariable String name) {
        return componentStatusService.componentStatusMetric(name);
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventTable> events(@PathVariable String namespace) {
        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable(null, namespace, null, null);
        return eventTableListOptional.map(V1EventTableList::getDataTable).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/kind/{kind}/namespace/{namespace}/events/{name}/{uId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public V1EventTableList events(@PathVariable String kind, @PathVariable String namespace,
                                   @PathVariable String name, @PathVariable String uId) {
        return eventService.eventTable(kind, namespace, name, uId).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/events/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventDescribe event(@PathVariable String namespace, @PathVariable String name) {
        return eventService.event(namespace, name).orElse(null);
    }

    protected User getUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

}
