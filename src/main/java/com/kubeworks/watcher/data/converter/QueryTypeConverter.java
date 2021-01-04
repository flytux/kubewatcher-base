package com.kubeworks.watcher.data.converter;

import com.kubeworks.watcher.data.vo.QueryType;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class QueryTypeConverter extends AbstractEnumConverter<QueryType, String> {

    public QueryTypeConverter() {
        super(QueryType.class);
    }

}
