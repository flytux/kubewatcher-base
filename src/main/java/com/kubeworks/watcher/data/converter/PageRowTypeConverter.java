package com.kubeworks.watcher.data.converter;

import com.kubeworks.watcher.data.vo.PageRowType;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class PageRowTypeConverter extends AbstractEnumConverter<PageRowType, String> {

    public PageRowTypeConverter() {
        super(PageRowType.class);
    }

}
