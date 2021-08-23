package com.kubeworks.watcher.config;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kubeworks.watcher.ecosystem.kubernetes.ObjectMapperHolder;
import com.kubeworks.watcher.ecosystem.kubernetes.serdes.IntOrStringModule;
import feign.Logger;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableFeignClients(basePackages="com.kubeworks.watcher.ecosystem")
public class ServiceConfig {

    @Bean
    public Logger.Level feignClientLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer() {
        return builder -> builder.modules(new JavaTimeModule(), new JodaModule(), new IntOrStringModule());
    }

    @Configuration
    @AutoConfigureAfter(name="org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration")
    protected static class ObjectMapperAccessConfig {

        protected ObjectMapperAccessConfig(final Jackson2ObjectMapperBuilder builder) {
            ObjectMapperHolder.putObjectMapper(builder.build());
        }
    }

    @Configuration
    @EnableCaching(proxyTargetClass=true)
    protected static class CacheConfig { /* Spring Configuration Class */ }

    @Configuration
    @EnableScheduling
    protected static class SchedulingConfig { /* Spring configuration class */ }

    @Configuration
    @EnableJpaAuditing
    @EntityScan(basePackages="com.kubeworks.watcher.data.entity")
    @EnableJpaRepositories(basePackages="com.kubeworks.watcher.data.repository")
    protected static class JpaConfig { /* Spring configuration class */ }

    @Configuration
    @MapperScan(basePackages={"com.kubeworks.watcher.data.mapper", "com.kubeworks.watcher.config"}, annotationClass= Mapper.class)
    protected static class MybatisConfig { /* Spring configuration class */ }

    @Configuration
    @EnableTransactionManagement
    protected static class TransactionConfig { /* Spring configuration class */ }
}
