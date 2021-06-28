package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import io.kubernetes.client.openapi.models.V1NetworkPolicy;
import io.kubernetes.client.openapi.models.V1NetworkPolicySpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NetworkingV1NetworkPolicyTableList extends AbstractNetworkingV1TableList<NetworkPolicyTable, V1NetworkPolicy> {

    @Override
    protected NetworkPolicyTable createInstance() {
        return new NetworkPolicyTable();
    }

    @Override
    protected void putValueIntoField(final NetworkPolicyTable builder, final String field, final String value) {
        throw new UnsupportedOperationException("putValueIntoField not supported");
    }

    @Override
    protected void executeExtraProcess(final NetworkPolicyTable data, final V1ObjectAsTable<V1NetworkPolicy> row) {
        throw new UnsupportedOperationException("executeExtraProcess not supported");
    }

    @Override
    protected void executeExtraProcessWithMeta(final NetworkPolicyTable data, final V1NetworkPolicy e, final V1ObjectMeta meta) {

        if (Objects.nonNull(meta)) {
            data.setName(meta.getName());

            if (Objects.nonNull(meta.getCreationTimestamp())) {
                data.setAge(ExternalConstants.getBetweenPeriodDay(meta.getCreationTimestamp().toInstant().getMillis()));
            }
        }

        final V1NetworkPolicySpec spec = e.getSpec();
        if (Objects.nonNull(spec)) {
            final List<String> types = spec.getPolicyTypes();
            if (Objects.nonNull(types)) {
                data.setPolicyType(types.stream().map(String::toString).collect(Collectors.joining("/")));
            }
        }
    }
}
