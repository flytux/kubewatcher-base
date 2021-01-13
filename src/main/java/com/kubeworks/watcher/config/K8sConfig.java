package com.kubeworks.watcher.config;

import com.kubeworks.watcher.config.properties.MonitoringProperties;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import io.kubernetes.client.util.credentials.Authentication;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class K8sConfig {

    private final MonitoringProperties monitoringProperties;

    @Value("${logging.level.root}")
    public LogLevel rootLogLevel;

    public K8sConfig(MonitoringProperties monitoringProperties) {
        this.monitoringProperties = monitoringProperties;
    }

    @Bean
    public ApiClient k8sApiClient() throws IOException {
        ApiClient apiClient = ClientBuilder.standard(false)
            .setBasePath(monitoringProperties.getDefaultK8sUrl())
            .setVerifyingSsl(false)
            .setAuthentication(getAuthentication()).build();

        return apiClient.setHttpClient(apiClient.getHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor())
            .connectTimeout(Duration.ofSeconds(2))
            .readTimeout(Duration.ofSeconds(3)).build()
        );
    }

    private Authentication getAuthentication() {
        return new AccessTokenAuthentication(monitoringProperties.getDefaultK8sApiToken());
//        return new AccessTokenAuthentication(new String(Base64.getEncoder().encode(token.getBytes()), StandardCharsets.UTF_8));
    }

    private Interceptor loggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (rootLogLevel == LogLevel.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        return logging;
    }

}
