package com.kubeworks.watcher.cloud.monitoring.controller.cluster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.base.MetricResponseData;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.service.*;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.*;

@RestController
@AllArgsConstructor(onConstructor_={@Autowired})
@RequestMapping(value="/api/v1/monitoring/cluster")
public class ClusterRestController {

    private static final long NODE_MENU_ID = 112;
    private static final long POD_MENU_ID = 1121;
    private static final long DEPLOYMENT_MENU_ID = 1122;
    private static final long DAEMON_SET_MENU_ID = 1123;
    private static final long STATEFUL_SET_MENU_ID = 1124;
    private static final long STORAGE_MENU_ID = 113;

    private static final String STORAGES_STR = "storages";
    private static final String VIEW_PATH_PREFIX = "monitoring/cluster/";

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

    @GetMapping(value="/nodes", produces=MediaType.TEXT_HTML_VALUE)
    public List<NodeTable> nodes() {
        return nodeService.nodes();
    }

    @GetMapping(value="/nodes/{nodeName}")
    public NodeDescribe node(@PathVariable final String nodeName) {
        return nodeService.nodeDescribe(nodeName);
    }

    @GetMapping(value="/nodes/{nodeName}/metrics")
    public Map<String, Object> nodeMetrics(@PathVariable final String nodeName) {

        final Map<String, Object> node = Maps.newHashMapWithExpectedSize(5);

        node.put("node", nodeService.nodeDescribe(nodeName));
        node.put("page", pageViewService.getPageView(NODE_MENU_ID));

        processTemplateEngine(node, "nodes");

        node.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return node;
    }

    @GetMapping(value="/nodes/{nodeName}/pods/metrics")
    public ApiResponse<MetricResponseData> nodePodCapacityMetric(@PathVariable final String nodeName) {
        return nodeService.podMetricByNode(nodeName);
    }

    @GetMapping(value="/workloads/pods")
    public List<PodTable> pods() {
        return podService.allNamespacePodTables();
    }

