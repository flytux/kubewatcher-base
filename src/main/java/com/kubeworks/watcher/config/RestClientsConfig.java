package com.kubeworks.watcher.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubeworks.watcher.config.properties.GrafanaProperties;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import lombok.AllArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.AIMDBackoffManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultBackoffStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class RestClientsConfig {



    @Bean
    public RestTemplateCustomizer restTemplateCustomizer(HttpClient httpClient) {
        return restTemplate -> {
            final ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
            if (requestFactory instanceof HttpComponentsClientHttpRequestFactory) {
                final HttpComponentsClientHttpRequestFactory factory = (HttpComponentsClientHttpRequestFactory) requestFactory;
                factory.setHttpClient(httpClient);
                factory.setConnectTimeout(3000);
                factory.setReadTimeout(3000);
            }
        };
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(50);
        return HttpClientBuilder.create()
            .setConnectionManager(connManager)
            .setBackoffManager(new AIMDBackoffManager(connManager))
            .setConnectionBackoffStrategy(new DefaultBackoffStrategy())
            .build();
    }

    @Configuration
    @AllArgsConstructor(onConstructor_ = {@Autowired})
    public static class BaseFeignClientConfig {

        private final ObjectMapper objectMapper;

        @Bean
        public Decoder feignDecoder() {
            return new ResponseEntityDecoder(
                new SpringDecoder(() ->
                    new HttpMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))));
        }

    }

    @Configuration
    public static class GrafanaFeignClientConfig extends BaseFeignClientConfig {

        private final GrafanaProperties grafanaProperties;

        public GrafanaFeignClientConfig(ObjectMapper objectMapper, GrafanaProperties grafanaProperties) {
            super(objectMapper);
            this.grafanaProperties = grafanaProperties;
        }

        @Bean
        @Override
        public Decoder feignDecoder() {
            return super.feignDecoder();
        }

        @Bean
        public RequestInterceptor grafanaRequestInterceptor() {
            return request -> request.header("Authorization", "Bearer " + grafanaProperties.getApiToken());
        }

    }

}
