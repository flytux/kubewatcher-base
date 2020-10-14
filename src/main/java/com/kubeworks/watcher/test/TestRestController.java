package com.kubeworks.watcher.test;

import com.kubeworks.watcher.ecosystem.grafana.dto.DashboardDetail;
import com.kubeworks.watcher.ecosystem.grafana.service.GrafanaSerivce;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class TestRestController {

    private final GrafanaSerivce grafanaSerivce;
    private final KubernetesClientService kubernetesClientService;
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

    @GetMapping(value = "/grafana/dashboards/{uid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DashboardDetail grafanaDashboardPanels(@PathVariable String uid) {
        return grafanaSerivce.dashboard(uid);
    }

    @GetMapping(value = "/k8s/nodes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NodeTable> nodes() {
        return nodeService.nodes();
    }

    @GetMapping(value = "/k8s/nodes/{nodeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public NodeTable node(@PathVariable String nodeName) {
        return nodeService.node(nodeName);
    }

    @GetMapping(value = "/k8s/nodes/{nodeName}/describe", produces = MediaType.APPLICATION_JSON_VALUE)
    public NodeDescribe nodeDescribe(@PathVariable String nodeName) {
        return nodeService.nodeDescribe(nodeName);
    }

    @GetMapping(value = "/k8s/pod", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PodTable> allPod() {
        return podService.allNamespacePodTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/pod/{podName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PodDescribe pod(@PathVariable String namespace, @PathVariable String podName) {
        Optional<PodDescribe> podDescribeOptional = podService.pod(namespace, podName);
        return podDescribeOptional.orElse(null);
    }

    @GetMapping(value = "/k8s/deployments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DeploymentTable> allDeployments() {
        return deploymentService.allNamespaceDeploymentTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/deployments/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeploymentDescribe allDeployments(@PathVariable String namespace, @PathVariable String name) {
        return deploymentService.deployment(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/daemonsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DaemonSetTable> allDaemonSets() {
        return daemonSetService.allNamespaceDaemonSetTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/daemonsets/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DaemonSetDescribe daemonSet(@PathVariable String namespace, @PathVariable String name) {
        return daemonSetService.daemonSet(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/statefulsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StatefulSetTable> allStatefulSets() {
        return statefulSetService.allNamespaceStatefulSetTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/statefulsets/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatefulSetDescribe statefulSet(@PathVariable String namespace, @PathVariable String name) {
        return statefulSetService.statefulSet(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JobTable> allJobs() {
        return jobService.allNamespaceJobTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/jobs/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JobDescribe job(@PathVariable String namespace, @PathVariable String name) {
        return jobService.job(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/cronjobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CronJobTable> allCronJobs() {
        return cronJobService.allNamespaceCronJobTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/cronjobs/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CronJobDescribe cronJob(@PathVariable String namespace, @PathVariable String name) {
        return cronJobService.cronJob(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/persistent-volumes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersistentVolumeTable> allPersistentVolumeTables() {
        return persistentVolumeService.allPersistentVolumeTables();
    }

    @GetMapping(value = "/k8s/persistent-volumes/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersistentVolumeDescribe persistentVolume(@PathVariable String name) {
        return persistentVolumeService.persistentVolume(name).orElse(null);
    }

    @GetMapping(value = "/k8s/persistent-volume-claims", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersistentVolumeClaimTable> allNamespacePersistentVolumeClaimTables() {
        return persistentVolumeService.allNamespacePersistentVolumeClaimTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/persistent-volume-claims/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PersistentVolumeClaimDescribe persistentVolumeClaim(@PathVariable String namespace, @PathVariable String name) {
        return persistentVolumeService.persistentVolumeClaim(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/storage-classes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StorageClassTable> allStorageClassClaimTables() {
        return storageService.allStorageClassClaimTables();
    }

    @GetMapping(value = "/k8s/storage-classes/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StorageClassDescribe storageClass(@PathVariable String name) {
        return storageService.storageClass(name).orElse(null);
    }

    @GetMapping(value = "/k8s/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventTable> allNamespaceEventTables() {
        return eventService.allNamespaceEventTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/events/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventDescribe event(@PathVariable String namespace, @PathVariable String name) {
        return eventService.event(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/kind/{kind}/namespace/{namespace}/events/{name}/{uId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public V1EventTableList eventTable(@PathVariable String kind, @PathVariable String namespace,
                                       @PathVariable String name, @PathVariable String uId) {
        return eventService.eventTable(kind, namespace, name, uId).orElse(null);
    }
}

