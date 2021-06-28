package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodSecurityPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.PolicyV1beta1PodSecurityPolicy;
import org.joda.time.DateTime;

import java.util.Objects;

public class RbacV1beta1PodSecurityPolicyTableList extends V1ObjectTableList<PodSecurityPolicyTable, PolicyV1beta1PodSecurityPolicy> {

    @Override
    protected PodSecurityPolicyTable createInstance() {
        return new PodSecurityPolicyTable();
    }

    @Override
    protected void putValueIntoField(final PodSecurityPolicyTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "priv" :
                builder.setPrivileged(value); break;
            case "volumes" :
                builder.setVolumes(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final PodSecurityPolicyTable data, final V1ObjectAsTable<PolicyV1beta1PodSecurityPolicy> row) {

        if (Objects.nonNull(row.getObject().getMetadata())) {
            final DateTime dateTime = row.getObject().getMetadata().getCreationTimestamp();
            if (Objects.nonNull(dateTime)) {
                data.setAge(ExternalConstants.getBetweenPeriodDay(dateTime.toInstant().getMillis()));
            }
        }
    }
}
