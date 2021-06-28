package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.google.common.collect.ImmutableMap;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodLogsService;
import io.kubernetes.client.openapi.ApiClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
public class PodLogsServiceImpl implements PodLogsService  {

    private static final Integer DEFAULT_TAIL_LINE_NUM = 500;

    private final CoreV1ApiExtendHandler handler;

    @Autowired
    public PodLogsServiceImpl(final ApiClient client) {
        this.handler = new CoreV1ApiExtendHandler(client);
    }

    /**
     * @link(https://v1-18.docs.kubernetes.io/docs/reference/generated/kubernetes-api/v1.18/#read-log-pod-v1-core)
     */

    @Override
    public Map<String, String> searchPodLog(final String namespace, final String name, final String container, final String sinceTime) {

        final CoreV1ApiExtendHandler.PodLogParam param = CoreV1ApiExtendHandler.PodLogParam.builder()
            .container(container).sinceTime(sinceTime).tailLines(StringUtils.isEmpty(sinceTime) ? DEFAULT_TAIL_LINE_NUM : null).build();
        final Optional<String> podLogOptional = Optional.ofNullable(handler.searchPodsLog(namespace, name, param));

        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        if (!podLogOptional.isPresent()) {
            return builder.put("log", "").put("lastLogTime", sinceTime).build();
        }

        final String lastLogTime = ZonedDateTime.parse(getLastLogTime(podLogOptional.get())).truncatedTo(ChronoUnit.SECONDS).plusSeconds(1).toString();

        return builder.put("log", podLogOptional.get()).put("lastLogTime", lastLogTime).build();
    }

    private String getLastLogTime(final String logString) {
        return logString.substring(StringUtils.lastIndexOf(logString, "\n", logString.length() - 2) + 1, StringUtils.indexOf(logString, "Z", StringUtils.lastIndexOf(logString, "\n", logString.length() - 2)) + 1);
    }
}
