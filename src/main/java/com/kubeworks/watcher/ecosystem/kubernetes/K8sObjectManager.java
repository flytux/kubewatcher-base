package com.kubeworks.watcher.ecosystem.kubernetes;

import com.kubeworks.watcher.config.properties.ApplicationServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class K8sObjectManager {

    private final ApplicationServiceProperties applicationServiceProperties;

    public int compareByNamespace(String namespace1, String namespace2) {
        int index1 = applicationServiceProperties.getNamespaces().indexOf(namespace1);
        int index2 = applicationServiceProperties.getNamespaces().indexOf(namespace2);
        if (index1 >= 0) {
            if (index2 >= 0) {
                return index1 - index2;
            }
            return Integer.MIN_VALUE;
        }
        if (index2 >= 0) {
            return Integer.MAX_VALUE;
        }
        return namespace1.compareToIgnoreCase(namespace2);
    }

}
