package com.kubeworks.watcher.data.converter;

import com.kubeworks.watcher.data.vo.AlertSeverity;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class AlertSeverityConverter extends AbstractEnumConverter<AlertSeverity, String> {

    public AlertSeverityConverter() {
        super(AlertSeverity.class);
    }

}
