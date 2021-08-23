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

        /*To-Do
        Application.Yaml에 설정값을 이용하여 Rancher Cluster에 접속하는 경우 아래 로직을 이용하고
        클러스터 내부에 배포될 때는 아래 InCluster 로직을 이용하도록 수정 필요
         */
        //ApiClient apiClient = ClientBuilder.standard(false)
        //  .setBasePath(monitoringProperties.getDefaultK8sUrl())
        //    .setVerifyingSsl(false)
        //    .setAuthentication(getAuthentication()).build();

        /* InCluster 배포시 ApiServer 접속
           Namespace 내 default serviceaccount에 권한 부여 필요
         */
        ApiClient apiClient = io.kubernetes.client.util.Config.defaultClient();
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(apiClient);

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
