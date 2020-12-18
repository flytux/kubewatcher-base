package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class IngressTable {

    String name;
    String namespace;
    String ingressClass;
    String hosts;
    String paths;
    String address;
    String ports;
    String age;

    public String getPathsHtml() {
        if (StringUtils.isEmpty(paths)) {
            return ExternalConstants.NONE_STR.toLowerCase();
        }
        return String.join("</br>", StringUtils.split(paths, ","));
    }
}
