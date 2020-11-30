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
    private final ConfigMapService configMapService;
    private final SecretService secretService;
    private final ResourceQuotaService resourceQuotaService;
    private final HPAService hpaService;
    private final NamespaceService namespaceService;
    private final LimitRangeService limitRangeService;
    private final CustomResourceService customResourceService;
    private final ServiceKindService serviceKindService;
    private final IngressService ingressService;
    private final EndpointService endpointService;
    private final NetworkPolicyService networkPolicyService;
    private final ServiceAccountService serviceAccountService;
    private final RoleBindingService roleBindingService;


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

    @GetMapping(value = "/k8s/kind/namespace/{namespace}/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventTable> eventTable(@PathVariable String namespace) {
        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable(null, namespace, null, null);
        return eventTableListOptional.map(V1EventTableList::getDataTable).orElse(null);
    }

    @GetMapping(value = "/k8s/configmaps", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConfigMapTable> configMaps() {
        return configMapService.allNamespaceConfigMapTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/configmap/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ConfigMapDescribe configMap(@PathVariable String namespace, @PathVariable String name) {
        return configMapService.configMap(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/secrets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SecretTable> secrets() {
        return secretService.allNamespaceSecretTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/secrets/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SecretDescribe secret(@PathVariable String namespace, @PathVariable String name) {
        return secretService.secret(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/resource-quotas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ResourceQuotaTable> resourceQuotas() {
        return resourceQuotaService.allNamespaceResourceQuotaTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/resource-quotas/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResourceQuotaDescribe resourceQuota(@PathVariable String namespace, @PathVariable String name) {
        return resourceQuotaService.resourceQuota(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/hpa/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HPATable> hpaList() {
        return hpaService.allNamespaceHPATables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/hpa/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HPADescribe hpa(@PathVariable String namespace, @PathVariable String name) {
        return hpaService.hpa(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/limit-ranges", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LimitRangeTable> limitRanges() {
        return limitRangeService.allNamespaceLimitRangeTables();
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/limit-ranges", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LimitRangeDescribe> limitRanges(@PathVariable String namespace) {
        return limitRangeService.listNamespacedLimitRange(namespace);
    }

    @GetMapping(value = "/k8s/namespace/{namespace}/limit-ranges/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LimitRangeDescribe limitRange(@PathVariable String namespace, @PathVariable String name) {
        return limitRangeService.limitRange(namespace, name).orElse(null);
    }

    @GetMapping(value = "/k8s/namespaces", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NamespaceTable> namespaces() {
        return namespaceService.allNamespaceTables();
    }

    @GetMapping(value = "/k8s/namespaces/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public NamespaceDescribe namespace(@PathVariable String name) {
        return namespaceService.namespace(name).orElse(null);
    }

    @GetMapping(value = "/k8s/custom-resources", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomResourceTable> customResources() {
        return customResourceService.allCustomResourceTables();
    }

    @GetMapping(value = "/k8s/custom-resources/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomResourceDescribe customResource(@PathVariable String name) {
        return customResourceService.customResource(name).orElse(null);
    }

    @GetMapping(value = "/k8s/services", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ServiceTable> services() {
        return serviceKindService.allNamespaceServiceTables();
    }

    @GetMapping(value = "/k8s/ingresses", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IngressTable> ingresses() {
        return ingressService.allNamespaceIngressTables();
    }

    @GetMapping(value = "/k8s/endpoints", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EndpointTable> endpoints() {
        return endpointService.allNamespaceEndpointTables();
    }

    @GetMapping(value = "/k8s/network-policies", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NetworkPolicyTable> networkPolicies() {
        return networkPolicyService.allNamespaceNetworkPolicyTables();
    }

    @GetMapping(value = "/k8s/service-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ServiceAccountTable> serviceAccounts() {
        return serviceAccountService.allNamespaceServiceAccountTables();
    }

    @GetMapping(value = "/k8s/role-bindings", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoleBindingTable> roleBindings() {
        return roleBindingService.allNamespaceRoleBindingTables();
    }


}

