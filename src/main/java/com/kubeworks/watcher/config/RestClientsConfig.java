package com.kubeworks.watcher.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.codec.Decoder;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.AIMDBackoffManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultBackoffStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
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

    @SneakyThrows
    @Bean(destroyMethod = "close")
    public CloseableHttpClient httpClient() {

        final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(50);

        return HttpClients.custom()
            .setConnectionManager(connManager)
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .setSSLContext(SSLContextBuilder.create()
                .loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
            .setBackoffManager(new AIMDBackoffManager(connManager))
            .setConnectionBackoffStrategy(new DefaultBackoffStrategy())
            .build();
    }

    @Configuration
    @AllArgsConstructor(onConstructor_ = {@Autowired})
    public static class BaseFeignClientConfig {

        private final ObjectMapper objectMapper;

        @Bean
        public Logger.Level feignLoggerLevel() {
            return Logger.Level.FULL;
        }

        @Bean
        public Decoder feignDecoder() {
            return new ResponseEntityDecoder(new SpringDecoder(() -> new HttpMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))));
        }
    }
}
