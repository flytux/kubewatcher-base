package com.kubeworks.watcher.data.mapper;

import com.kubeworks.watcher.data.vo.ClusterPodUsage;
import com.kubeworks.watcher.data.vo.UsageMetricType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ClusterPodUsageMapper {

    @Select(
        "SELECT application, namespace, pod_count, max_cpu, avg_cpu, max_memory, avg_memory, create_time\n" +
        "FROM `cluster_pod_usage`"
    )
    List<ClusterPodUsage> selectAll();

    @Insert(
        "<script>\n" +
        "    INSERT INTO `cluster_pod_usage`\n" +
        "        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
        "            <if test=\"application != null\">\n" +
        "                `application`,\n" +
        "            </if>\n" +
        "            <if test=\"namespace != null\">\n" +
        "                `namespace`,\n" +
        "            </if>\n" +
        "            <if test=\"podCount != null\">\n" +
        "                `pod_count`,\n" +
        "            </if>\n" +
        "            <if test=\"maxCpu != null\">\n" +
        "                `max_cpu`,\n" +
        "            </if>\n" +
        "            <if test=\"avgCpu != null\">\n" +
        "                `avg_cpu`,\n" +
        "            </if>\n" +
        "            <if test=\"maxMemory != null\">\n" +
        "                `max_memory`,\n" +
        "            </if>\n" +
        "            <if test=\"avgMemory != null\">\n" +
        "                `avg_memory`,\n" +
        "            </if>\n" +
        "                `create_time`,\n" +
        "        </trim>\n" +
        "    VALUES\n" +
        "        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
        "            <if test=\"application != null\">\n" +
        "                #{application},\n" +
        "            </if>\n" +
        "            <if test=\"namespace != null\">\n" +
        "                #{namespace},\n" +
        "            </if>\n" +
        "            <if test=\"podCount != null\">\n" +
        "                #{podCount},\n" +
        "            </if>\n" +
        "            <if test=\"maxCpu != null\">\n" +
        "                #{maxCpuNumber},\n" +
        "            </if>\n" +
        "            <if test=\"avgCpu != null\">\n" +
        "                #{avgCpuNumber},\n" +
        "            </if>\n" +
        "            <if test=\"maxMemory != null\">\n" +
        "                #{maxMemoryNumber},\n" +
        "            </if>\n" +
        "            <if test=\"avgMemory != null\">\n" +
        "                #{avgMemoryNumber},\n" +
        "            </if>\n" +
        "            <choose>\n" +
        "                <when test=\"createTime != null\">\n" +
        "                    #{createTime},\n" +
        "                </when>\n" +
        "                <otherwise>\n" +
        "                    now(),\n" +
        "                </otherwise>\n" +
        "            </choose>\n" +
        "        </trim>\n" +
        "</script>"
    )
    int insert(ClusterPodUsage clusterPodUsage);

    @Insert(
        "<script>\n" +
        "    INSERT INTO `cluster_pod_usage`\n" +
        "        (\n" +
        "                `application`,\n" +
        "                `namespace`,\n" +
        "                `pod_count`,\n" +
        "                `max_cpu`,\n" +
        "                `avg_cpu`,\n" +
        "                `max_memory`,\n" +
        "                `avg_memory`,\n" +
        "                `create_time`\n" +
        "        )\n" +
        "    VALUES\n" +
        "    <foreach item=\"item\" separator=\",\" collection=\"list\">\n" +
        "        (\n" +
        "            #{item.application},\n" +
        "            #{item.namespace},\n" +
        "            #{item.podCount},\n" +
        "            #{item.maxCpuNumber},\n" +
        "            #{item.avgCpuNumber},\n" +
        "            #{item.maxMemoryNumber},\n" +
        "            #{item.avgMemoryNumber},\n" +
        "            #{item.createTime}\n" +
        "         )\n" +
        "    </foreach>\n" +
        "</script>"
    )
    int inserts(List<ClusterPodUsage> clusterPodUsages);

    @Select(
        "<script>\n" +
        "    SELECT\n" +
        "      application, namespace, \n" +
        "      max(pod_count) as max_pod_count, avg(pod_count) as pod_count,\n" +
        "      max(max_cpu) as max_cpu, round(avg(avg_cpu), 9) as avg_cpu, \n" +
        "      max(max_memory) as max_memory, round(avg(avg_memory), 0) as avg_memory\n" +
        "    FROM `cluster_pod_usage`\n" +
        "    WHERE\n" +
        "    <trim suffixOverrides=\"AND\">\n" +
        "        1=1 AND\n" +
        "        <if test=\"@org.apache.commons.lang3.StringUtils@isNotEmpty(namespace)\" >\n" +
        "            namespace = #{namespace} AND\n" +
        "        </if>\n" +
        "        <if test=\"start != null and end != null\" >\n" +
        "            `create_time` BETWEEN #{start} AND #{end} \n" +
        "        </if>\n" +
        "    </trim>\n" +
        "    GROUP BY namespace, application\n" +
        "    ORDER BY namespace, application\n" +
        "</script>"
    )
    List<ClusterPodUsage> selectSummaryByNamespacePeriod(@Param("namespace") String namespace,
                                                         @Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end);

    @Select(
        "<script>\n" +
        "    SELECT\n" +
        "      application, namespace,\n" +
        "      <choose>\n" +
        "        <when test=\"@org.apache.commons.lang3.StringUtils@equalsIgnoreCase(usageMetricType.name(), 'POD')\">\n" +
        "            max(pod_count) as max_pod_count, avg(pod_count) as pod_count,\n" +
        "        </when>\n" +
        "        <when test=\"@org.apache.commons.lang3.StringUtils@equalsIgnoreCase(usageMetricType.name(), 'CPU')\">\n" +
        "            max(max_cpu) as max_cpu, round(avg(avg_cpu), 9) as avg_cpu,\n" +
        "        </when>\n" +
        "        <when test=\"@org.apache.commons.lang3.StringUtils@equalsIgnoreCase(usageMetricType.name(), 'MEMORY')\">\n" +
        "            max(max_memory) as max_memory, round(avg(avg_memory), 0) as avg_memory,\n" +
        "        </when>\n" +
        "        <otherwise>\n" +
        "            max(pod_count) as max_pod_count, avg(pod_count) as pod_count,          \n" +
        "            max(max_cpu) as max_cpu, round(avg(avg_cpu), 9) as avg_cpu,            \n" +
        "            max(max_memory) as max_memory, round(avg(avg_memory), 0) as avg_memory,\n" +
        "        </otherwise>\n" +
        "      </choose>\n" +
        "      ${groupByFormat} as create_time \n" +
        "    FROM `cluster_pod_usage`\n" +
        "    WHERE\n" +
        "    <trim suffixOverrides=\"AND\">\n" +
        "        1=1 AND\n" +
        "        <if test=\"@org.apache.commons.lang3.StringUtils@isNotEmpty(namespace)\" >\n" +
        "            namespace = #{namespace} AND\n" +
        "        </if>\n" +
        "        <if test=\"@org.apache.commons.lang3.StringUtils@isNotEmpty(application)\" >\n" +
        "            application = #{application} AND\n" +
        "        </if>\n" +
        "        <if test=\"start != null and end != null\" >\n" +
        "            `create_time` BETWEEN #{start} AND #{end} \n" +
        "        </if>\n" +
        "    </trim>\n" +
        "    GROUP BY namespace, application, ${groupByFormat} \n" +
        "    ORDER BY create_time, namespace, application\n" +
        "</script>"
    )
    List<ClusterPodUsage> selectStatisticsSummaryByPeriodGroup(@Param("namespace") String namespace, @Param("application") String application,
                                                               @Param("usageMetricType") UsageMetricType usageMetricType, @Param("groupByFormat") String groupByFormat,
                                                               @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
