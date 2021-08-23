package com.kubeworks.watcher.data.converter;

import com.kubeworks.watcher.data.vo.AlertCategory;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class AlertCategoryConverter extends AbstractEnumConverter<AlertCategory, String> {

    public AlertCategoryConverter() {
        super(AlertCategory.class);
    }

}
