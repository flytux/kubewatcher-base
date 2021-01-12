package com.kubeworks.watcher.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/*").allowedOrigins("*");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

//        registry.addViewController("").setViewName("redirect:/main");
        registry.addViewController("/").setViewName("redirect:/main");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/main").setViewName("main");

        /* monitoring */
        /*      > application */
        registry.addViewController("/monitoring/application/overview").setViewName("monitoring/application/overview");
        registry.addViewController("/monitoring/application/topologies").setViewName("monitoring/application/topologies");
        /*      > cluster (1) */
//        registry.addViewController("/monitoring/cluster/overview").setViewName("monitoring/cluster/overview");
//        registry.addViewController("/monitoring/cluster/nodes").setViewName("monitoring/cluster/nodes");
        /*         >> workloads */
//        registry.addViewController("/monitoring/cluster/workloads/overview").setViewName("monitoring/cluster/workloads/workloads");
//        registry.addViewController("/monitoring/cluster/workloads/pods").setViewName("monitoring/cluster/workloads/pods");
//        registry.addViewController("/monitoring/cluster/workloads/deployments").setViewName("monitoring/cluster/workloads/deployments");
//        registry.addViewController("/monitoring/cluster/workloads/daemonsets").setViewName("monitoring/cluster/workloads/daemonsets");
//        registry.addViewController("/monitoring/cluster/workloads/statefulsets").setViewName("monitoring/cluster/workloads/statefulsets");
//        registry.addViewController("/monitoring/cluster/workloads/jobs").setViewName("monitoring/cluster/workloads/jobs");
//        registry.addViewController("/monitoring/cluster/workloads/cronjobs").setViewName("monitoring/cluster/workloads/cronjobs");
        /*      > cluster (2) */
//        registry.addViewController("/monitoring/cluster/storages").setViewName("monitoring/cluster/storages");
//        registry.addViewController("/monitoring/cluster/events").setViewName("monitoring/cluster/events");
        /*      > jvm */
//        registry.addViewController("/monitoring/jvm/overview").setViewName("monitoring/jvm/overview");
//        registry.addViewController("/monitoring/jvm/application").setViewName("monitoring/jvm/application");
        /*      > database */
//        registry.addViewController("/monitoring/database").setViewName("monitoring/database/database");
        /*      > vm */
//        registry.addViewController("/monitoring/vm/overview").setViewName("monitoring/vm/vm-overview");
//        registry.addViewController("/monitoring/vm/monitoring").setViewName("monitoring/vm/vm-monitoring");
        /*      > alarm */
        registry.addViewController("/monitoring/alarm/list").setViewName("monitoring/alarm/alarm-list");

        /* application */
        /*      > catalog */
        registry.addViewController("/application/catalog/applications").setViewName("application/catalog/application-list");
        registry.addViewController("/application/catalog/releases").setViewName("application/catalog/release-list");
        /*      > usage */
        registry.addViewController("/application/usage/usage-overview").setViewName("application/usage/usage-overview");

        /* cluster */
        /*      > config */
//        registry.addViewController("/cluster/config/configmaps").setViewName("cluster/config/configmaps");
//        registry.addViewController("/cluster/config/secrets").setViewName("cluster/config/secrets");
//        registry.addViewController("/cluster/config/resource-quotas").setViewName("cluster/config/resource-quotas");
//        registry.addViewController("/cluster/config/hpa").setViewName("cluster/config/hpa");
//        registry.addViewController("/cluster/config/namespaces").setViewName("cluster/config/namespaces");
//        registry.addViewController("/cluster/config/custom-resources").setViewName("cluster/config/custom-resources");
        /*      > network */
        registry.addViewController("/cluster/network/endpoints").setViewName("cluster/network/endpoints");
        registry.addViewController("/cluster/network/ingress").setViewName("cluster/network/ingress");
        registry.addViewController("/cluster/network/services").setViewName("cluster/network/services");
        registry.addViewController("/cluster/network/policies").setViewName("cluster/network/policies");
        /*      > acl */
        registry.addViewController("/cluster/acl/service-accounts").setViewName("cluster/access-control/service-accounts");
        registry.addViewController("/cluster/acl/role-bindings").setViewName("cluster/access-control/role-bindings");
        registry.addViewController("/cluster/acl/roles").setViewName("cluster/access-control/roles");
        registry.addViewController("/cluster/acl/pod-security-policies").setViewName("cluster/access-control/pod-security-policies");

        /* security */
        registry.addViewController("/security/groups").setViewName("security/groups");
        /*      > roles */
        registry.addViewController("/security/roles/user-role-management").setViewName("security/roles/user-role-management");
        registry.addViewController("/security/users").setViewName("security/users");
        registry.addViewController("/security/keys").setViewName("security/keys");

        /* setting */
        /*      > alarm */
        registry.addViewController("/setting/alarm/list").setViewName("setting/alarm/setting-alarm-list");
        registry.addViewController("/setting/preference").setViewName("setting/preference/preference");

        /* ncp계정관리 */
        registry.addViewController("/ncp/audit").setViewName("ncp/audit");
        registry.addViewController("/ncp/authorization").setViewName("ncp/authorization");
        registry.addViewController("/ncp/groups").setViewName("ncp/groups");
        registry.addViewController("/ncp/policies").setViewName("ncp/policies");
        registry.addViewController("/ncp/roles").setViewName("ncp/roles");
        registry.addViewController("/ncp/users").setViewName("ncp/users");

    }
}
