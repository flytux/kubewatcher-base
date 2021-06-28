package com.kubeworks.watcher.ecosystem;

import com.google.common.collect.ImmutableList;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1Probe;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Objects;
import java.util.regex.Pattern;

public final class YamlHandler {

    /**
     * TODO : Jackson yaml로 변경 여부 체크가 필요함.
     * 참고 : https://github.com/spariev/snakeyaml/blob/master/src/test/java/org/yaml/snakeyaml/issues/issue60/SkipBeanTest.java
     */

    private static final Yaml YAML;
    private static final Pattern LINE = Pattern.compile("\n");
    private static final Pattern SPACE = Pattern.compile(" ", Pattern.LITERAL);

    static {
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        final Representer representer = new Representer() {

            @Override
            protected NodeTuple representJavaBeanProperty(final Object b, final Property p, final Object v, final Tag c) {
                return (v != null) ? super.representJavaBeanProperty(b, p, v, c) : null;
            }
        };

        representer.getPropertyUtils().setSkipMissingProperties(true);
        ImmutableList.of(V1Container.class, V1EnvVar.class, V1VolumeMount.class, V1Probe.class).forEach(e -> representer.addClassTag(e, Tag.MAP));

        YAML = new Yaml(representer, options);
    }

    private YamlHandler() {
        throw new UnsupportedOperationException("Cannot instantiate this class");
    }

    public static String serialize(final Object data) {
        return processSerialization(data);
    }

    public static String serializeIntoHtml(final Object data, final String defaults) {

        final String v = processSerialization(data);

        return Objects.equals(v, "") ? defaults : LINE.matcher(SPACE.matcher(v).replaceAll("&nbsp;")).replaceAll("<br/>");
    }

    private static String processSerialization(final Object data) {
        return Objects.isNull(data) ? "" : YAML.dump(data);
    }
}
