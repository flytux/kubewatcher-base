package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.ExternalConstants;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.IngressTable;

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
public class NetworkingV1beta1IngressTableList extends V1ObjectTableList<IngressTable, NetworkingV1beta1Ingress> {

    @SerializedName("items")
    private List<NetworkingV1beta1Ingress> items;

    @Override
    protected IngressTable getDataObject() {
        return new IngressTable();
    }

    @Override
    protected void makeObject(IngressTable builder, String fieldName, String value) {
        // no implementation
    }

    @Override
    public List<IngressTable> getDataTable() {
        if (getItems() == null) {
            return Collections.emptyList();
        }

        List<IngressTable> list = new ArrayList<>(getItems().size());
        for (NetworkingV1beta1Ingress rowObject : getItems()) {
            IngressTable data = getDataObject();
            if (rowObject.getMetadata() != null) {
                data.setName(rowObject.getMetadata().getName());
                data.setNamespace(rowObject.getMetadata().getNamespace());
                if (rowObject.getMetadata().getCreationTimestamp() != null) {
                    data.setAge(ExternalConstants.getBetweenPeriodDay(rowObject.getMetadata().getCreationTimestamp().toInstant().getMillis()));
                }
            }
            if (rowObject.getSpec() != null && rowObject.getSpec().getRules() != null) {

                NetworkingV1beta1IngressSpec spec = rowObject.getSpec();
                List<NetworkingV1beta1IngressRule> rules = spec.getRules();
                String hosts = rules.stream()
                    .map(NetworkingV1beta1IngressRule::getHost).collect(Collectors.joining(", "));

                String paths = rules.stream().map(NetworkingV1beta1IngressRule::getHttp)
                    .filter(Objects::nonNull)
                    .map(http -> http.getPaths().stream().map(NetworkingV1beta1HTTPIngressPath::getPath).filter(Objects::nonNull).collect(Collectors.joining(", ")))
                    .collect(Collectors.joining(", "));

                data.setHosts(hosts);
                data.setPaths(paths);

            }

            list.add(data);
        }
        return list;
    }


}
