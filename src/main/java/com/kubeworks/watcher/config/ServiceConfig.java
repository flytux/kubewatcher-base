package com.kubeworks.watcher.config;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kubeworks.watcher.ecosystem.kubernetes.serdes.IntOrStringModule;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1Probe;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

@Configuration
public class ServiceConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
            .modules(new JavaTimeModule(), new JodaModule(), new IntOrStringModule());
    }

    /**
     * 참고 : https://github.com/spariev/snakeyaml/blob/master/src/test/java/org/yaml/snakeyaml/issues/issue60/SkipBeanTest.java
     * TODO : Jackson yaml로 변경 여부 체크가 필요함.
     * @return Yaml
     */
    @Bean
    public Yaml yaml() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new SkipNullRepresenter();
        representer.addClassTag(V1Container.class, Tag.MAP);
        representer.addClassTag(V1EnvVar.class, Tag.MAP);
        representer.addClassTag(V1VolumeMount.class, Tag.MAP);
        representer.addClassTag(V1Probe.class, Tag.MAP);
        representer.getPropertyUtils()
            .setSkipMissingProperties(true);
        return new Yaml(representer, dumperOptions);
    }

    private static class SkipNullRepresenter extends Representer {
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
                                                      Object propertyValue, Tag customTag) {
            if (propertyValue == null) {
                return null;
            } else {
                return super
                    .representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        }
    }



}
