package com.kubeworks.watcher.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import io.kubernetes.client.util.credentials.Authentication;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class K8sConfig {

    @Bean
    public ApiClient k8sApiClient() throws IOException {
        ApiClient apiClient = ClientBuilder.standard(false)
            .setBasePath("https://rancher.localhost.song/k8s/clusters/c-x4l59")
            .setVerifyingSsl(false)
            .setAuthentication(getAuthentication()).build();

        return apiClient.setHttpClient(apiClient.getHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor())
            .readTimeout(Duration.ofSeconds(3)).build()
        );
    }

    private Authentication getAuthentication() {
        String token = "kubeconfig-user-48zzt:jbzlv598x6xjx2n66dvmlbqlfzsxvnnhfrkmkmbt9k7hkg9tmhfwl9";
        return new AccessTokenAuthentication(token);
//        return new AccessTokenAuthentication(new String(Base64.getEncoder().encode(token.getBytes()), StandardCharsets.UTF_8));
    }

    private Interceptor loggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

}
