package com.kubeworks.watcher.cloud.monitoring.controller.cluster;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController()
@RequestMapping(path = "/api/v1")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ClusterRestController {

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

    @GetMapping(value = "/monitoring/cluster/nodes", produces = MediaType.TEXT_HTML_VALUE)
    public List<NodeTable> nodes() {
        return nodeService.nodes();
    }

    @GetMapping(value = "/monitoring/cluster/nodes/{nodeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public NodeDescribe node(@PathVariable String nodeName) {
        return nodeService.nodeDescribe(nodeName);
    }

//    @GetMapping(value = "/monitoring/cluster/workloads/overview", produces = MediaType.APPLICATION_JSON_VALUE)

    @GetMapping(value = "/monitoring/cluster/workloads/pods", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PodTable> pods() {
        return podService.allNamespacePodTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/pods/{podName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PodDescribe pod(@PathVariable String namespace, @PathVariable String podName) {
        Optional<PodDescribe> podDescribeOptional = podService.pod(namespace, podName);
        return podDescribeOptional.orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/deployments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DeploymentTable> deployments() {
        return deploymentService.allNamespaceDeploymentTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/deployments/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeploymentDescribe deployment(@PathVariable String namespace, @PathVariable String name) {
        return deploymentService.deployment(namespace, name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/daemonsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DaemonSetTable> daemonSets() {
        return daemonSetService.allNamespaceDaemonSetTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/daemonsets/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DaemonSetDescribe daemonSet(@PathVariable String namespace, @PathVariable String name) {
        return daemonSetService.daemonSet(namespace, name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/statefulsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StatefulSetTable> statefulSets() {
        return statefulSetService.allNamespaceStatefulSetTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/statefulsets/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatefulSetDescribe statefulSet(@PathVariable String namespace, @PathVariable String name) {
        return statefulSetService.statefulSet(namespace, name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JobTable> jobs() {
        return jobService.allNamespaceJobTables();
    }

    @GetMapping(value = "/monitoring/cluster/workloads/namespace/{namespace}/jobs/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JobDescribe job(@PathVariable String namespace, @PathVariable String name) {
        return jobService.job(namespace, name).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/cronjobs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CronJobTable> cronJobs() {
        return cronJobService.allNamespaceCronJobTables();
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

    @GetMapping(value = "/monitoring/cluster/kind/{kind}/namespace/{namespace}/events/{name}/{uId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public V1EventTableList events(@PathVariable String kind, @PathVariable String namespace,
                                   @PathVariable String name, @PathVariable String uId) {
        return eventService.eventTable(kind, namespace, name, uId).orElse(null);
    }

    @GetMapping(value = "/monitoring/cluster/namespace/{namespace}/events/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventDescribe event(@PathVariable String namespace, @PathVariable String name) {
        return eventService.event(namespace, name).orElse(null);
    }

}
