package com.kubeworks.watcher.config;

import com.kubeworks.watcher.config.properties.MonitoringProperties;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import io.kubernetes.client.util.credentials.Authentication;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.time.Duration;
import java.util.Locale;

@Configuration
@EnableConfigurationProperties(value= MonitoringProperties.class)
public class K8sConfig {

    private static final Long READ_TIMEOUT = 3L;
    private static final Long CONNECT_TIMEOUT = 2L;

    private static final String LOG_PROPERTY = "logging.level.okhttp3";

    private final LogLevel rootLoggingLevel;
    private final MonitoringProperties props;

    @Autowired
    public K8sConfig(final Environment env, final MonitoringProperties props) {
        this.props = props;
        this.rootLoggingLevel = Enum.valueOf(LogLevel.class, env.getProperty(LOG_PROPERTY, "INFO").toUpperCase(Locale.getDefault()));
    }

    @Bean("k8sApiClient")
    public ApiClient createApiClientFromConfig() throws IOException {
        return createApiClient(ClientBuilder.standard(false).setBasePath(props.getDefaultK8sUrl()).setVerifyingSsl(false).setAuthentication(createAuthentication()).build());
    }

    private Authentication createAuthentication() {
        return new AccessTokenAuthentication(props.getDefaultK8sApiToken());
    }

    private Interceptor createInterceptor() {
        return new HttpLoggingInterceptor().setLevel(LogLevel.DEBUG == rootLoggingLevel ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
    }

    private ApiClient createApiClient(final ApiClient client) {
        return client.setHttpClient(client.getHttpClient().newBuilder().addInterceptor(createInterceptor()).connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT)).readTimeout(Duration.ofSeconds(READ_TIMEOUT)).build());
    }
}
