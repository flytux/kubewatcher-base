package com.kubeworks.watcher.config;

import com.kubeworks.watcher.config.properties.MonitoringProperties;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import io.kubernetes.client.util.credentials.Authentication;
import lombok.AllArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Configuration
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class K8sConfig {

    private final MonitoringProperties monitoringProperties;

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
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

}
