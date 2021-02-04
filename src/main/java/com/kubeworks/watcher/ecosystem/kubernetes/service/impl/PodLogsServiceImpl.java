package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodLogsService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
public class PodLogsServiceImpl implements PodLogsService  {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreApi;
    private final Map<String, String> logMap;

    public PodLogsServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
        this.logMap = new HashMap<>();
    }

    /**
     * @link(https://v1-18.docs.kubernetes.io/docs/reference/generated/kubernetes-api/v1.18/#read-log-pod-v1-core)
     */
//    @SneakyThrows
//    @Override
//    public String getPodLog(String podName, String namespace, String container) throws ApiException {
//        String log = coreApi.readNamespacedPodLog(podName, namespace, container,false,false,
//            null, "true",false,null,null, true);
//            return log;
//    }

    @SneakyThrows
    @Override
    public Map<String, String> getPodLog(String podName, String namespace, String container, String sinceTime) throws ApiException {
        Optional<String> aTrue = Optional.ofNullable(coreApi.readNamespacedPodLog(podName, namespace, container, false, false,
            null, "true", false, null, sinceTime, null, true));

        String lastLogTime = "";
        if (aTrue.isPresent()) {
            String s = aTrue.get();
            lastLogTime = s.substring(
                StringUtils.lastIndexOf(s, "\n", s.length() - 2) + 1,
                StringUtils.indexOf(s, "Z", StringUtils.lastIndexOf(s, "\n", s.length() - 2)) + 1
            );

            lastLogTime = ZonedDateTime.parse(lastLogTime)
                .truncatedTo(ChronoUnit.SECONDS)
                .plusSeconds(1)
                .toString();
        } else {
            lastLogTime = sinceTime;
        }

        logMap.put("log", aTrue.orElse(""));
        logMap.put("lastLogTime", lastLogTime);

        return logMap;
    }



}
