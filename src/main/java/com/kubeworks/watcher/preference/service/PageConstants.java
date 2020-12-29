package com.kubeworks.watcher.preference.service;

import lombok.experimental.UtilityClass;

// TODO : 모든 url의 정보를 관리하게 끔 변경 예정
@UtilityClass
public class PageConstants {

    public final String API_URL_BY_NAMESPACED_PODS = "/monitoring/cluster/workloads/namespace/{namespace}/pods";
    public final String API_URL_BY_NAMESPACED_DEPLOYMENTS = "/monitoring/cluster/workloads/namespace/{namespace}/deployments";


}
