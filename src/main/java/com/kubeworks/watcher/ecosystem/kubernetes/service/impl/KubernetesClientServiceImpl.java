package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.V1NodeDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1NodeTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.KubernetesClientService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1EventList;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1PodList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class KubernetesClientServiceImpl implements KubernetesClientService {

    private final CoreV1ApiExtendHandler coreApi;

    @Autowired
    public KubernetesClientServiceImpl(ApiClient k8sApiClient) {
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
    }

    @Deprecated
    @SneakyThrows
    @Override
    public V1NodeTableList nodesTable() {
        ApiResponse<V1NodeTableList> response = coreApi.listNodeAsTable("true");
        V1NodeTableList nodeList = response.getData();
        nodeList.getRows().forEach(v1Node -> {
            log.info("node={}", v1Node.getCells().get(0));
        });
        return nodeList;
    }


    @Deprecated
    @SneakyThrows
    @Override
    public V1NodeDescribe describeNode(String nodeName) {

        V1Node v1Node1 = coreApi.readNode(nodeName, "true", Boolean.TRUE, Boolean.TRUE);

        String nodeFieldSelector = "spec.nodeName=" + nodeName;
        V1PodList v1PodList = coreApi.listPodForAllNamespaces(null, null, nodeFieldSelector,
            null, 500, "true", null, 0, Boolean.FALSE);

        String eventFieldSelector = String.join(",", Arrays.asList("involvedObject.namespace=", "involvedObject.kind=Node"
            , "involvedObject.uid=" + nodeName, "involvedObject.name=" + nodeName));

        V1EventList v1EventList = coreApi.listEventForAllNamespaces(null, null, eventFieldSelector,
            null, 500, "true", null, 0, Boolean.FALSE);

        V1NodeDescribe nodeDescribe = V1NodeDescribe.builder().node(v1Node1).pods(v1PodList).events(v1EventList).build();

        log.info("cpu request={}", nodeDescribe.requestCpu());

        return nodeDescribe;
    }



    @SneakyThrows
    private V1NamespaceList getAllNameSpaces() {
        V1NamespaceList namespaceList = coreApi.listNamespace("true", null, null, null, null, 0, null, 3, Boolean.FALSE);
        namespaceList.getItems().forEach(v1Namespace -> {
            log.info("namespace={}", v1Namespace.getMetadata().getName());
        });
        return namespaceList;
    }


    @SneakyThrows
    private V1PodList getAllPods() {
        V1PodList podList = coreApi.listPodForAllNamespaces(null, null,
            "spec.nodeName=v3", null, null, null,
            null, null, null);
        podList.getItems().forEach(v1Pod -> log.info("pod={}, nodeName={}", v1Pod.getMetadata().getName(), v1Pod.getSpec().getNodeName()));
        return podList;
    }
}
