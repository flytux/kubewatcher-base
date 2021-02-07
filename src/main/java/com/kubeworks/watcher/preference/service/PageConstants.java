package com.kubeworks.watcher.preference.service;

import lombok.experimental.UtilityClass;

// TODO : 모든 url의 정보를 관리하게 끔 변경 예정
@UtilityClass
public class PageConstants {

    public final String API_URL_BY_NAMESPACED_PODS = "/monitoring/cluster/workloads/namespace/{namespace}/pods";
    public final String API_URL_BY_NAMESPACED_DEPLOYMENTS = "/monitoring/cluster/workloads/namespace/{namespace}/deployments";
    public final String API_URL_BY_NAMESPACED_DAEMONSETS = "/monitoring/cluster/workloads/namespace/{namespace}/daemonsets";
    public final String API_URL_BY_NAMESPACED_STATEFULSETS = "/monitoring/cluster/workloads/namespace/{namespace}/statefulsets";
    public final String API_URL_BY_NAMESPACED_JOBS = "/monitoring/cluster/workloads/namespace/{namespace}/jobs";
    public final String API_URL_BY_NAMESPACED_CRONJOBS = "/monitoring/cluster/workloads/namespace/{namespace}/cronjobs";
    public final String API_URL_BY_NAMESPACED_PVC = "/monitoring/cluster/namespace/{namespace}/storages";
    public final String API_URL_BY_NAMESPACED_CONFIGMAPS = "/cluster/config/namespace/{namespace}/configmaps";
    public final String API_URL_BY_NAMESPACED_RESOURCEQUOTAS = "/cluster/config/namespace/{namespace}/resource-quotas";
    public final String API_URL_BY_NAMESPACED_HPA = "/cluster/config/namespace/{namespace}/hpa";
    public final String API_URL_BY_NAMESPACED_SERVICES = "/cluster/network/namespace/{namespace}/services";
    public final String API_URL_BY_NAMESPACED_INGRESS = "/cluster/network/namespace/{namespace}/ingress";
    public final String API_URL_BY_NAMESPACED_ENDPOINTS = "/cluster/network/namespace/{namespace}/endpoints";
    public final String API_URL_BY_NAMESPACED_POLICIES = "/cluster/network/namespace/{namespace}/policies";
    public final String API_URL_BY_NAMESPACED_SERVICEACCOUNTS = "/cluster/acl/namespace/{namespace}/service-accounts";
    public final String API_URL_BY_NAMESPACED_ROLEBINDINGS = "/cluster/acl/namespace/{namespace}/role-bindings";
    public final String API_URL_BY_NAMESPACED_ROLES = "/cluster/acl/namespace/{namespace}/roles";
    public final String API_URL_BY_NAMESPACED_SECRETS = "/cluster/config/namespace/{namespace}/secrets";
    public final String API_URL_BY_NAMESPACED_EVENTS = "/monitoring/cluster/namespace/{namespace}/events/contentList";

    public final String API_URL_BY_NAMESPACED_USAGE = "/application/usage/usage-overview/namespace/{namespace}";

}
