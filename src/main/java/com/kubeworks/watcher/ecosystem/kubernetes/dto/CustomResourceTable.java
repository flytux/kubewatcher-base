package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class CustomResourceTable implements NamespaceSettable {

    String name;
    String group;
    String version;
    String scope;
    String age;

    @Override
    public void setNamespace(final String namespace) {
        // Do nothing
    }
}
