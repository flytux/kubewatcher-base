package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.IngressTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import io.kubernetes.client.openapi.models.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NetworkingV1beta1IngressTableList extends AbstractNetworkingV1TableList<IngressTable, NetworkingV1beta1Ingress> {

    @Override
    protected IngressTable createInstance() {
        return new IngressTable();
    }

    @Override
    protected void putValueIntoField(final IngressTable builder, final String field, final String value) {
        throw new UnsupportedOperationException("putValueIntoField not supported");
    }

    @Override
    protected void executeExtraProcess(final IngressTable data, final V1ObjectAsTable<NetworkingV1beta1Ingress> row) {
        throw new UnsupportedOperationException("executeExtraProcess not supported");
    }

    @Override
    protected void executeExtraProcessWithMeta(final IngressTable data, final NetworkingV1beta1Ingress e, final V1ObjectMeta meta) {

        if (Objects.nonNull(meta)) {
            data.setName(meta.getName());

            if (Objects.nonNull(meta.getCreationTimestamp())) {
                data.setAge(ExternalConstants.getBetweenPeriodDay(meta.getCreationTimestamp().toInstant().getMillis()));
            }
        }

        final NetworkingV1beta1IngressSpec spec = e.getSpec();
        if (Objects.nonNull(spec)) {
            final List<NetworkingV1beta1IngressRule> rules = spec.getRules();
            if (Objects.nonNull(rules)) {
                final String hosts = rules.stream().map(NetworkingV1beta1IngressRule::getHost).collect(Collectors.joining(", "));
                final String paths = rules.stream().map(NetworkingV1beta1IngressRule::getHttp).filter(Objects::nonNull)
                    .map(h -> h.getPaths().stream().map(NetworkingV1beta1HTTPIngressPath::getPath).filter(Objects::nonNull).collect(Collectors.joining(", "))).collect(Collectors.joining(", "));

                data.setHosts(hosts);
                data.setPaths(paths);
            }
        }
    }
}
