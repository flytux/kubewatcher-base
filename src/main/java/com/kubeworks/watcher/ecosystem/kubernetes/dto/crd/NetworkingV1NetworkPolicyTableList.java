package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.ExternalConstants;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyTable;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;

import io.kubernetes.client.openapi.models.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NetworkingV1NetworkPolicyTableList extends V1ObjectTableList<NetworkPolicyTable, V1NetworkPolicy> {

    @SerializedName("items")
    private List<V1NetworkPolicy> items;

    @Override
    protected NetworkPolicyTable getDataObject() {
        return new NetworkPolicyTable();
    }

    @Override
    protected void makeObject(NetworkPolicyTable builder, String fieldName, String value) {
        // no implementation
    }

    @Override
    public List<NetworkPolicyTable> getDataTable() {
        if (getItems() == null) {
            return Collections.emptyList();
        }

        List<NetworkPolicyTable> list = new ArrayList<>(getItems().size());
        for (V1NetworkPolicy rowObject : getItems()) {
            NetworkPolicyTable data = getDataObject();
            if (rowObject.getMetadata() != null) {
                data.setName(rowObject.getMetadata().getName());
                data.setNamespace(rowObject.getMetadata().getNamespace());
                if (rowObject.getMetadata().getCreationTimestamp() != null) {
                    data.setAge(ExternalConstants.getBetweenPeriodDay(rowObject.getMetadata().getCreationTimestamp().toInstant().getMillis()));
                }
            }
            if (rowObject.getSpec() != null) {
                V1NetworkPolicySpec spec = rowObject.getSpec();
                List<String> policyTypes = spec.getPolicyTypes();

                String type = policyTypes.stream().map(String::toString)
                    .collect(Collectors.joining("/"));

                data.setPolicyType(type);

            }

            list.add(data);
        }

        return list;
    }
}