    @GetMapping(value="/workloads/namespace/{namespace}/pods")
    public List<PodTable> pods(@PathVariable final String namespace) {
        return podService.podTables(namespace);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/pods/{podName}")
    public PodDescribe pod(@PathVariable final String namespace, @PathVariable final String podName) {
        return podService.pod(namespace, podName).orElse(null);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/pods/{podName}/metrics")
    public Map<String, Object> podMetrics(@PathVariable final String namespace, @PathVariable final String podName) {

        final Map<String, Object> pod = Maps.newHashMapWithExpectedSize(5);

        pod.put("pod", podService.pod(namespace, podName).orElse(null));
        pod.put("page", pageViewService.getPageView(POD_MENU_ID));

        processTemplateEngine(pod, "workloads/pods");

        pod.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return pod;
    }

    @GetMapping(value="/workloads/namespace/{namespace}/pods/{name}/{container}/log")
    public Map<String, Object> podLog(@PathVariable final String namespace, @PathVariable final String name,
            @PathVariable final String container, @RequestParam String sinceTime) {

        final Map<String, Object> pod = new HashMap<>();

        if ("default".equals(container)) {
            final List<String> containerList = podService.containers(namespace, name);

            pod.put("containerList", containerList);
            pod.put("podLog", podLogsService.searchPodLog(namespace, name, containerList.get(0), ""));
        } else {
            pod.put("podLog", podLogsService.searchPodLog(namespace, name, container, sinceTime));
        }

        return pod;
    }

    @GetMapping(value="/workloads/deployments")
    public List<DeploymentTable> deployments() {
        return deploymentService.allNamespaceDeploymentTables();
    }

    @GetMapping(value="/workloads/namespace/{namespace}/deployments")
    public List<DeploymentTable> deployments(@PathVariable final String namespace) {
        return deploymentService.deployments(namespace);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/deployments/{name}")
    public DeploymentDescribe deployment(@PathVariable final String namespace, @PathVariable final String name) {
        return deploymentService.deployment(namespace, name).orElse(null);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/deployments/{name}/metrics")
    public Map<String, Object> deploymentMetrics(@PathVariable final String namespace, @PathVariable final String name) {

        final Map<String, Object> deployment = Maps.newHashMapWithExpectedSize(5);

        deployment.put("deployment", deploymentService.deployment(namespace, name).orElse(null));
        deployment.put("page", pageViewService.getPageView(DEPLOYMENT_MENU_ID));

        processTemplateEngine(deployment, "workloads/deployments");

        deployment.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return deployment;
    }

    @GetMapping(value="/workloads/statefulsets")
    public List<StatefulSetTable> statefulSets() {
        return statefulSetService.allNamespaceStatefulSetTables();
    }

    @GetMapping(value="/workloads/namespace/{namespace}/statefulsets")
    public List<StatefulSetTable> statefulSets(@PathVariable final String namespace) {
        return statefulSetService.statefulSets(namespace);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/statefulsets/{name}")
    public StatefulSetDescribe statefulSet(@PathVariable final String namespace, @PathVariable final String name) {
        return statefulSetService.statefulSet(namespace, name).orElse(null);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/statefulsets/{name}/metrics")
    public Map<String, Object> statefulSetMetrics(@PathVariable final String namespace, @PathVariable final String name) {

        final Map<String, Object> statefulSet = Maps.newHashMapWithExpectedSize(5);

        statefulSet.put("statefulSet", statefulSetService.statefulSet(namespace, name).orElse(null));
        statefulSet.put("page", pageViewService.getPageView(STATEFUL_SET_MENU_ID));

        processTemplateEngine(statefulSet, "workloads/statefulsets");

        statefulSet.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return statefulSet;
    }

    @GetMapping(value="/workloads/daemonsets")
    public List<DaemonSetTable> daemonSets() {
        return daemonSetService.allNamespaceDaemonSetTables();
    }

    @GetMapping(value="/workloads/namespace/{namespace}/daemonsets")
    public List<DaemonSetTable> daemonSets(@PathVariable final String namespace) {
        return daemonSetService.daemonSets(namespace);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/daemonsets/{name}")
    public DaemonSetDescribe daemonSet(@PathVariable final String namespace, @PathVariable final String name) {
        return daemonSetService.daemonSet(namespace, name).orElse(null);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/daemonsets/{name}/metrics")
    public Map<String, Object> daemonSetMetrics(@PathVariable final String namespace, @PathVariable final String name) {

        final Map<String, Object> daemonSet = Maps.newHashMapWithExpectedSize(5);

        daemonSet.put("daemonSet", daemonSetService.daemonSet(namespace, name).orElse(null));
        daemonSet.put("page", pageViewService.getPageView(DAEMON_SET_MENU_ID));

        processTemplateEngine(daemonSet, "workloads/daemonsets");

        daemonSet.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return daemonSet;
    }

    @GetMapping(value="/workloads/jobs")
    public List<JobTable> jobs() {
        return jobService.allNamespaceJobTables();
    }

    @GetMapping(value="/workloads/namespace/{namespace}/jobs")
    public List<JobTable> jobs(@PathVariable final String namespace) {
        return jobService.jobs(namespace);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/jobs/{name}")
    public JobDescribe job(@PathVariable final String namespace, @PathVariable final String name) {
        return jobService.job(namespace, name).orElse(null);
    }

    @GetMapping(value="/workloads/cronjobs")
    public List<CronJobTable> cronJobs() {
        return cronJobService.allNamespaceCronJobTables();
    }

    @GetMapping(value="/workloads/namespace/{namespace}/cronjobs")
    public List<CronJobTable> cronJobs(@PathVariable final String namespace) {
        return cronJobService.cronJobs(namespace);
    }

    @GetMapping(value="/workloads/namespace/{namespace}/cronjobs/{name}")
    public CronJobDescribe cronJob(@PathVariable final String namespace, @PathVariable final String name) {
        return cronJobService.cronJob(namespace, name).orElse(null);
    }

    @GetMapping(value="/storages")
    public Map<String, Object> storages() {

        final Map<String, Object> response = Maps.newHashMapWithExpectedSize(3);

        response.put("persistentVolumeClaims", persistentVolumeService.allNamespacePersistentVolumeClaimTables());
        response.put("persistentVolumes", persistentVolumeService.allPersistentVolumeTables());
        response.put(STORAGES_STR, storageService.allStorageClassClaimTables());

        return response;
    }

    @GetMapping(value="/namespace/{namespace}/storages")
    public Map<String, Object> storages(@PathVariable final String namespace) {

        final Map<String, Object> response = Maps.newHashMapWithExpectedSize(3);

        response.put("persistentVolumeClaims", persistentVolumeService.persistentVolumeClaims(namespace));
        response.put("persistentVolumes", persistentVolumeService.allPersistentVolumeTables());
        response.put(STORAGES_STR, storageService.allStorageClassClaimTables());

        return response;
    }

    @GetMapping(value="/persistent-volumes")
    public List<PersistentVolumeTable> persistentVolumes() {
        return persistentVolumeService.allPersistentVolumeTables();
    }

    @GetMapping(value="/persistent-volumes/{name}")
    public PersistentVolumeDescribe persistentVolume(@PathVariable final String name) {
        return persistentVolumeService.persistentVolume(name).orElse(null);
    }

    @GetMapping(value="/persistent-volume-claims")
    public List<PersistentVolumeClaimTable> persistentVolumeClaims() {
        return persistentVolumeService.allNamespacePersistentVolumeClaimTables();
    }

    @GetMapping(value="/namespace/{namespace}/persistent-volume-claims/{name}")
    public PersistentVolumeClaimDescribe persistentVolumeClaim(@PathVariable final String namespace, @PathVariable final String name) {
        return persistentVolumeService.persistentVolumeClaim(namespace, name).orElse(null);
    }

    @GetMapping(value="/namespace/{namespace}/persistent-volume-claims/{name}/metrics")
    public Map<String, Object> persistentVolumeClaimMetrics(@PathVariable final String namespace, @PathVariable final String name) {

        final Map<String, Object> persistentVolumeClaim = Maps.newHashMapWithExpectedSize(5);

        persistentVolumeClaim.put("persistentVolumeClaim", persistentVolumeService.persistentVolumeClaim(namespace, name).orElse(null));
        persistentVolumeClaim.put("page", pageViewService.getPageView(STORAGE_MENU_ID));

        processTemplateEngine(persistentVolumeClaim, STORAGES_STR, "pvcModalContents");

        persistentVolumeClaim.put("host", monitoringProperties.getDefaultPrometheusUrl());

        return persistentVolumeClaim;
    }

    @GetMapping(value="/storage-classes")
    public List<StorageClassTable> storageClasses() {
        return storageService.allStorageClassClaimTables();
    }

    @GetMapping(value="/storage-classes/{name}")
    public StorageClassDescribe storageClass(@PathVariable final String name) {
        return storageService.storageClass(name).orElse(null);
    }

    @GetMapping(value="/events")
    public List<EventTable> events() {
        return eventService.allNamespaceEventTables();
    }

    @GetMapping(value="/namespace/{namespace}/events/contentList")
    public List<EventTable> eventsContentList(@PathVariable final String namespace) {
        return eventService.events(namespace);
    }

    @GetMapping(value="/events/count")
    public ApiResponse<MetricResponseData> eventCount() {

        final ApiResponse<MetricResponseData> response = new ApiResponse<>();

        try {
            final List<Object> value = ImmutableList.of(System.currentTimeMillis() / 1000, eventService.allNamespaceEventTables().size());
            final MetricResponseData.MetricResult mr = MetricResponseData.MetricResult.builder().value(value).build();

            response.setSuccess(true);
            response.setMessage("");
            response.setData(new MetricResponseData(Collections.singletonList(mr)));
        } catch (final Exception e) {
            response.setSuccess(false);
        }

        return response;
    }

    @GetMapping(value="/component/status/{name}")
    public ApiResponse<MetricResponseData> eventCount(@PathVariable final String name) {
        return componentStatusService.componentStatusMetric(name);
    }

    @GetMapping(value="/namespace/{namespace}/events")
    public List<EventTable> events(@PathVariable final String namespace) {
        return eventService.eventTable(null, namespace, null, null).map(V1EventTableList::createDataTableList).orElse(null);
    }

    @GetMapping(value="/kind/{kind}/namespace/{namespace}/events/{name}/{uId}")
    public V1EventTableList events(@PathVariable final String kind, @PathVariable final String namespace,
                                   @PathVariable final String name, @PathVariable final String uId) {
        return eventService.eventTable(kind, namespace, name, uId).orElse(null);
    }

    @GetMapping(value="/namespace/{namespace}/events/{name}")
    public EventDescribe event(@PathVariable final String namespace, @PathVariable final String name) {
        return eventService.event(namespace, name).orElse(null);
    }

    private void processTemplateEngine(final Map<String, Object> res, final String template) {
        processTemplateEngine(res, template, "modalContents");
    }

    private void processTemplateEngine(final Map<String, Object> res, final String template, final String selector) {
        res.put("describe", springTemplateEngine.process(VIEW_PATH_PREFIX + template, Collections.singleton(selector), new Context(Locale.KOREA, res)));
    }
}
