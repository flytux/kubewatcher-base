package com.kubeworks.watcher.data.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class ChartQueryVO {

    long cQueryId;

    String apiQuery;

    String legend;

    String queryStep;

}
