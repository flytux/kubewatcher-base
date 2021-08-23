package com.kubeworks.watcher.data.converter;

import com.kubeworks.watcher.data.vo.AlertResource;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class AlertResourceConverter extends AbstractEnumConverter<AlertResource, String> {

    public AlertResourceConverter() {
        super(AlertResource.class);
    }

}
