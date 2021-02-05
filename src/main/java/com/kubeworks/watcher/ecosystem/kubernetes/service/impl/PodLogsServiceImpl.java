package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.google.common.collect.ImmutableMap;
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
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
public class PodLogsServiceImpl implements PodLogsService  {

    private static final Integer DEFAULT_TAIL_LINE_NUM = 500;

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreApi;

    public PodLogsServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
    }

    /**
     * @link(https://v1-18.docs.kubernetes.io/docs/reference/generated/kubernetes-api/v1.18/#read-log-pod-v1-core)
     */

    @SneakyThrows
    @Override
    public Map<String, String> getPodLog(String podName, String namespace, String container, String sinceTime) throws ApiException {

        Integer tailLines = StringUtils.isEmpty(sinceTime) ? DEFAULT_TAIL_LINE_NUM : null;

        Optional<String> podLogOptional = Optional.ofNullable(coreApi.readNamespacedPodLog(podName, namespace, container, false, false,
            null, "true", false, null, sinceTime, tailLines, true));

        ImmutableMap.Builder<String, String> responseBuilder = ImmutableMap.builder();
        if (!podLogOptional.isPresent()) {
            return responseBuilder
                .put("log", "")
                .put("lastLogTime", sinceTime)
                .build();
        }

        String logString = podLogOptional.get();
        String lastLogTime = getLastLogTime(logString);
        lastLogTime = ZonedDateTime.parse(lastLogTime).truncatedTo(ChronoUnit.SECONDS)
            .plusSeconds(1).toString();

        return responseBuilder
            .put("log", podLogOptional.get())
            .put("lastLogTime", lastLogTime)
            .build();
    }

    private String getLastLogTime(String logString) {
        return logString.substring(
            StringUtils.lastIndexOf(logString, "\n", logString.length() - 2) + 1,
            StringUtils.indexOf(logString, "Z", StringUtils.lastIndexOf(logString, "\n", logString.length() - 2)) + 1
        );
    }


}
